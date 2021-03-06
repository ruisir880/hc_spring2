package com.ray.hc_spring2.web.controller;

import com.google.common.base.Joiner;
import com.ray.hc_spring2.core.OperationLogComponent;
import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.HCNetTools;
import com.ray.hc_spring2.web.dto.AlarmResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HCController {

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private OperationLogComponent operationLogComponent;

    private List<String> failedIpList = new ArrayList<>();

    private boolean isAlarm = false;

    private Map<String,HCNetTools>  deviceHCNetToolsMap = new HashMap<>();


    @PostMapping(value ="/startOrCancelAlarm")
    @RequiresPermissions("oneTouch")//权限管理;
    @ResponseBody
    public AlarmResult startOrCancelAlarm() {
        failedIpList.clear();
        if (isAlarm) {
            operationLogComponent.operate("撤防");
            cancelAlarmHandler();
            int code = failedIpList.size();
            AlarmResult alarmResult = new AlarmResult();
            alarmResult.setCode(code);
            alarmResult.setMsg(code == 0 ? "撤防成功" : Joiner.on(",").join(failedIpList) + "撤防失败");
            isAlarm = false;
            return alarmResult;
        }
        operationLogComponent.operate("布防");
        startAlarmHandler();
        int code = failedIpList.size();
        AlarmResult alarmResult = new AlarmResult();
        alarmResult.setCode(code);
        alarmResult.setMsg(code == 0 ? "布防成功" : Joiner.on(",").join(failedIpList) + "布防失败");
        isAlarm = true;
        return alarmResult;
    }


    public void startAlarmHandler() {
        List<HcDevice> devices = deviceService.findByAllWebCam();
        HCNetTools hcNetTools;
        String key;
        for (HcDevice device : devices) {
            key = device.getId() + "|" + device.getIp();
            if (deviceHCNetToolsMap.get(key) == null) {
                deviceHCNetToolsMap.put(key, new HCNetTools());
            }
            hcNetTools = deviceHCNetToolsMap.get(key);
            hcNetTools.initDevices();
            if (!hcNetTools.startAlarm(device)) {
                failedIpList.add(device.getIp());
            }
        }
    }

    public void cancelAlarmHandler() {
        List<HcDevice> devices = deviceService.findByAllWebCam();
        HCNetTools hcNetTools;
        String key;
        for (HcDevice device : devices) {
            key = device.getId() + "|" + device.getIp();
            if (deviceHCNetToolsMap.get(key) == null) {
                deviceHCNetToolsMap.put(key, new HCNetTools());
            }
            hcNetTools = deviceHCNetToolsMap.get(key);
            hcNetTools.initDevices();
            if (!hcNetTools.cancelAlarm(device)) {
                failedIpList.add(device.getIp());
            }
        }
    }





}
