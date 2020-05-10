package com.ray.hc_spring2.core.service;

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
            return deviceRepository.findDevicesByDefenseArea(defenseArea);
        }
        return deviceRepository.findAll();
    }

    @Override
    public List<HcDevice> findByAll() {
        return deviceRepository.findAll();
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


}
