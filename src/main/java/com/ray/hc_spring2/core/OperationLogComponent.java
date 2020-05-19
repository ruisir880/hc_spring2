package com.ray.hc_spring2.core;

import com.ray.hc_spring2.core.repository.OperationLogRepository;
import com.ray.hc_spring2.core.service.OperationLogService;
import com.ray.hc_spring2.model.OperationLog;
import com.ray.hc_spring2.model.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OperationLogComponent {
    private static Logger log = LoggerFactory.getLogger(OperationLogComponent.class);

    @Autowired
    private OperationLogRepository operationLogRepository;

    public void operate(String operation){
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        log.info(String.format("用户:%s 操作：%s",userInfo.getUsername(),operation));
        OperationLog operationLog = new OperationLog();
        operationLog.setUser(userInfo.getUsername());
        operationLog.setOperation(operation);
        operationLog.setGenerationTime(new Date());
        operationLogRepository.save(operationLog);

    }
}
