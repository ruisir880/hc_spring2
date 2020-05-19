package com.ray.hc_spring2.utils;


import com.ray.hc_spring2.HCNetSDK;
import com.ray.hc_spring2.core.HcCache;
import com.ray.hc_spring2.core.SpringContextUtils;
import com.ray.hc_spring2.core.WarnComponent;
import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.core.repository.AlarmLogRepository;
import com.ray.hc_spring2.model.AlarmLog;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.web.config.MyWebSocket;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HCNetTools {
    private static Logger logger = LoggerFactory.getLogger(HCNetTools.class);
    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;//设备信息
    HCNetSDK.NET_DVR_IPPARACFG  m_strIpparaCfg;//IP参数
    HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;//用户参数

    boolean bRealPlay;//是否在预览.
    String m_sDeviceIP;//已登录设备的IP地址

    NativeLong lUserID;//用户句柄
    NativeLong lPreviewHandle;//预览句柄
    NativeLongByReference m_lPort;//回调预览时播放库端口指针
    FMSGCallBack fMSFCallBack;//报警回调函数实现
    NativeLong lAlarmHandle;//报警布防句柄

    private AlarmLogRepository alarmLogRepository;
    private WarnComponent warnComponent;
    private MyWebSocket myWebSocket;
    private HcCache hcCache;

    public HCNetTools()
    {
        lUserID = new NativeLong(-1);
        lPreviewHandle = new NativeLong(-1);
        m_lPort = new NativeLongByReference(new NativeLong(-1));
        warnComponent = SpringContextUtils.getApplicationContext().getBean(WarnComponent.class);
        hcCache = SpringContextUtils.getApplicationContext().getBean(HcCache.class);
    }

    public int testDevice(HcDevice hcDevice){
        try {
            int code = 0;
            if (initDevices() == 1) {
                return 1;//初始化失败
            }
            int regSuc = deviceRegist(hcDevice.getAccount(), hcDevice.getPassword(), hcDevice.getIp(), hcDevice.getPort());
            if (regSuc != 0) {
                code = regSuc;//注册失败
            }
            return code;
        }finally {
            shutDownDev();
        }
    }

    /**
     * 初始化资源配置
     */
    public int initDevices(){
        if(!hCNetSDK.NET_DVR_Init()) return 1;//初始化失败
        return 0;
    }
    /**
     * 设备注册
     * @param name 设备用户名
     * @param password 设备登录密码
     * @param ip IP地址
     * @param port 端口
     * @return 结果
     */
    public int deviceRegist(String name,String password,String ip,String port){
        if (bRealPlay){//判断当前是否在预览
            return 2;//"注册新用户请先停止当前预览";
        }
        if (lUserID.longValue() > -1){//先注销,在登录
            hCNetSDK.NET_DVR_Logout_V30(lUserID);
            lUserID = new NativeLong(-1);
        }
        //注册(既登录设备)开始
        m_sDeviceIP = ip;
        m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();//获取设备参数结构
        lUserID = hCNetSDK.NET_DVR_Login_V30(m_sDeviceIP,(short)Integer.parseInt(port),name,password, m_strDeviceInfo);//登录设备
        if (lUserID.longValue() == -1){
            m_sDeviceIP = "";//登录未成功,IP置为空
            return 3;//"注册失败";
        }
        return 0;
    }


    /**
     * 获取设备通道
     */
    public int getChannelNumber(){
        IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
        boolean bRet = false;
        int iChannelNum = -1;

        m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        m_strIpparaCfg.write();
        Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
        bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
        m_strIpparaCfg.read();

        String devices = "";
        if (!bRet){
            //设备不支持,则表示没有IP通道
            for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++){
                devices = "Camera" + (iChannum + m_strDeviceInfo.byStartChan);
            }
        }else{
            for(int iChannum =0; iChannum < HCNetSDK.MAX_IP_CHANNEL; iChannum++) {
                if (m_strIpparaCfg.struIPChanInfo[iChannum].byEnable == 1) {
                    devices = "IPCamera" + (iChannum + m_strDeviceInfo.byStartChan);
                }
            }
        }
        if(StringUtils.isNotEmpty(devices)){
            if(devices.charAt(0) == 'C'){//Camara开头表示模拟通道
                //子字符串中获取通道号
                iChannelNum = Integer.parseInt(devices.substring(6));
            }else{
                if(devices.charAt(0) == 'I'){//IPCamara开头表示IP通道
                    //子字符创中获取通道号,IP通道号要加32
                    iChannelNum = Integer.parseInt(devices.substring(8)) + 32;
                }else{
                    return 4;
                }
            }
        }
        return iChannelNum;
    }

    public void shutDownDev(){
        //如果已经注册,注销
            if (lUserID.longValue() > -1){
            hCNetSDK.NET_DVR_Logout_V30(lUserID);
        }
        hCNetSDK.NET_DVR_Cleanup();
    }

    public boolean startAlarm(HcDevice device) {
        int result = deviceRegist(device.getAccount(), device.getPassword(), device.getIp(), device.getPort());
        MessageUtil.register(result, device.getIp());
        if (result == 0) {
            if (fMSFCallBack == null)
            {
                fMSFCallBack = new FMSGCallBack();
            }
            Pointer pUser = null;
            if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(fMSFCallBack, pUser))
            {
                logger.error("设置回调函数失败!");
            }

            lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V30(lUserID);
            if (lAlarmHandle.intValue() == -1)
            {
                //布放失败
                return false;
            }
        }else {
            logger.warn(device.getIp() + "注册失败。");
            return false;
        }
        return true;
    }

    public boolean cancelAlarm(HcDevice device) {
        if (!hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
            //撤防失败
            lAlarmHandle = new NativeLong(-1);
            return false;
        } else
        {
            lAlarmHandle = new NativeLong(-1);
            return true;
        }
    }


    /******************************************************************************
     *内部类:   FMSGCallBack
     *报警信息回调函数
     ******************************************************************************/
    public class FMSGCallBack implements HCNetSDK.FMSGCallBack
    {
        //报警信息回调函数

        public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.RECV_ALARM pAlarmInfo, int dwBufLen, Pointer pUser)
        {
            String sAlarmType = new String();
            //todo 插入数据库
            AlarmLog alarmLog = new AlarmLog();
            alarmLog.setAlarmTime(new Date());
            alarmLog.setDeviceIp(new String(pAlarmer.sDeviceIP).split("\0", 2)[0]);

            //lCommand是传的报警类型
            switch (lCommand.intValue())
            {
                //9000报警
                case HCNetSDK.COMM_ALARM_V30:
                    HCNetSDK.NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new HCNetSDK.NET_DVR_ALARMINFO_V30();
                    strAlarmInfoV30.write();
                    Pointer pInfoV30 = strAlarmInfoV30.getPointer();
                    pInfoV30.write(0, pAlarmInfo.RecvBuffer, 0, strAlarmInfoV30.size());
                    strAlarmInfoV30.read();

                    switch (strAlarmInfoV30.dwAlarmType)
                    {
                        case 0:
                            sAlarmType = new String("信号量报警");
                            break;
                        case 1:
                            sAlarmType = new String("硬盘满");
                            break;
                        case 2:
                            sAlarmType = new String("信号丢失");
                            break;
                        case 3:
                            sAlarmType = new String("移动侦测");
                            break;
                        case 4:
                            sAlarmType = new String("硬盘未格式化");
                            break;
                        case 5:
                            sAlarmType = new String("读写硬盘出错");
                            break;
                        case 6:
                            sAlarmType = new String("遮挡报警");
                            break;
                        case 7:
                            sAlarmType = new String("制式不匹配");
                            break;
                        case 8:
                            sAlarmType = new String("非法访问");
                            break;
                    }
                    alarmLog.setAlarmType(sAlarmType);
                    break;

                //8000报警
                case HCNetSDK.COMM_ALARM:
                    HCNetSDK.NET_DVR_ALARMINFO strAlarmInfo = new HCNetSDK.NET_DVR_ALARMINFO();
                    strAlarmInfo.write();
                    Pointer pInfo = strAlarmInfo.getPointer();
                    pInfo.write(0, pAlarmInfo.RecvBuffer, 0, strAlarmInfo.size());
                    strAlarmInfo.read();


                    switch (strAlarmInfo.dwAlarmType)
                    {
                        case 0:
                            sAlarmType = new String("信号量报警");
                            break;
                        case 1:
                            sAlarmType = new String("硬盘满");
                            break;
                        case 2:
                            sAlarmType = new String("信号丢失");
                            break;
                        case 3:
                            sAlarmType = new String("移动侦测");
                            break;
                        case 4:
                            sAlarmType = new String("硬盘未格式化");
                            break;
                        case 5:
                            sAlarmType = new String("读写硬盘出错");
                            break;
                        case 6:
                            sAlarmType = new String("遮挡报警");
                            break;
                        case 7:
                            sAlarmType = new String("制式不匹配");
                            break;
                        case 8:
                            sAlarmType = new String("非法访问");
                            break;
                    }
                    alarmLog.setAlarmType(sAlarmType);
                    break;

                //ATM DVR transaction information
                case HCNetSDK.COMM_TRADEINFO:
                    //处理交易信息报警
                    break;

                //IPC接入配置改变报警
                case HCNetSDK.COMM_IPCCFG:
                    // 处理IPC报警
                    break;

                default:
                    alarmLog.setAlarmType("未知");
                    break;
            }
            logger.warn(alarmLog.getDeviceIp()+"报警, 报警类型:"+ alarmLog.getAlarmType());
             warnComponent.addWarn(hcCache.getAreaByIp(alarmLog.getDeviceIp()), Constants.SYSTEM_LOCAL);

            // alarmLogRepository.save(alarmLog);
        }
    }

    public void stopRealPlay(){
        m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
        m_strClientInfo.lChannel = new NativeLong(1);
        hCNetSDK.NET_DVR_StopRealPlay(lUserID);
        shutDownDev();
    }

}
