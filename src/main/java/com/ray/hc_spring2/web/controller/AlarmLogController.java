package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.HcCache;
import com.ray.hc_spring2.core.OperationLogComponent;
import com.ray.hc_spring2.core.service.PageQueryService;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.utils.DateUtil;
import com.ray.hc_spring2.utils.ParseUtil;
import com.ray.hc_spring2.web.dto.AlarmLogDto;
import com.ray.hc_spring2.web.dto.AlarmLogListDto;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class AlarmLogController {

    @Autowired
    private PageQueryService pageQueryService;
    @Autowired
    private ParseUtil parseUtil;
    @Autowired
    private OperationLogComponent operationLogComponent;

    @RequestMapping(value ="/alarmLogList")
    @RequiresPermissions("alarm.log")//权限管理;
    public ModelAndView alarmLogList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("alarmLogList");
        return modelAndView;
    }

    @RequestMapping(value = "/queryAlarmLog",method = RequestMethod.GET)
    @RequiresPermissions("alarm.log")//权限管理;
    @ResponseBody
    public AlarmLogListDto queryUserPage(String page, String defenseArea, String alarmState, String startTime, String endTime) throws ParseException {
        operationLogComponent.operate("查询警告记录");
        int pageInt = StringUtils.isEmpty(page) ? 1 : Integer.valueOf(page);
        Date startDate = null;
        Date endDate = null;
        if (!StringUtils.isEmpty(startTime)) {
            startDate = DateUtil.parseDate(startTime);
        }

        if (!StringUtils.isEmpty(endTime)) {
            endDate = DateUtil.parseDate(endTime);
        }

        Page<AlarmLog> alarmLogs = pageQueryService.queryAlarmLog(pageInt,defenseArea, alarmState, startDate, endDate);

        return new AlarmLogListDto(getAlarmLogListDto(alarmLogs),alarmLogs.getTotalElements(),alarmLogs.getNumber()+1,alarmLogs.getTotalPages());
    }

    private List<AlarmLogDto> getAlarmLogListDto(Page<AlarmLog> alarmLogs){
        List<AlarmLogDto> alarmLogDtoList = new ArrayList();
        for(AlarmLog log: alarmLogs.getContent()){
            alarmLogDtoList.add(parseUtil.getAlarmLogDto(log));
        }
        return alarmLogDtoList;
    }
}
