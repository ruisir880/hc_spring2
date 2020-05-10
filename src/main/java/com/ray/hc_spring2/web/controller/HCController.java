package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.HCNetTools;
import com.ray.hc_spring2.web.dto.AlarmResult;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HCController {

    @Autowired
    private DeviceService deviceService;

    private boolean isAlarm = false;

    private Map<HcDevice,HCNetTools>  deviceHCNetToolsMap = new HashMap<>();




    @PostMapping(value ="/startOrCancelAlarm")
    @ResponseBody
    public AlarmResult startOrCancelAlarm(){

       if(isAlarm){
           int code = cancelAlarmHandler();
           AlarmResult alarmResult = new AlarmResult();
           alarmResult.setCode(code);
           alarmResult.setMsg(code==0 ? "撤防成功":"撤防失败");
          return alarmResult;
       }
        int code = startAlarmHandler();
        AlarmResult alarmResult = new AlarmResult();
        alarmResult.setCode(code);
        alarmResult.setMsg(code==0 ? "布防成功":"布防失败");
        return alarmResult;
    }


    public int startAlarmHandler(){
        List<HcDevice> devices = deviceService.findByAll();
        HCNetTools hcNetTools;
        for(HcDevice device : devices){
            if(deviceHCNetToolsMap.get(device) == null){
                deviceHCNetToolsMap.put(device,new HCNetTools());
            }
            hcNetTools = deviceHCNetToolsMap.get(device);
            hcNetTools.initDevices();
            hcNetTools.startAlarm(device);
        }
        return 0;
    }

    public int cancelAlarmHandler(){
        List<HcDevice> devices = deviceService.findByAll();
        HCNetTools hcNetTools;
        for(HcDevice device : devices){
            if(deviceHCNetToolsMap.get(device) == null){
                deviceHCNetToolsMap.put(device,new HCNetTools());
            }
            hcNetTools = deviceHCNetToolsMap.get(device);
            hcNetTools.initDevices();
            hcNetTools.cancelAlarm(device);
        }
        return 0;
    }





}
