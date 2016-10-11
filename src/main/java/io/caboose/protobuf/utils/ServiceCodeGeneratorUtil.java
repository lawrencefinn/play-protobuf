package io.caboose.protobuf.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.caboose.protobuf.ProtobufService;
import io.caboose.protobuf.models.RPCModel;
import io.caboose.protobuf.models.ServiceModel;
import play.mvc.Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility to generate controllers and parsers from proto files, routes from proto files, and proto files from controllers
 *
 */
public class ServiceCodeGeneratorUtil {
    final MustacheFactory mf;
    final Mustache mustache;
    final Mustache reverseMustache ;
    final Mustache parserMustache;
    final Mustache routeMustache;


    public ServiceCodeGeneratorUtil() {
        mf = new DefaultMustacheFactory(".");
        mustache = mf.compile("service.template");
        reverseMustache = mf.compile("reverseservice.template");
        parserMustache = mf.compile("parser.template");
        routeMustache = mf.compile("routes.template");
    }


    /**
     * Generate controllers / parsers from proto files
     * @param inputPath input proto file describing services GRPC style
     * @param outputPath main directory to output controllers / parsers to.  Controllers will utilize java package to
     *                   build the full path added to this parameter
     * @param generateParsers boolean to generate parser
     * @throws IOException
     */
    public void generateClassFiles(String inputPath, String outputPath, boolean generateParsers) throws IOException {
        File file = new File(inputPath);
        Stream<String> lines = Files.lines(Paths.get(file.getPath()));
        ServiceParserUtil serviceParser = new ServiceParserUtil(lines);
        List<ServiceModel> services = serviceParser.getServicesDefintion();
        Set<String> generatedParsers = new HashSet<>();
        services.forEach(service -> {
            try {
                String path = outputPath + "/" +
                        Optional.of(service.getJavaPackage()).orElse(service.getPseudoPackage()).orElse("")
                                .replaceAll("\\.", "/") ;
                boolean mkdir = new File(path + "/controllers").mkdirs();
                File outFile = new File(path + "/controllers/" + service.getName() + ".java");
                try(FileWriter writer = new FileWriter(outFile)){
                    mustache.execute(writer, service).flush();
                }
                if (generateParsers){
                    mkdir = new File(path + "/parsers").mkdirs();
                    service.getFunctions().forEach(func -> {
                        if (!generatedParsers.contains(func.getInputShortName())) {
                            func.setJavaPackage(service.getJavaPackage());
                            File outFuncFile = new File(path + "/parsers/" + func.getInputShortName() + "Parser.java");
                            try (FileWriter funcWriter = new FileWriter(outFuncFile)) {
                                parserMustache.execute(funcWriter, func).flush();
                                generatedParsers.add(func.getInputShortName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Generate the play routes file
     * @param inputPath proto file
     * @param outputPathWithFilename full path with filename to output routes to
     * @throws IOException
     */
    public void generateRoutesFiles(String inputPath, String outputPathWithFilename) throws IOException {
        File file = new File(inputPath);
        Stream<String> lines = Files.lines(Paths.get(file.getPath()));
        ServiceParserUtil serviceParser = new ServiceParserUtil(lines);
        List<ServiceModel> servicesDefintion = serviceParser.getServicesDefintion();
        servicesDefintion.forEach(service -> {
            service.getFunctions().forEach(func -> {
                func.setJavaPackage(service.getJavaPackage());
            });
        });
        ServicesWrapperModel services = new ServicesWrapperModel(servicesDefintion);
        File outFile = new File(outputPathWithFilename);
        try(FileWriter writer = new FileWriter(outFile)){
            routeMustache.execute(writer, services).flush();
        }

    }

    /**
     * Generate proto file from controller class.  Useful for making changes in code and exposing .proto to others
     * @param clazz controller class
     * @param outputPath directory to output proto file to
     * @throws IOException
     */
    public void generateProtoFiles(Class<? extends Controller> clazz, String outputPath) throws IOException {
        Optional<String> javaPackage = Optional.ofNullable(clazz.getPackage())
                .map(Package::getName)
                .map(pack -> {
                    if (pack.lastIndexOf('.') > -1){
                        return pack.substring(0, pack.lastIndexOf('.')).trim();
                    } else {
                        return pack;
                    }
                });
        Optional<String> pseudoPackage = Optional.ofNullable(clazz.getAnnotation(ProtobufService.Service.class))
                .map(ProtobufService.Service::packageName);

        List<RPCModel> functions = Arrays.stream(clazz.getMethods())
                .filter(method -> {
                    return (method.getAnnotation(ProtobufService.Input.class) != null
                            && method.getAnnotation(ProtobufService.Output.class) != null);
                })
                .map(method -> {
                    String input = method.getAnnotation(ProtobufService.Input.class).value().getName().replaceAll("\\$", ".");
                    String output = method.getAnnotation(ProtobufService.Output.class).value().getName().replaceAll("\\$", ".");
                    return new RPCModel(method.getName(), input, output);
                }).collect(Collectors.toList());

        ServiceModel service = new ServiceModel(javaPackage, pseudoPackage, clazz.getSimpleName(), functions);
        try {
            String path = outputPath + "/" + service.getName() + ".proto";
            File outFile = new File(path);
            try(FileWriter writer = new FileWriter(outFile)){
                reverseMustache.execute(writer, service).flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ServicesWrapperModel{
        final List<ServiceModel> services;
        public ServicesWrapperModel(List<ServiceModel> services){
            this.services = services;
        }
    }
}
