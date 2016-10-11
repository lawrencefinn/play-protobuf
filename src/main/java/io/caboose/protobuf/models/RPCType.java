package io.caboose.protobuf.models;

/**
 * Enum representing RPC input types.  UNARY is the only one supported ATM.
 */
public enum RPCType {
    UNARY,
    STREAMING
}
