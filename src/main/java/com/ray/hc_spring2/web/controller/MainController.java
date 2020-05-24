package com.ray.hc_spring2.web.controller;

import com.ray.hc_spring2.core.ModbusComponent;
import com.ray.hc_spring2.core.OperationLogComponent;
import com.ray.hc_spring2.core.repository.AlarmLogRepository;
import com.ray.hc_spring2.core.repository.DefenseAreaRepository;
import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.core.service.PageQueryService;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.model.DefenseArea;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.ParseUtil;
import com.ray.hc_spring2.web.dto.AlarmLogDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {
    private static Logger log = LoggerFactory.getLogger(MainController.class);

    private List<AlarmLogDto> latestAlarmLogs = new ArrayList<>();

    public static final String RTSP = "rtsp://%s:%s@%s:554/h264/ch1/main/av_stream";

    @Autowired
    private PageQueryService pageQueryService;
    @Autowired
    private ParseUtil parseUtil;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ModbusComponent modbusComponent;
    @Autowired
    private OperationLogComponent operationLogComponent;
    @Autowired
    private AlarmLogRepository alarmLogRepository;
    @Autowired
    private DefenseAreaRepository defenseAreaRepository;

    @RequestMapping(value ="/main")
    public ModelAndView mainHtml(String area) {
        ModelAndView modelAndView = new ModelAndView();
        area = StringUtils.isBlank(area) ? "1" : area;
        List<HcDevice> ipcList = deviceService.findByArea(area);
        List<String> list = new ArrayList<>();
        for (HcDevice ipc : ipcList) {
            list.add(String.format(RTSP, ipc.getAccount(), ipc.getPassword(), ipc.getIp()));
        }
        List<DefenseArea> defenseAreas = defenseAreaRepository.findAll();
        modelAndView.addObject("urlList", list);
        modelAndView.addObject("defenseAreas", defenseAreas);
        modelAndView.addObject("alarmLogList", getLatestAlarmLogs());
        modelAndView.setViewName("main");
        return modelAndView;
    }

    @RequestMapping(value = "/latestLog")
    @ResponseBody
    public List<AlarmLogDto> getLatestAlarmLogs() {
        Page<AlarmLog> logs = pageQueryService.queryAlarmLog(1, null,null, null, null);
        latestAlarmLogs.clear();
        AlarmLogDto logDto;
        for (int i = 0; i < 5 && i < logs.getTotalElements(); i++) {
            logDto = parseUtil.getAlarmLogDto(logs.getContent().get(i));
            logDto.setIndex(i + 1);
            latestAlarmLogs.add(logDto);
        }
        return latestAlarmLogs;
    }


    @RequestMapping(value ="/getVideoList")
    @ResponseBody
    public List<String> getVideoList(String area) {
        List<String> list = new ArrayList<>();
        area = StringUtils.isBlank(area) ? "1" : area;
        List<HcDevice> ipcList = deviceService.findByArea(area);

        for (HcDevice ipc : ipcList) {
            list.add(String.format(RTSP, ipc.getAccount(), ipc.getPassword(), ipc.getIp(), ipc.getPort()));
        }
        return list;
    }


    @PostMapping(value ="/closeController")
    @ResponseBody
    public int closeController() {
        operationLogComponent.operate("关闭报警");
        try {
            modbusComponent.stopWarn();
        }catch (Exception e){
            log.error("一键消除报警失败",e);
            return -1;
        }

        return 0;
    }

    @PostMapping(value ="/startController")
    @ResponseBody
    public int startController() {
        operationLogComponent.operate("打开报警");
        try {
            modbusComponent.startWarnArea(Arrays.asList(1,2,3,4));
        }catch (Exception e){
            log.error("一键打开报警失败",e);
            return -1;
        }

        return 0;
    }


    @PostMapping(value ="/dealWarn")
    @ResponseBody
    public int dealWarn(long id) {
        operationLogComponent.operate("处理报警");
        AlarmLog alarmLog = alarmLogRepository.findById(id);
        alarmLog.setState("已处理");
        alarmLog.setEndTime(new Date());
        alarmLogRepository.save(alarmLog);
        return 0;
    }

}
