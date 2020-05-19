package com.ray.hc_spring2.execptions;

/**
 * Created by rui on 2018/9/2.
 */
public class BusinessLogicException extends Exception {

    public BusinessLogicException(String msg) {
        super(msg);
    }

    public BusinessLogicException(Throwable cause) {
        super(cause);
    }
}
