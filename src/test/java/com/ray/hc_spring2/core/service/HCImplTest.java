package com.ray.hc_spring2.core.service;

import com.ray.hc_spring2.HcSpring2Application;
import com.ray.hc_spring2.core.repository.DefenseAreaRepository;
import com.ray.hc_spring2.core.repository.OperationLogRepository;
import com.ray.hc_spring2.model.DefenseArea;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.model.OperationLog;
import com.ray.hc_spring2.web.controller.HCController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HcSpring2Application.class)
public class HCImplTest {

    @Autowired
    private  UserInfoService userInfoService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private HCController hcController;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private DefenseAreaRepository defenseAreaRepository;


    @Test
    public void findByUsername() {
        userInfoService.findByUsername("admin");
    }

    @Test
    public void testAddDefenseArea(){
        DefenseArea defenseArea = new DefenseArea();
        defenseArea.setDefenseName("1");
        defenseAreaRepository.save(defenseArea);
    }

    @Test
    public void deviceAddTest() {
        String ip = "192.168.1.64";
        HcDevice hcDevice = new HcDevice();
        hcDevice.setAccount("admin");
        hcDevice.setPassword("Special101");
        hcDevice.setIp("192.168.1.64");
        hcDevice.setPort("8000");

        DefenseArea  defenseArea = new DefenseArea();
        defenseArea.setId(1L);
        hcDevice.setDefenseArea(defenseArea);
        deviceService.saveDevice(hcDevice);
    }

    @Test
    public void testQueryDefenseArea(){
        deviceService.findByAll();
    }

    @Test
    public void deviceFindTest() {
        List<HcDevice> devices = deviceService.findByArea("1");
        System.out.println(devices.size());
    }

    @Test
    public void testAlarm(){
        hcController.startAlarmHandler();
    }

    @Test
    public void testOperation(){
        List<OperationLog> operationLogs = new ArrayList<>();
        for(int i=0;i<100;i++){
            OperationLog operationLog = new OperationLog();
            operationLog.setOperation("do");
            operationLog.setUser("user"+i);
            operationLogs.add(operationLog);
        }
        operationLogRepository.save(operationLogs);
    }



}