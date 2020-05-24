package com.ray.hc_spring2.core;

import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.core.repository.AlarmLogRepository;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.web.config.MyWebSocket;
import groovy.lang.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                log.warn("开始联合报警==============================");
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

}
