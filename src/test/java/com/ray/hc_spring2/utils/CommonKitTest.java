package com.ray.hc_spring2.utils;

import com.ray.hc_spring2.HCNetSDK;
import com.sun.jna.Native;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CommonKitTest {

    @Test
    public void getWebPath() {
        System.out.println(CommonKit.getWebPath());
        HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary(CommonKit.getWebPath()+"\\HCNetSDK", HCNetSDK.class);
    }
}