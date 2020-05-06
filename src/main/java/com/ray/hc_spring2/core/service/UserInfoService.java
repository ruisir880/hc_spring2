package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.model.UserInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserInfoService {

    UserInfo findById(long userId);

    UserInfo findByUsername(String username);

    UserInfo saveUser(UserInfo userInfo);

    Page<UserInfo> pageQuery(int page);

    List<UserInfo> findByCondition(String username, String realName, String mobile);

    Page<UserInfo> pageUserQuery(int page, String username, String realName, String mobile);

    void deleteUser(long userId);

}
