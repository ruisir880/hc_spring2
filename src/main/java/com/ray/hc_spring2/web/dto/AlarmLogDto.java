package com.ray.hc_spring2.web.dto;

import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.model.OperationLog;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlarmLogDto implements Serializable {

    private List<AlarmLog> alarmLogs = new ArrayList<>();
    private long totalCount;
    private int page;
    private int totalPage;

    public AlarmLogDto(Page<AlarmLog> alarmLogs) {
        this.alarmLogs = alarmLogs.getContent();
        this.totalCount = alarmLogs.getTotalElements();
        this.page = alarmLogs.getNumber() + 1;
        this.totalPage = alarmLogs.getTotalPages();
    }

    public List<AlarmLog> getAlarmLogs() {
        return alarmLogs;
    }

    public void setAlarmLogs(List<AlarmLog> alarmLogs) {
        this.alarmLogs = alarmLogs;
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
