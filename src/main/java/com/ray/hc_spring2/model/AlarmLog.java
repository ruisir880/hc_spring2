package com.ray.hc_spring2.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class AlarmLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String deviceIp;
    private String defenseArea;
    private String state;
    private String alarmType;
    private Date alarmTime;
    private Date endTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }


    public String getDefenseArea() {
        return defenseArea;
    }

    public void setDefenseArea(String defenseArea) {
        this.defenseArea = defenseArea;
    }
}
