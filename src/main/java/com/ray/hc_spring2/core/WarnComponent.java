package com.ray.hc_spring2.core;

import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.web.config.MyWebSocket;
import groovy.lang.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

@Singleton
@Component
public class WarnComponent {
    private static Logger log = LoggerFactory.getLogger(WarnComponent.class);
    private static final int TIME_DIFF =20;
    private static Map<String, Date> WARN_MAP = Collections.synchronizedMap(new HashMap<String, Date>());

    @Autowired
     private MyWebSocket myWebSocket;;

    public void addWarn(String defenseArea,String system) {
        String da = defenseArea.trim();
        try {
            WARN_MAP.put(da + "|" + system, new Date());
            Date remoteDate = WARN_MAP.get(da + "|" + Constants.SYSTEM_REMOTE);
            Date localDate = WARN_MAP.get(da + "|" + Constants.SYSTEM_LOCAL);
            if (system.equals(Constants.SYSTEM_LOCAL) && remoteDate != null) {
                if(ifInTime(remoteDate,localDate)){
                    log.warn("开始联合报警==============================");
                    myWebSocket.sendMessage(defenseArea);
                }
            }
        }catch (Exception e){
            log.error("error occurs in WarnComponent",e);
        }


    }


    private boolean ifInTime(Date date1 , Date date2){
        long subTime = Math.abs(date1.getTime() -date2.getTime());
        if(subTime/1000>20){
            return false;
        }
        return true;
    }


     
}
