package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.OperationLogComponent;
import com.ray.hc_spring2.core.constant.DeviceType;
import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.NvrTools;
import com.ray.hc_spring2.web.dto.NvrChannelInfoDto;
import com.ray.hc_spring2.web.dto.ReturnResultDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

@Controller
public class NvrController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private OperationLogComponent operationLogComponent;
    private NvrTools nvrTools = new NvrTools();

    @RequestMapping(value ="/nvr")
    public ModelAndView nvr() {
        ModelAndView modelAndView = new ModelAndView();
        List<HcDevice> hcDevices = deviceService.findByAll();
        modelAndView.addObject("deviceList",hcDevices);
        HcDevice nvr = deviceService.findNvr();
        if (nvr == null) {
            nvr = new HcDevice();
            nvr.setDeviceType(DeviceType.NVR.toString());
        }else {
            setNvrChannelInfo(nvr,modelAndView);
        }
        modelAndView.addObject("device", nvr);
        modelAndView.setViewName("nvr");
        return modelAndView;
    }

    @RequestMapping(value ="/replayer")
    public ModelAndView replayer() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("replayer");
        return modelAndView;
    }

    /**
     * 认证成功返回0；失败-1
     * @param id
     * @param ip
     * @param account
     * @param password
     * @param port
     * @return
     */
    @PostMapping(value ="/editNvr")
    @ResponseBody
    public int editNvr(String id,String ip,String account,String password, String port ) {
        operationLogComponent.operate("编辑NVR");
        HcDevice device = new HcDevice();
        device.setId(StringUtils.isNoneBlank(id) ? Long.valueOf(id) : null);
        device.setAccount(account);
        device.setIp(ip);
        device.setPassword(password);
        device.setPort(port);
        device.setDeviceType(DeviceType.NVR.toString());
        if(nvrTools.register(device.getIp(),device.getPort(),device.getAccount(),device.getPassword())) {
            nvrTools.release();
            deviceService.saveDevice(device);
            return 0;
        }
        return -1;
    }

    @PostMapping(value = "/addChannel")
    @ResponseBody
    public ReturnResultDto addChannel(String selectedDevice,String selectedChannel){
        HcDevice ipc = deviceService.findByIp(selectedDevice);
        HcDevice nvr = deviceService.findNvr();
        int channelIndex = Integer.valueOf(selectedChannel.substring(8));

        if(nvrTools.register(nvr.getIp(),nvr.getPort(),nvr.getAccount(),nvr.getPassword())) {
            nvrTools.setChannel(channelIndex,ipc);
            nvrTools.saveChange();
            nvrTools.release();
            return new ReturnResultDto(0,"添加成功");
        }
        return new ReturnResultDto(1,"注册失败");
    }

    @PostMapping(value = "/deleteChannel")
    @ResponseBody
    public ReturnResultDto deleteChannel(String selectedChannel){
        HcDevice nvr = deviceService.findNvr();
        int channelIndex = Integer.valueOf(selectedChannel.substring(8));

        if(nvrTools.register(nvr.getIp(),nvr.getPort(),nvr.getAccount(),nvr.getPassword())) {
            nvrTools.deleteChannel(channelIndex);
            nvrTools.saveChange();
            nvrTools.release();
            return new ReturnResultDto(0,"删除成功");
        }
        return new ReturnResultDto(1,"注册失败");
    }

    @RequestMapping(value ="/refreshNvr")
    @ResponseBody
    public List<NvrChannelInfoDto> refresh() {
        HcDevice device = deviceService.findNvr();
        if (device != null) {
            if(nvrTools.register(device.getIp(),device.getPort(),device.getAccount(),device.getPassword())) {
                return nvrTools.getNvrChannelInfo();
            }
        }
        return new ArrayList<>();
    }

    private void setNvrChannelInfo(HcDevice device, ModelAndView modelAndView){
        if(nvrTools.register(device.getIp(),device.getPort(),device.getAccount(),device.getPassword())) {
            modelAndView.addObject("nvrChannels",nvrTools.getNvrChannelInfo());
            nvrTools.release();
        }
    }

}
