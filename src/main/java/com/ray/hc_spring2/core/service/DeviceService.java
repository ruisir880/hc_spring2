package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.model.HcDevice;

import java.util.List;

public interface DeviceService {
    List<HcDevice> findByArea(String defenseArea);

    List<HcDevice> findByAll();

    void saveDevice(HcDevice hcDevice);
    void deleteById(long id);

    HcDevice findByIp(String ip);
    HcDevice findById(long id);
}
