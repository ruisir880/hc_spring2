package com.ray.hc_spring2.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {
    private static Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    public static void register(int num, String ip){
        switch (num){
            case 0:
                logger.info(ip+"注册成功。");
                return;
            case 3:
                logger.warn(ip+"注册失败。");
                return;

        }
    }
}
