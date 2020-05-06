package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.model.OperationLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OperationLogRepository extends CrudRepository<OperationLog, Long> {

}
