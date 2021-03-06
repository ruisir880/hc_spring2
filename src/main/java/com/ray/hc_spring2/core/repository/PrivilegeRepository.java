package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.PrivilegeInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by rui on 2018/8/18.
 */
public interface PrivilegeRepository extends CrudRepository<PrivilegeInfo, Long> {
    List<PrivilegeInfo> findAll();
}
