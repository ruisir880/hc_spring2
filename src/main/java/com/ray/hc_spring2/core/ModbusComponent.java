package com.ray.hc_spring2.core;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import com.ray.hc_spring2.core.repository.DefenseAreaRepository;
import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.execptions.HCRuntimeException;
import com.ray.hc_spring2.model.DefenseArea;
import com.ray.hc_spring2.model.HcDevice;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

@Component
public class ModbusComponent {
    private static Logger log = LoggerFactory.getLogger(ModbusComponent.class);
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DefenseAreaRepository defenseAreaRepository;

    private String controllerIp;
    //private List<DefenseArea> defenseAreas;


    public void startWarnArea(List<Integer> area){
        sendOrder(area,true);
    }

    public void stopWarn(){
        sendOrder(Arrays.asList(1,2,3,4),false);
    }

    private void sendOrder(List<Integer> areas, boolean isOpen){
        initParams();
        try {
            // 设置主机TCP参数
            TcpParameters tcpParameters = new TcpParameters();

            // 设置TCP的ip地址
            InetAddress adress = InetAddress.getByName(controllerIp);

            // TCP参数设置ip地址
            // tcpParameters.setHost(InetAddress.getLocalHost());
            tcpParameters.setHost(adress);

            // TCP设置长连接
            tcpParameters.setKeepAlive(true);
            // TCP设置端口，这里设置是默认端口502
            tcpParameters.setPort(Modbus.TCP_PORT);

            // 创建一个主机
            ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            Modbus.setAutoIncrementTransactionId(true);

            int slaveId = 1;//从机地址
            int offset = 16;//寄存器读取开始地址
            int quantity = 8;//读取的寄存器数量

            try {
                if (!master.isConnected()) {
                    master.connect();// 开启连接
                }
                // 读取对应从机的数据，readInputRegisters读取的写寄存器，功能码04
                boolean[] registerValues = master.readCoils(slaveId, offset, quantity);
                StringBuilder stringBuilder = new StringBuilder();
                for(boolean registerValue:registerValues){
                    stringBuilder.append(String.valueOf(registerValue));
                }
                log.info("控制器信息："+ stringBuilder);

                for(int area:areas) {
                    master.writeSingleCoil(slaveId, offset + area - 1, isOpen);//灯光1亮
                }
                master.writeSingleCoil(slaveId, offset + 4, isOpen);//蜂蜜器开
            } catch (Exception e) {
                throw new HCRuntimeException("操作控制器发生错误.",e);
            }  finally {
                try {
                    master.disconnect();
                } catch (ModbusIOException e) {
                    log.warn("关闭控制连接异常.",e);
                }
            }
        } catch (Exception e) {
            throw new HCRuntimeException("连接控制错误.",e);
        }
    }

    private void initParams(){
        HcDevice controller = deviceService.findController();

        if(controller== null){
            throw new HCRuntimeException("设备列表中找不到控制器。");
        }
        controllerIp =controller.getIp();
       // defenseAreas = defenseAreaRepository.findAll();
    }
}
