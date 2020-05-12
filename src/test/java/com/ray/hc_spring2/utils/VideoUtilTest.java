package com.ray.hc_spring2.utils;

import com.ray.hc_spring2.model.HcDevice;
import org.junit.Test;

public class VideoUtilTest {

    @Test
    public void startTranscodeAndGetTasker() {
        HcDevice hcDevice = new HcDevice();
        hcDevice.setAccount("admin");
        hcDevice.setPassword("Special101");
        hcDevice.setIp("192.168.1.64");
        hcDevice.setPort("8000");
        VideoUtil videoUtil = new VideoUtil();
        videoUtil.startTranscodeAndGetTasker(hcDevice,hcDevice.getIp().replace(".",""));
    }

    @Test
    public void webSocketTest(){

    }
}