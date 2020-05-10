package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.DefenseArea;
import com.ray.hc_spring2.model.HcDevice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DefenseAreaRepository extends CrudRepository<DefenseArea, Long> {
    List<DefenseArea> findAll();

}
