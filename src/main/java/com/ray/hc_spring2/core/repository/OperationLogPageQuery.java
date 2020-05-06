package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.OperationLog;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface OperationLogPageQuery<T, ID extends Serializable> extends CrudRepository<OperationLog, Long> {
}
