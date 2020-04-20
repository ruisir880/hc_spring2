package com.ray.hc_spring2.execptions;

public class HCRuntimeException extends RuntimeException {
    public HCRuntimeException(String message) {
        super(message);
    }

    public HCRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HCRuntimeException(Throwable cause) {
        super(cause);
    }
}
