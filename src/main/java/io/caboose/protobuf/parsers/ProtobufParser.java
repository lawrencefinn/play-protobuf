package io.caboose.protobuf.parsers;

import akka.util.ByteString;
import play.Logger;
import play.api.http.HttpConfiguration;
import play.http.HttpErrorHandler;
import play.mvc.BodyParser;
import play.mvc.Http;

import java.lang.reflect.Method;

/**
 * Abstract bodyparser class to parse protobuf.
 * To use:
 * Create class and extend ProtobufParser<T> with T being the protobuf class
 * Implement getClazz to return T.class.
 * Implement constructor with @Inject
 *
 * See test/java/protos/com/example/tutorial/PhoneNumberParser.java
 */
public abstract class ProtobufParser<T extends com.google.protobuf.GeneratedMessageV3 > extends BodyParser.BufferingBodyParser<T> {


    protected abstract Class<T> getClazz();

    protected Method method;

    protected ProtobufParser(HttpConfiguration httpConfiguration, HttpErrorHandler errorHandler, String errorMessage) throws NoSuchMethodException {
        super(httpConfiguration, errorHandler, errorMessage);
        Class[] parameterTypes = new Class[]{byte[].class};
        Class<T> clazz = getClazz();
        method = clazz.getMethod("parseFrom", parameterTypes);
    }

    @Override
    protected T parse(Http.RequestHeader request, ByteString bytes) throws Exception {
        try {
            byte[] byteArray = bytes.toArray();
            Object invoke = method.invoke(null, byteArray);
            return (T)invoke;
        } catch (Exception ex){
            Logger.error("Some parse error", ex);
            throw ex;
        }
    }
}
