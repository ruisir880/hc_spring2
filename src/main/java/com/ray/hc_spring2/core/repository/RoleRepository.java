package com.ray.hc_spring2.core.repository;


import com.ray.hc_spring2.model.RoleInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by rui on 2018/8/18.
 */
public interface RoleRepository extends CrudRepository<RoleInfo, Long> {

    RoleInfo findByRoleName(String roleName);


    RoleInfo findById(long roleId);
}
