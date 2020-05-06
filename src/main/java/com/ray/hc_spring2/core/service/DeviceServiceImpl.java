package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.core.repository.DeviceRepository;
import com.ray.hc_spring2.model.HcDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<HcDevice> findByArea(String defenseArea) {
        return deviceRepository.findDevicesByDefenseArea(defenseArea);
    }

    public void saveDevice(HcDevice hcDevice){
        deviceRepository.save(hcDevice);
    }
}
