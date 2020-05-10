package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.service.PageQueryService;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.model.OperationLog;
import com.ray.hc_spring2.utils.DateUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class AlarmLogController {

    @Autowired
    private PageQueryService pageQueryService;

    @RequestMapping(value ="/alarmLogList")
    public ModelAndView alarmLogList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("alarmLogList");
        return modelAndView;
    }

    @RequestMapping(value = "/queryAlarmLog",method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView queryUserPage(String page,String defenseArea,String alarmState,String startTime,String endTime) throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        int pageInt = StringUtils.isEmpty(page) ? 1 : Integer.valueOf(page);
        Calendar calendar = Calendar.getInstance();
        Date startDate = null;
        Date endDate = null;
        if (!StringUtils.isEmpty(startTime)) {
            startDate = DateUtil.parseDate(startTime);
        }

        if (!StringUtils.isEmpty(endTime)) {
            endDate = DateUtil.parseDate(endTime);
        }

        Page<AlarmLog> alarmLogs = pageQueryService.queryAlarmLog(pageInt, alarmState, startDate, endDate);
        modelAndView.addObject("alarmPage", alarmLogs);
        modelAndView.addObject("totalCount", alarmLogs.getTotalElements());
        modelAndView.addObject("page", alarmLogs.getNumber() + 1);

        modelAndView.setViewName("alarmLogList");
        return modelAndView;
    }
}
