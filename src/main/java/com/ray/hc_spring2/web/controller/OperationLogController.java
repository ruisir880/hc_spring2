package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.service.PageQueryService;
import com.ray.hc_spring2.model.OperationLog;
import com.ray.hc_spring2.utils.DateUtil;
import com.ray.hc_spring2.web.dto.OperationLogDto;
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

@Controller
public class OperationLogController {

    @Autowired
    private PageQueryService pageQueryService;


    @RequestMapping(value ="/operationLogList")
    public ModelAndView operationLogList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("operationLogList");
        return modelAndView;
    }

    @RequestMapping(value = "/queryOperationLog",method = RequestMethod.GET)
    @ResponseBody
    public OperationLogDto queryOperationLog(String page,String operator,String startTime,String endTime) throws ParseException {
        int pageInt = StringUtils.isEmpty(page) ? 1:Integer.valueOf(page);
        Calendar calendar = Calendar.getInstance();
        Date startDate = null;
        Date endDate = null;
        if(!StringUtils.isEmpty(startTime) ){
            startDate= DateUtil.parseDate(startTime);
        }

        if(!StringUtils.isEmpty(endTime)){
            endDate= DateUtil.parseDate(endTime);
        }

        Page<OperationLog> operationLogs = pageQueryService.queryOperationLog(pageInt,operator,startDate,endDate);

        return new OperationLogDto(operationLogs);
    }
}
