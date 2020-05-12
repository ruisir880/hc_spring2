package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.core.service.PageQueryService;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.utils.ParseUtil;
import com.ray.hc_spring2.web.dto.AlarmLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    private List<AlarmLogDto> latestAlarmLogs = new ArrayList<>();

    @Autowired
    private PageQueryService pageQueryService;
    @Autowired
    private ParseUtil parseUtil;

    @RequestMapping(value ="/main")
    public ModelAndView mainHtml() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        modelAndView.addObject("url","rtsp://admin:Special101@192.168.1.64:554/h264/ch1/main/av_stream");
        modelAndView.addObject("url2","rtsp://admin:Special101@192.168.1.65:554/h264/ch1/main/av_stream");
        modelAndView.addObject("alarmLogList",getLatestAlarmLogs());
        return modelAndView;
    }

    @RequestMapping(value = "/latestLog")
    @ResponseBody
    public List<AlarmLogDto> getLatestAlarmLogs() {
        Page<AlarmLog> logs = pageQueryService.queryAlarmLog(1, null, null, null);
        if (latestAlarmLogs.size() < Constants.LATEST_LOG_SIZE) {
            latestAlarmLogs.clear();
            AlarmLogDto logDto;
            for (int i = 0; i < 5 && i < logs.getTotalElements(); i++) {
                logDto = parseUtil.getAlarmLogDto(logs.getContent().get(i));
                logDto.setIndex(i + 1);
                latestAlarmLogs.add(logDto);
            }
        }
        return latestAlarmLogs;
    }


}
