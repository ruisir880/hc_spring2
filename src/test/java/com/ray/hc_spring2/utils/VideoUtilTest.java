package com.ray.hc_spring2.utils;

import com.ray.hc_spring2.HCNetSDK;
import com.ray.hc_spring2.model.HcDevice;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class VideoUtilTest {


    @Test
    public void webSocketTest(){
        HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
        NativeLong lUserID = new NativeLong(-1);
        hCNetSDK.NET_DVR_Init();

        HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        lUserID = hCNetSDK.NET_DVR_Login_V30("192.168.1.2", Short.valueOf("8000"), "admin", "special101", m_strDeviceInfo);

        HCNetSDK.NET_DVR_FILECOND m_strFilecond = new HCNetSDK.NET_DVR_FILECOND();
        m_strFilecond.struStartTime = new HCNetSDK.NET_DVR_TIME();
        m_strFilecond.struStopTime = new HCNetSDK.NET_DVR_TIME();
        m_strFilecond.struStartTime.dwYear = 2020;
        m_strFilecond.struStartTime.dwMonth = 5;
        m_strFilecond.struStartTime.dwDay = 16;
        m_strFilecond.struStartTime.dwHour = 0;
        m_strFilecond.struStartTime.dwMinute = 0;
        m_strFilecond.struStartTime.dwSecond = 0;
        m_strFilecond.struStopTime.dwYear = 2020;//结束时间
        m_strFilecond.struStopTime.dwMonth = 5;
        m_strFilecond.struStopTime.dwDay = 16;
        m_strFilecond.struStopTime.dwHour = 22;
        m_strFilecond.lChannel = new NativeLong(1);//通道号

        NativeLong lFindFile = hCNetSDK.NET_DVR_FindFile_V30(lUserID, m_strFilecond);

        //setDVRConfigSuc是true，但是nvr并没有改变；
        hCNetSDK.NET_DVR_Logout(lUserID);
        hCNetSDK.NET_DVR_Cleanup();
    }


    @Test
    public void testSocket() throws IOException, InterruptedException {
        Socket s = new Socket(InetAddress.getLocalHost(),10010);
        //获取输出流对象
        OutputStream os = s.getOutputStream();

     /*   for(int i =0;i<3;i++) {*/
            String str = "10009,变电站,2010-05-14 10:09:00,防区1";
            os.write(str.getBytes());
            os.flush();
            Thread.sleep(5000);
      /*  }
        s.close();

        System.out.println("防区1".substring(2));*/
    }
}
