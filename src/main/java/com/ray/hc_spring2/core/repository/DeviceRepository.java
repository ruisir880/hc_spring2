package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.HcDevice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceRepository extends CrudRepository<HcDevice, Long> {
    @Query("select t from HcDevice t where t.defenseArea.defenseName=:defenseArea and t.deviceType=:deviceType")
    List<HcDevice> findDevicesByDefenseAreaAndDeviceType(@Param(value="defenseArea") String defenseArea,@Param(value="deviceType")  String deviceType);

    List<HcDevice> findAll();
    HcDevice findByIp(String ip);

    HcDevice findById(long id);

    List<HcDevice> findDevicesByDeviceType(String deviceType);
}
