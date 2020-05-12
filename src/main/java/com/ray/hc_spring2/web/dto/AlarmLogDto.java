package com.ray.hc_spring2.web.dto;

import java.io.Serializable;

public class AlarmLogDto implements Serializable {

    private int index;
    private String defenseArea;
    private String state;
    private String alarmType;
    private String alarmTime;
    private String endTime;


    public String getDefenseArea() {
        return defenseArea;
    }

    public void setDefenseArea(String defenseArea) {
        this.defenseArea = defenseArea;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
