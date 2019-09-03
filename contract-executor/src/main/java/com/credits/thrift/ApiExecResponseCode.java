package com.credits.thrift;

public enum ApiExecResponseCode {
    SUCCESS(0),
    FAILURE(1),
    INCOMPATIBLE_VERSION(2),
    NODE_UNREACHABLE(3);

    private final byte code;

    ApiExecResponseCode(int code) {
        this.code = (byte) (0xFF & code);
    }

    public byte getCode() {
        return code;
    }
}
