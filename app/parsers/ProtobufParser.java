package parsers;

import akka.util.ByteString;
import play.Logger;
import play.api.http.HttpConfiguration;
import play.http.HttpErrorHandler;
import play.mvc.BodyParser;
import play.mvc.Http;

import java.lang.reflect.Method;

/**
 * DESCRIPTION
 */
public abstract class ProtobufParser<T extends com.google.protobuf.GeneratedMessageV3 > extends BodyParser.BufferingBodyParser<T> {


    protected abstract Class<T> getClazz();

    protected ProtobufParser(HttpConfiguration httpConfiguration, HttpErrorHandler errorHandler, String errorMessage) {
        super(httpConfiguration, errorHandler, errorMessage);
    }

    @Override
    protected T parse(Http.RequestHeader request, ByteString bytes) throws Exception {
        try {
            Class[] parameterTypes = new Class[]{byte[].class};
            Class<T> clazz = getClazz();
            Method method = clazz.getMethod("parseFrom", parameterTypes);
            byte[] byteArray = bytes.toArray();
            Object invoke = method.invoke(null, byteArray);
            return (T)invoke;
        } catch (Exception ex){
            Logger.error("Some parse error", ex);
            throw ex;
        }
    }
}
