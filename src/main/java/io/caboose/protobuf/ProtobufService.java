package io.caboose.protobuf;

import io.caboose.protobuf.models.RPCType;
import play.Logger;
import play.mvc.Result;
import play.mvc.Results;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

/**
 * Annotations and function to handle protobuf input and output
 */
public interface ProtobufService {
    /**
     * Annotation to express output as protobuf class
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Output {
        Class<? extends com.google.protobuf.GeneratedMessageV3 > value();
        RPCType rpcType() default RPCType.UNARY;
    }

    /**
     * Annotation to express input as protobuf class.  Used mostly by dynamic router
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Input {
        Class<? extends com.google.protobuf.GeneratedMessageV3 > value();
        RPCType rpcType() default RPCType.UNARY;
    }

    /**
     * Annotation to describe GRPC style service.  PackageName is an alias to expose for routing
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Service {
        String packageName() default "";
    }

    /**
     * Return a play result from protobuf object.  Should be of style return ProtobufService.ok(protobufObject);
     * @param input protobuf object
     * @param <T>
     * @return
     */
    public static <T> Result ok(T input) {
        try {
            return Results.ok((byte[]) (input.getClass().getMethod("toByteArray").invoke(input)));
        } catch (NoSuchMethodException  | IllegalAccessException | InvocationTargetException e) {
            Logger.error("Some issue parsing output", e);
            return Results.internalServerError();
        }
    }
}
