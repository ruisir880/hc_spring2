package com.ray.hc_spring2.model;

public class Device {

    private  int type;
    private String account;
    private String password;
    private String port;
    private String ip;

    public Device(int type, String account, String password, String port, String ip) {
        this.type = type;
        this.account = account;
        this.password = password;
        this.port = port;
        this.ip = ip;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
