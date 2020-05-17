package com.ray.hc_spring2.web.dto;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class NvrChannelInfoDto implements Serializable {
    private String no;
    private String channelNo;
    private String ipcIp;
    private String ipcPort;
    private String ipChannel;
    private String online;



    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getIpcIp() {
        return ipcIp;
    }

    public void setIpcIp(String ipcIp) {
        this.ipcIp = ipcIp;
    }

    public String getIpcPort() {
        return ipcPort;
    }

    public void setIpcPort(String ipcPort) {
        this.ipcPort = ipcPort;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getIpChannel() {
        return ipChannel;
    }

    public void setIpChannel(String ipChannel) {
        this.ipChannel = ipChannel;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
