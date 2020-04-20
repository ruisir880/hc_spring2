package com.ray.hc_spring2.utils;

import cc.eguid.commandManager.CommandManager;
import cc.eguid.commandManager.CommandManagerImpl;
import com.ray.hc_spring2.model.Device;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class VideoUtil {

    private static CommandManager manager;

    private HCNetTools hcTool = new HCNetTools();

    /**
     * 开始推流并获取推流进程名称
     * @param device 设备信息
     * @param appName 进程名命名
     * @return 返回结果集{msg:"",tasker:""}
     */
    public Map<String,String> startTranscodeAndGetTasker(Device device, String appName){
        Map<String,String> result = new HashMap<>();
        int code = 0;
        String[] resultNames = {"成功","初始化失败","注册前请关闭预览","注册失败","通道获取失败"};
        if(device.getType() == 1){//海康设备
            if(hcTool.initDevices() == 1) code = 1;//初始化失败
            int regSuc = hcTool.deviceRegist(device.getAccount(),device.getPassword(),device.getIp(),device.getPort());
            if(regSuc != 0) code = regSuc;//注册失败
            int channelNumber = hcTool.getChannelNumber();
            if(channelNumber == -1) code = 4;
            if(code == 0) {
                String tasker = VideoUtil.startTranscoding(appName,device.getAccount(),device.getPassword(),device.getIp(),channelNumber);
                result.put("tasker", (StringUtils.isNotEmpty(tasker)) ? tasker : appName);
            }else{
                result.put("msg",resultNames[code]);
            }
            hcTool.shutDownDev();
        }
        return result;
    }
    /**
     * 开始推流
     * @param appName 进程名称,为相机ip去"."
     * @param name 登录相机账号
     * @param password 密码
     * @param ip ip
     * @param channelNumber 通道号 获取rtsp流的参数
     * @return
     */
    public static String startTranscoding(String appName,String name,String password,String ip,int channelNumber)  {
        String channelNumberStr = "ch" + channelNumber;
        if(manager == null){
            manager = new CommandManagerImpl(10);
        }
        if(VideoUtil.taskerIsRun(appName)) return appName;//如果进程存在,则直接返回进程名
        Map<String,String> map = new HashMap<>();
        map.put("appName", appName);//进程名
        map.put("input", "rtsp://"+name+":"+password+"@" + ip + ":554/h264/"+channelNumberStr+"/main/av_stream");//组装rtsp流
        map.put("output", "rtmp://localhost:1935/live/");//rtmp流.live为nginx-rtmp的配置
        map.put("codec", "h264");
        map.put("fmt", "flv");
        map.put("fps", "25");
        map.put("rs", "640x360");
        map.put("twoPart", "1");
        // 执行任务，id就是appName，如果执行失败返回为null
        return manager.start(map);
    }


    /**
     * 关闭进程
     * @param tasker
     * @return
     */
    public static boolean stopTranscoding(String tasker){
        if(!VideoUtil.taskerIsRun(tasker)) return true;
        return manager.stop(tasker);
    }

    /**
     * 判断当前推流进程是否存在
     * @param appName 进程名称
     * @return 进程名称,为"0"时表示进程不存在
     */
    public static boolean taskerIsRun(String appName){
        return (manager.queryAll().size()>0 && manager.query(appName) != null);
    }
}  