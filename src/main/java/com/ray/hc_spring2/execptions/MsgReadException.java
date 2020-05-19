package com.ray.hc_spring2.execptions;

/**
 * Created by rui on 2018/9/2.
 */
public class MsgReadException extends BusinessLogicException {
    public MsgReadException(String msg) {
        super(msg);
    }

    public MsgReadException(Throwable cause) {
        super(cause);
    }
}
