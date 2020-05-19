package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.OperationLogComponent;
import com.ray.hc_spring2.core.repository.PrivilegeRepository;
import com.ray.hc_spring2.core.service.UserInfoService;
import com.ray.hc_spring2.model.PrivilegeInfo;
import com.ray.hc_spring2.model.UserInfo;
import com.ray.hc_spring2.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class UserInfoController {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private OperationLogComponent operationLogComponent;

    @RequestMapping(value ="/userAdd")
    public ModelAndView userAdd() {
        ModelAndView modelAndView = new ModelAndView();
        UserInfo userInfo = new UserInfo();
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.setViewName("userEdit");
        return modelAndView;
    }

    @RequestMapping(value ="/userList")
    public ModelAndView userList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userList");
        return modelAndView;
    }

    @RequestMapping(value = {"/editUser","/addUser"}, method = RequestMethod.POST )
    @ResponseBody
    public int userInfoAdd(String uid,String username,String password,String realName,String mobile,String email) throws ExecutionException {
        operationLogComponent.operate("编辑用户");
        UserInfo user = new UserInfo();
        if(StringUtils.isNotEmpty(uid)){
            user.setUid(Long.valueOf(uid));
        }
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setMobile(mobile);
        user.setEmail(email);
        if(user.getUid()<1){
            UserInfo userInfo = userInfoService.findByUsername(user.getUsername());
            if (userInfo != null) {
                return -1;
            }
        }
        String salt = UserUtil.generateSalt(user.getPassword());
        String encryptPassword = UserUtil.encryptPassword(user.getUsername(), user.getPassword(), salt);
        user.setSalt(salt);
        user.setPassword(encryptPassword);

        userInfoService.saveUser(user);
        return 0;
    }

    @PostMapping("/deleteUser")
    @ResponseBody
    public int userDel(long userId){
        userInfoService.deleteUser(userId);
        return 0;
    }

    @GetMapping("/userEdit")
    public ModelAndView userEdit(long userId) throws ExecutionException {
        ModelAndView modelAndView = new ModelAndView();
        UserInfo userInfo = userInfoService.findById(userId);
        List<PrivilegeInfo> privilegeInfos =  privilegeRepository.findAll();
        modelAndView.addObject("userInfo",userInfo);
        modelAndView.setViewName("userEdit");
        return modelAndView;
    }

    @RequestMapping(value = "/queryUserList",method = RequestMethod.GET)
    @ResponseBody
    public List<UserInfo> queryUserPage(String username,String realName,String mobile){
        List<UserInfo> userInfoList = userInfoService.findByCondition(username,realName,mobile);
        return userInfoList;
    }
}
