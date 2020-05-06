package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.model.HcDevice;

import java.util.List;

public interface DeviceService {
    List<HcDevice> findByArea(String defenseArea);

    void saveDevice(HcDevice hcDevice);
}
