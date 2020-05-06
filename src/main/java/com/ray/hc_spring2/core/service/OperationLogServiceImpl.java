package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.core.repository.OperationLogRepository;
import com.ray.hc_spring2.model.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    private OperationLogRepository repository;

    public void addLog(OperationLog log){
        repository.save(log);
    }
}
