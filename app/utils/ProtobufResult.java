package utils;

import play.Logger;
import play.mvc.Result;
import play.mvc.Results;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

/**
 * DESCRIPTION
 */
public interface ProtobufResult<A> {
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Of {

        /**
         * The class of the body parser to use.
         *
         * @return the class
         */
        Class<? extends com.google.protobuf.GeneratedMessageV3 > value();
    }
    public static <T> Result ok(T input) {
        try {
            return Results.ok((byte[]) (input.getClass().getMethod("toByteArray").invoke(input)));
        } catch (NoSuchMethodException  | IllegalAccessException | InvocationTargetException e) {
            Logger.error("Some issue parsing output", e);
            return Results.internalServerError();
        }
    }
}
