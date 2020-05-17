package com.ray.hc_spring2.utils;

import com.ray.hc_spring2.HCNetSDK;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.web.dto.NvrChannelInfoDto;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NvrTools {
    private static Logger logger = LoggerFactory.getLogger(NvrTools.class);
    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
    HCNetSDK.NET_DVR_DEVICEINFO_V30 struDeviceInfo;
    NativeLong lUserID;//用户句柄
    HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg;//IP参数
    private int m_iCurChanIndex;

    private boolean m_bGetIpCfg;
    public static final int ANALOG_CHAN_DISABLE = 0;
    public static final int ANALOG_CHAN_ENABLE = 1;
    public static final int IP_CHAN_DISABLE = 2;
    public static final int IP_CHAN_ENABLE = 3;


    public NvrTools() {
        lUserID = new NativeLong(-1);
    }

    public boolean register(String ip, String port, String account, String password) {
        hCNetSDK.NET_DVR_Init();
        struDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        lUserID = hCNetSDK.NET_DVR_Login_V30(ip, Short.valueOf(port), account, password, struDeviceInfo);
        if (lUserID.longValue() < 0) {
            logger.warn("注册NVR失败");
            hCNetSDK.NET_DVR_Cleanup();
            return false;
        }
        readChannelConf();
        return true;
    }

    private void readChannelConf() {
        IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
        m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        m_strIpparaCfg.write();
        Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
        m_bGetIpCfg = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
        m_strIpparaCfg.read();
    }

    public void setChannel(int channelNo, HcDevice device) {
        readChannelConf();
        int iIPChanIndex = channelNo-1;
        int i;
        int dwID = 0;
        boolean bExistDev = false;
        dwID = m_strIpparaCfg.struIPChanInfo[iIPChanIndex].byIPID;//current connetcted IP device ID
        //this ip channel is connected to devices
        if (dwID > 0) {
            for (int j = 0; j < HCNetSDK.MAX_IP_CHANNEL; j++) {
                if (iIPChanIndex == j) {
                    continue;
                }
                if (m_strIpparaCfg.struIPChanInfo[j].byIPID == dwID) {
                    bExistDev = true;
                    break;
                }
            }

            if (!bExistDev) {
                //this IP device is not connected in other IP channels, clear its device info
                m_strIpparaCfg.struIPDevInfo[dwID - 1] = new HCNetSDK.NET_DVR_IPDEVINFO();
            }
        }
        //clear device info
        m_strIpparaCfg.struIPChanInfo[iIPChanIndex] = new HCNetSDK.NET_DVR_IPCHANINFO();


        bExistDev = false;
        dwID = 0;

        for (i = 0; i < hCNetSDK.MAX_IP_DEVICE; i++)
        {
            //find existed device
            String sIP1 = toIPString(new String(m_strIpparaCfg.struIPDevInfo[i].struIP.sIpV4));//结构体里的设备IP

            if (device.getIp().equals(sIP1) )
            {
                dwID = i+1;
                System.out.println(String.format("dev[%d]ip[%s]exist", dwID, m_strIpparaCfg.struIPDevInfo[i].struIP.sIpV4));
                for (int j = 0; j< HCNetSDK.MAX_IP_CHANNEL; j++)
                {
                    if ((m_strIpparaCfg.struIPChanInfo[j].byIPID == dwID) && (iIPChanIndex == m_strIpparaCfg.struIPChanInfo[j].byChannel))
                    {
                        System.out.println(String.format("ipdev[%d]ip[%s]exist, chan[%d] had added", dwID, m_strIpparaCfg.struIPDevInfo[i].struIP.sIpV4, iIPChanIndex));
                       // JOptionPane.showMessageDialog(this, "对应IP设备的通道已经被添加");
                        return;
                    }
                }
                bExistDev = true;
                System.out.println(String.format("ipdev[%d]ip[%s]existed and chan[%d] had not been added", dwID, m_strIpparaCfg.struIPDevInfo[i].struIP.sIpV4, iIPChanIndex));
                break;
            }
        }

        if (dwID == 0)//new device
        {
            for (i = 0; i< HCNetSDK.MAX_IP_DEVICE; i++)
            {
                if (m_strIpparaCfg.struIPDevInfo[i].dwEnable != 1)
                {
                    dwID = i + 1;//find the first empty node
                    break;
                }
            }
        }

        m_strIpparaCfg.struIPChanInfo[iIPChanIndex].byEnable = 0;
        m_strIpparaCfg.struIPChanInfo[iIPChanIndex].byChannel = (byte) 1;
        m_strIpparaCfg.struIPChanInfo[iIPChanIndex].byIPID = (byte) dwID;

        if (!bExistDev) {
            m_strIpparaCfg.struIPDevInfo[dwID - 1].struIP.sIpV4 = (device.getIp() + "\0").getBytes();
            m_strIpparaCfg.struIPDevInfo[dwID - 1].dwEnable = 1;
            m_strIpparaCfg.struIPDevInfo[dwID - 1].sUserName = (device.getAccount() + "\0").getBytes();
            m_strIpparaCfg.struIPDevInfo[dwID - 1].sPassword = (device.getPassword() + "\0").getBytes();
            m_strIpparaCfg.struIPDevInfo[dwID - 1].wDVRPort = (short) Integer.parseInt(device.getPort());
        }

    }

    public void deleteChannel(int channelNo) {
        int channelIndex = channelNo-1;
        readChannelConf();
        if (channelIndex < 0 || channelIndex >= HCNetSDK.MAX_IP_CHANNEL) {
            logger.warn("illegal channel:" + channelIndex);
            return;
        }
        int dwID = m_strIpparaCfg.struIPChanInfo[channelIndex].byIPID;
        if (dwID <= 0) {
            logger.warn(String.format("the ip %s device error!", channelIndex));
            return;
        }

        m_strIpparaCfg.struIPDevInfo[dwID - 1] = new HCNetSDK.NET_DVR_IPDEVINFO();
        m_strIpparaCfg.struIPChanInfo[channelIndex] = new HCNetSDK.NET_DVR_IPCHANINFO();
    }

    public void deleteAllChanel(){
        for (int i = 0; i < HCNetSDK.MAX_IP_CHANNEL; i++)
        {
            if (m_strIpparaCfg.struIPChanInfo[i].byIPID != 0)
                deleteChannel(i);
            }

    }


    public boolean saveChange() {
        m_strIpparaCfg.write();
        Pointer lpNetConfig = m_strIpparaCfg.getPointer();
        boolean setDVRConfigSuc = hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_IPPARACFG,
                new NativeLong(0), lpNetConfig, m_strIpparaCfg.size());
        return setDVRConfigSuc;
    }


    public void release() {
        hCNetSDK.NET_DVR_Logout(lUserID);
        hCNetSDK.NET_DVR_Cleanup();
    }

    public void rePlay() {
    }

    private String toIPString(String s)
    {
        String[] sIP = new String[2];//结构体里的设备IP
        sIP = s.split("\0", 2);
        return sIP[0];
    }

    public List<NvrChannelInfoDto> getNvrChannelInfo() {

        int dwChanNum = 0;
        int iIndex = 0;
        int dwIPChanIndex = 0;
        int dwID = 0;
        List<NvrChannelInfoDto> nvrChannelInfoDtos = new ArrayList<>();
        for (int i = 0; i < hCNetSDK.MAX_CHANNUM_V30; i++) {
            NvrChannelInfoDto newRow = new NvrChannelInfoDto();
            if (i >= HCNetSDK.MAX_ANALOG_CHANNUM && i < (struDeviceInfo.byIPChanNum + HCNetSDK.MAX_ANALOG_CHANNUM)) {
                dwIPChanIndex = i - HCNetSDK.MAX_ANALOG_CHANNUM;
                dwChanNum = dwIPChanIndex + struDeviceInfo.byStartChan;
                dwID = m_strIpparaCfg.struIPChanInfo[dwIPChanIndex].byIPID;
                newRow.setNo(String.format("%02d", iIndex + 1));
                newRow.setChannelNo("IPCamera" + dwChanNum);

                if (m_strIpparaCfg.struIPChanInfo[dwIPChanIndex].byIPID != 0) {
                    newRow.setIpcIp(toIPString(new String(m_strIpparaCfg.struIPDevInfo[dwID - 1].struIP.sIpV4)));
                    newRow.setIpcPort(m_strIpparaCfg.struIPDevInfo[dwID - 1].wDVRPort + "");
                    newRow.setIpChannel(m_strIpparaCfg.struIPChanInfo[dwIPChanIndex].byChannel + "");
                    if (m_strIpparaCfg.struIPChanInfo[dwIPChanIndex].byEnable == 1) {
                        newRow.setOnline("在线");
                    } else {
                        newRow.setOnline("离线");
                    }
                } else {
                    newRow.setIpcIp("0.0.0.0");
                    newRow.setIpcPort("0");
                    newRow.setIpChannel("0");
                    newRow.setOnline("否");
                }
                iIndex++;
                nvrChannelInfoDtos.add(newRow);
            }

        }
        return nvrChannelInfoDtos;
    }
}
