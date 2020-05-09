package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.HCNetTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    private HCNetTools tools = new HCNetTools();

    @RequestMapping(value ="/deviceEdit")
    public ModelAndView deviceEdit() {
        ModelAndView modelAndView = new ModelAndView();
        HcDevice device = new HcDevice();
        modelAndView.addObject("device",device);
        modelAndView.setViewName("deviceEdit");
        return modelAndView;
    }


    @PostMapping(value ="/editDevice")
    @ResponseBody
    public int editDevice(String id,String ip,String account,String password, String port ,String defenseArea) {
        ModelAndView modelAndView = new ModelAndView();
        HcDevice device = new HcDevice();
        device.setId(StringUtils.isNoneBlank(id) ? Long.valueOf(id) : null);
        device.setAccount(account);
        device.setIp(ip);
        device.setPassword(password);
        device.setPort(port);
        device.setDefenseArea(defenseArea);
        if (deviceService.findByIp(ip) != null) {
           return -1;
        }
        int result= tools.testDevice(device);
        if( result== 0) {
            deviceService.saveDevice(device);
        }
        return result;
    }
}
