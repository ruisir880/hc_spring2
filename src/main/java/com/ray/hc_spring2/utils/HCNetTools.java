package com.ray.hc_spring2.utils;


import com.ray.hc_spring2.HCNetSDK;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;



/**
 * Created by Hanlex.Liu on 2018/9/22 11:55.
 */


public class HCNetTools {
    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;//设备信息
    HCNetSDK.NET_DVR_IPPARACFG  m_strIpparaCfg;//IP参数
    HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;//用户参数

    boolean bRealPlay;//是否在预览.
    String m_sDeviceIP;//已登录设备的IP地址

    int lUserID;//用户句柄
    NativeLong lPreviewHandle;//预览句柄
    NativeLongByReference m_lPort;//回调预览时播放库端口指针


    //FRealDataCallBack fRealDataCallBack;//预览回调函数实现

    public HCNetTools()
    {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);//防止被播放窗口(AWT组件)覆盖
        lUserID = -1;
        lPreviewHandle = new NativeLong(-1);
        m_lPort = new NativeLongByReference(new NativeLong(-1));
        //fRealDataCallBack= new FRealDataCallBack();
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
        if (lUserID > -1){//先注销,在登录
            hCNetSDK.NET_DVR_Logout_V30(lUserID);
            lUserID = -1;
        }
        //注册(既登录设备)开始
        m_sDeviceIP = ip;
        m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();//获取设备参数结构
        lUserID = hCNetSDK.NET_DVR_Login_V30(m_sDeviceIP,(short)Integer.parseInt("8000"),name,password, m_strDeviceInfo);//登录设备
        if (lUserID == -1){
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
        bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, 0, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
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
        if (lUserID > -1){
            hCNetSDK.NET_DVR_Logout_V30(lUserID);
        }
        hCNetSDK.NET_DVR_Cleanup();
    }




    public static void main(String[] args) {

    }


}
