package com.ray.hc_spring2.utils;

import com.ray.hc_spring2.core.HcCache;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.web.dto.AlarmLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParseUtil {

    @Autowired
    private HcCache hcCache;

    public AlarmLogDto getAlarmLogDto(AlarmLog log){
        AlarmLogDto alarmLogDto = new AlarmLogDto();
        alarmLogDto.setDefenseArea(hcCache.getAreaByIp(log.getDeviceIp()));
        alarmLogDto.setAlarmType(log.getAlarmType());
        alarmLogDto.setState(log.getState());
        alarmLogDto.setAlarmTime(DateUtil.formatDate(log.getAlarmTime()));
        alarmLogDto.setEndTime(DateUtil.formatDate(log.getEndTime()));
        return alarmLogDto;
    }
}
