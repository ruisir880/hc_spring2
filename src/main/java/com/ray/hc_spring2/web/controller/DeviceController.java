package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.HcCache;
import com.ray.hc_spring2.core.ModbusComponent;
import com.ray.hc_spring2.core.OperationLogComponent;
import com.ray.hc_spring2.core.WarnComponent;
import com.ray.hc_spring2.core.constant.DeviceType;
import com.ray.hc_spring2.core.repository.DefenseAreaRepository;
import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.model.DefenseArea;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.HCNetTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
public class DeviceController {
    private static Logger log = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DefenseAreaRepository defenseAreaRepository;
    @Autowired
    private HcCache hcCache;
    @Autowired
    private OperationLogComponent operationLogComponent;


    private HCNetTools tools = new HCNetTools();

    @RequestMapping(value ="/deviceEdit")
    @RequiresPermissions("system.management")//权限管理;
    public ModelAndView deviceEdit(String deviceId ) {
        ModelAndView modelAndView = new ModelAndView();
        HcDevice device = new HcDevice();
        if(StringUtils.isNotEmpty(deviceId)){
            device = deviceService.findById(Long.valueOf(deviceId));
        }
        modelAndView.addObject("device",device);
        modelAndView.addObject("defenseAreaList",defenseAreaRepository.findAll());
        modelAndView.setViewName("deviceEdit");
        return modelAndView;
    }

    @RequestMapping(value ="/deviceList")
    @RequiresPermissions("system.management")//权限管理;
    public ModelAndView deviceList(String defenseArea) {
        ModelAndView modelAndView = new ModelAndView();
        List<HcDevice> hcDevices = deviceService.findByArea(defenseArea);
        modelAndView.addObject("deviceList",hcDevices);
        modelAndView.addObject("defenseAreaList",defenseAreaRepository.findAll());
        modelAndView.setViewName("deviceList");
        return modelAndView;
    }

    @RequestMapping(value ="/queryDevices")
    @RequiresPermissions("system.management")//权限管理;
    @ResponseBody
    public List<HcDevice> queryDevices(String defenseArea) {
        ModelAndView modelAndView = new ModelAndView();
        List<HcDevice> hcDevices = deviceService.findByArea(defenseArea);
        return hcDevices;
    }

    @PostMapping(value ="/deleteDevice")
    @RequiresPermissions("system.management")//权限管理;
    @ResponseBody
    public int deleteDevice(long deviceId) {
        deviceService.deleteById(Long.valueOf(deviceId));
        return 0;
    }

    @PostMapping(value ="/editDevice")
    @RequiresPermissions("system.management")//权限管理;
    @ResponseBody
    public int editDevice(String id,String ip,String account,String password, String port ,String defenseArea,String type) {
        operationLogComponent.operate("编辑设备");
        boolean isWebCam = type.equals(DeviceType.WebCame.toString());
        HcDevice device = new HcDevice();
        device.setId(StringUtils.isNoneBlank(id) ? Long.valueOf(id) : null);
        device.setAccount(isWebCam ? account : "*");
        device.setIp(ip);
        device.setPassword(isWebCam ? password : "*");
        device.setPort(port);
        device.setDeviceType(type);
        if (StringUtils.isNotEmpty(defenseArea)) {
            DefenseArea area = new DefenseArea();
            area.setId(Long.valueOf(defenseArea));
            device.setDefenseArea(area);
        }
        //新添加查询是否已经存在
        if (StringUtils.isEmpty(id)) {
            if (deviceService.findByIp(ip) != null) {
                return -1;
            }
        }
        int result = 0;
        if (isWebCam) {
            result = tools.testDevice(device);
        }

        if (result == 0) {
            deviceService.saveDevice(device);
            hcCache.clearIpDefenseAreaCache(device.getIp());
        }
        return result;
    }

}
