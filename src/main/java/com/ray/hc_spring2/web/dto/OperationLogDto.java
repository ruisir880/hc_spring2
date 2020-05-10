package com.ray.hc_spring2.web.dto;

import com.ray.hc_spring2.model.OperationLog;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

public class OperationLogDto implements Serializable {
    private List<OperationLog> operationLogList;
    private long totalCount;
    private int page;
    private int totalPage;


    public OperationLogDto(Page<OperationLog> operationLogList) {
        this.operationLogList = operationLogList.getContent();
        this.totalCount = operationLogList.getTotalElements();
        this.page = operationLogList.getNumber() + 1;
        this.totalPage = operationLogList.getTotalPages();
    }


    public List<OperationLog> getOperationLogList() {
        return operationLogList;
    }

    public void setOperationLogList(List<OperationLog> operationLogList) {
        this.operationLogList = operationLogList;
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
