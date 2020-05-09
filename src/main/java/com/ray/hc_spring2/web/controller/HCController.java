package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.HCNetSDK;
import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.HCNetTools;
import com.ray.hc_spring2.utils.MessageUtil;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Controller
public class HCController {

    @Autowired
    private DeviceService deviceService;

    private HCNetTools hcNetTools = new HCNetTools();


    @PostMapping(value ="/startAlarm")
    @ResponseBody
    public int startAlarmHandler(){
        List<HcDevice> devices = deviceService.findByAll();
        hcNetTools.initDevices();
        for(HcDevice device : devices){
            hcNetTools.startAlarm(device);
        }
        return 0;
    }

    public void stopAlarmHandler(){

    }





}
