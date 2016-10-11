package io.caboose.protobuf;

import com.google.inject.Inject;
import org.reflections.Reflections;
import play.Logger;
import play.inject.Injector;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.Router;
import play.routing.RoutingDsl;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Internal class used for dynamically routing based on package, class, and function name
 */
public class ProtobufRouter extends Controller {
    private final Injector injector;
    private final Map<String, Class<?>> classMap = new HashMap<>();
    private final Map<String, Method> methodMap = new HashMap<>();
    private final Map<String, Method> parseMethodMap = new HashMap<>();
    @Inject
    public ProtobufRouter(Injector injector){
        this.injector = injector;
        init();

    }

    private void init(){
        Reflections reflections = new Reflections();
        reflections.getSubTypesOf(Controller.class).forEach(clazz -> {
            String className = clazz.getName();
            List<Method> methods = getMethods(clazz);
            classMap.put(className, clazz);
            methods.forEach(method -> {
                methodMap.put(className + "/" + method.getName(), method);
                initParseMethod(className, method);
            });
            ProtobufService.Service annotation = clazz.getAnnotation(ProtobufService.Service.class);
            if (annotation != null){
                String pseudoClassName = annotation.packageName() + "." +  clazz.getSimpleName() ;
                classMap.put(pseudoClassName, clazz);
                methods.forEach(method -> {
                    methodMap.put(pseudoClassName + "/" + method.getName(), method);
                    initParseMethod(pseudoClassName, method);
                });
            }
        });
    }

    private void initParseMethod(String className, Method method){
        try {
            Method protobufParseMethod = getProtobufParseMethod(getProtobufClass(method));
            parseMethodMap.put(className + "/" + method.getName(), protobufParseMethod);
        } catch (NoSuchMethodException e) {
        }

    }

    private List<Method> getMethods(Class<?> clazz){
        return Arrays.stream(clazz.getMethods()).filter(method -> {
            ProtobufService.Input annotation = method.getAnnotation(ProtobufService.Input.class);
            return annotation != null;
        }).collect(Collectors.toList());
    }

    private Method getMethod(String className, String function) throws NoSuchMethodException{
        String methodName = className + "/" + function;
        return Optional.ofNullable(methodMap.get(methodName))
                .orElseThrow(() -> new NoSuchMethodException("Invalid method " + methodName));
    }
    private Method getParseMethod(String className, String function) throws NoSuchMethodException{
        String methodName = className + "/" + function;
        return Optional.ofNullable(parseMethodMap.get(methodName))
                .orElseThrow(() -> new NoSuchMethodException("Invalid parse method " + methodName));
    }



    public Router getRouter() {
        return new RoutingDsl().POST("/:class/:function").routeTo((String className, String function) -> {
            try {
                Class<?> clazz = classMap.get(className);
                if (clazz == null){
                    throw new ClassNotFoundException("Invalid class " + className);
                }
                Object o = injector.instanceOf(clazz);
                Method method = getMethod(className, function);
                Method protobufParseMethod = getParseMethod(className, function);
                Object input = protobufParseMethod.invoke(null, request().body().asBytes().toArray());
                Result response = (Result)method.invoke(o, input);
                return response;
            } catch (ClassNotFoundException | NoSuchMethodException
                    | IllegalAccessException e) {
                Logger.error("Error routing", e);
                return badRequest("Route not found /" + className + "/" + function);
            } catch (InvocationTargetException e) {
                if (e.getCause() != null){
                    Logger.error("Error parsing in route", e.getCause());
                    return badRequest("Invalid input for route /" + className + "/" + function);
                } else {
                    Logger.error("Error routing", e);
                    return badRequest("Route not found /" + className + "/" + function);
                }
            }
        }).build();
    }


    protected Class<?> getProtobufClass(Method method){
        ProtobufService.Output annotation = method.getAnnotation(ProtobufService.Output.class);
        return annotation.value();
    }

    protected Method getProtobufParseMethod(Class<?> protobufClass) throws NoSuchMethodException {
        Class[] parameterTypes = new Class[]{byte[].class};
        return protobufClass.getMethod("parseFrom", parameterTypes);
    }

}
