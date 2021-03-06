package com.ray.hc_spring2.web.dto;

import java.io.Serializable;

public class ReturnResultDto implements Serializable {
    private int code;
    private String msg;

    public ReturnResultDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
