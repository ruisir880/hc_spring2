package com.ray.hc_spring2.web.controller;

import com.google.common.base.Joiner;
import com.ray.hc_spring2.core.OperationLogComponent;
import com.ray.hc_spring2.core.repository.PrivilegeRepository;
import com.ray.hc_spring2.core.repository.RoleRepository;
import com.ray.hc_spring2.model.PrivilegeInfo;
import com.ray.hc_spring2.model.RoleInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ray Rui on 9/3/2018.
 */
@Controller
public class PermissionController {
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OperationLogComponent operationLogComponent;

    @GetMapping(value = "/privilegeList" )
    @RequiresPermissions("system.management")//权限管理;
    public ModelAndView monitorPointEdit(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("privilegeList");

        List<RoleInfo> roles = roleRepository.findAll();
        List<PrivilegeInfo> privilegeInfos = privilegeRepository.findAll();
        modelAndView.addObject("privileges",privilegeInfos);
        modelAndView.addObject("roleList",roles);
        modelAndView.addObject("privilegeIdList",
                roles.get(0).getPermissions().stream().map(
                        privilegeInfo -> privilegeInfo.getId()).collect(Collectors.toList()));
        return modelAndView;
    }

    @PostMapping(value = "/updatePrivilege" )
    @RequiresPermissions("system.management")//权限管理;
    @ResponseBody
    public int updatePrivilege(long roleId, @RequestParam(required = false, value = "privilegeIds[]") List<Long> idList){
        operationLogComponent.operate("编辑权限");
        List<PrivilegeInfo> permissions = (List<PrivilegeInfo>) privilegeRepository.findAll(idList);
        RoleInfo roleInfo = roleRepository.findById(roleId);
        roleInfo.setPermissions(permissions);
        roleRepository.save(roleInfo);
        return 0;
    }


    @GetMapping(value = "/getPrivilegeByRole" )
    @RequiresPermissions("system.management")//权限管理;
    @ResponseBody
    public String getRolePrivileges(long roleId){
        RoleInfo roleInfo  = roleRepository.findById(roleId);
        List<Long> idList = new ArrayList<>();
        if(roleInfo != null){
            for (PrivilegeInfo privilegeInfo : roleInfo.getPermissions()){
                idList.add(privilegeInfo.getId());
            }
        }
        return Joiner.on(",").join(idList);
    }

}
