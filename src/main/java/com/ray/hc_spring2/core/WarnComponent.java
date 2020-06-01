package com.ray.hc_spring2.core;

import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.core.repository.AlarmLogRepository;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.utils.DateUtil;
import com.ray.hc_spring2.web.config.MyWebSocket;
import groovy.lang.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.serial.SerialAddress;
import org.apache.mina.transport.serial.SerialConnector;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Singleton
@Component
public class WarnComponent {
    private static Logger log = LoggerFactory.getLogger(WarnComponent.class);
    private static final int TIME_DIFF = 20;
    private static Map<String, Date> WARN_MAP = Collections.synchronizedMap(new HashMap<String, Date>());
    private static Map<String, Date> LAST_WARN_MAP = Collections.synchronizedMap(new HashMap<String, Date>());

    @Autowired
    private MyWebSocket myWebSocket;
    @Autowired
    private ModbusComponent modbusComponent;
    @Autowired
    private AlarmLogRepository alarmLogRepository;
    @Autowired
    private Environment env;

    private String SERIAL_PORT;
    private int BAUD_RATE;

    @PostConstruct
    public void init(){
        BAUD_RATE =Integer.valueOf(env.getProperty("serialport.baud-rate"));
        SERIAL_PORT = env.getProperty("serialport.port-name");
    }

    public void addWarn(String defenseArea, String system) {
        String da = defenseArea.trim();
        Date warnDate = new Date();
        try {
            WARN_MAP.put(da + "|" + system, new Date());
            Date remoteDate = WARN_MAP.get(da + "|" + Constants.SYSTEM_REMOTE);
            Date localDate = WARN_MAP.get(da + "|" + Constants.SYSTEM_LOCAL);
            if (system.equals(Constants.SYSTEM_LOCAL) && remoteDate != null) {
                AlarmLog alarmLog = new AlarmLog();
                alarmLog.setAlarmTime(warnDate);
                alarmLog.setState("未处理");
                alarmLog.setDefenseArea(da);
                alarmLogRepository.save(alarmLog);
            }


            if (ifInTime(remoteDate, localDate) && notOccursIn10Secs(LAST_WARN_MAP.get(da), warnDate)) {
                LAST_WARN_MAP.put(da, warnDate);
                log.warn("开始联合报警============================防区:"+da);
                sendCommand(String.format("防区%s报警，时间:%s",da, DateUtil.formatDate(warnDate)));
                myWebSocket.sendMessage(da);
                modbusComponent.startWarnArea(Arrays.asList(Integer.valueOf(da)));
            }
        } catch (Exception e) {
            log.error("error occurs in WarnComponent", e);
        }


    }


    private boolean ifInTime(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        long subTime = Math.abs(date1.getTime() - date2.getTime());
        if (subTime / 1000 > 10) {
            return false;
        }
        return true;
    }

    private boolean notOccursIn10Secs(Date lastDate, Date thisDate) {
        if (lastDate == null) {
            return true;
        }
        long subTime = Math.abs(thisDate.getTime() - lastDate.getTime());
        if (subTime / 1000 > 10) {
            return true;
        }
        return false;
    }

    /**
     * 发送指令至串口
     * @param command
     */
    public void sendCommand(String command) {
        if (StringUtils.isNotBlank(command)) {
            IoBuffer buffer = IoBuffer.wrap(command.getBytes());
            IoSession session = null;
            try {
                log.info("开始写串口："+command);
                //创建串口连接
                SerialConnector connector = new SerialConnector();
                //绑定处理handler
                connector.setHandler(new IoHandlerAdapter());
                //设置过滤器
                connector.getFilterChain().addLast("logger", new LoggingFilter());
                //配置串口连接
                SerialAddress address = new SerialAddress(SERIAL_PORT, BAUD_RATE, SerialAddress.DataBits.DATABITS_8, SerialAddress.StopBits.BITS_1 , SerialAddress.Parity.NONE, SerialAddress.FlowControl.NONE);
                ConnectFuture future = connector.connect(address);
                log.info("连接串口成功========================");
                future.await();
                session = future.getSession();
                session.write(buffer);
                connector.dispose();
            } catch (Exception e) {
                log.warn("写串口失败",e);
            } finally {
                if (session != null) {
                    session.close(true);
                }
            }
        }
    }


}
