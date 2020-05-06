package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    /** 通过username查找用户信息 **/
    UserInfo findByUsername(String username);

    UserInfo save(UserInfo userInfo);

    List<UserInfo> findAll();

    Page<UserInfo> findAll(Specification<UserInfo> spec, Pageable pageable);


}
