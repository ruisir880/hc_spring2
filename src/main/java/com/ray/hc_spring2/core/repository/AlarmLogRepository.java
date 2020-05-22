package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.model.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

public interface AlarmLogRepository extends CrudRepository<AlarmLog, Long> {

    Page<AlarmLog> findAll(Specification<AlarmLog> spec, Pageable pageable);
    AlarmLog findById(long id);
}
