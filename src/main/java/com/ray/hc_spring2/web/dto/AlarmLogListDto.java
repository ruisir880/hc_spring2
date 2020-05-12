package com.ray.hc_spring2.web.dto;

import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.model.OperationLog;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlarmLogListDto implements Serializable {

    private List<AlarmLogDto> alarmLogs = new ArrayList<>();
    private long totalCount;
    private int page;
    private int totalPage;

    public AlarmLogListDto(List<AlarmLogDto> alarmLogs , long totalCount,int page,int totalPage) {
        this.alarmLogs = alarmLogs;
        this.totalCount = totalCount;
        this.page = page;
        this.totalPage = totalPage;
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

    public List<AlarmLogDto> getAlarmLogs() {
        return alarmLogs;
    }

    public void setAlarmLogs(List<AlarmLogDto> alarmLogs) {
        this.alarmLogs = alarmLogs;
    }
}
