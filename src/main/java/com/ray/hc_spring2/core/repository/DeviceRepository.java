package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.HcDevice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeviceRepository extends CrudRepository<HcDevice, Long> {
    List<HcDevice> findDevicesByDefenseArea(String defenseArea);
    List<HcDevice> findAll();
    HcDevice findByIp(String ip);

    HcDevice findById(long id);
}
