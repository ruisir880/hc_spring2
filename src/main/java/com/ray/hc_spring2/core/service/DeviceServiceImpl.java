package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.core.constant.DeviceType;
import com.ray.hc_spring2.core.repository.DeviceRepository;
import com.ray.hc_spring2.model.HcDevice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<HcDevice> findByArea(String defenseArea) {
        if(StringUtils.isNotEmpty(defenseArea)) {
            return deviceRepository.findDevicesByDefenseAreaAndDeviceType(defenseArea, DeviceType.WebCame.toString());
        }
        return findByAll();
    }

    @Override
    public List<HcDevice> findByAll() {
        List<HcDevice> devices = deviceRepository.findDevicesByDeviceType(DeviceType.WebCame.toString());
        devices.addAll(deviceRepository.findDevicesByDeviceType(DeviceType.Controller.toString())) ;
        return devices;
    }

    @Override
    public List<HcDevice> findByAllWebCam() {
        List<HcDevice> devices = deviceRepository.findDevicesByDeviceType(DeviceType.WebCame.toString());
        return devices;
    }

    public void saveDevice(HcDevice hcDevice){
        deviceRepository.save(hcDevice);
    }

    @Override
    public void deleteById(long id) {
        deviceRepository.delete(id);
    }

    @Override
    public HcDevice findByIp(String ip) {
        return deviceRepository.findByIp(ip);
    }

    @Override
    public HcDevice findById(long id) {
        return deviceRepository.findById(id);
    }

    @Override
    public HcDevice findNvr() {
        List<HcDevice> hcDevices = deviceRepository.findDevicesByDeviceType(DeviceType.NVR.toString());
        if(hcDevices.size()>0){
            return hcDevices.get(0);
        }
        return null;
    }

    @Override
    public HcDevice findController() {
        List<HcDevice> hcDevices = deviceRepository.findDevicesByDeviceType(DeviceType.Controller.toString());
        if(hcDevices.size()>0){
            return hcDevices.get(0);
        }
        return null;
    }



}
