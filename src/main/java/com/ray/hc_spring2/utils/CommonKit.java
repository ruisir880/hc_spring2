package com.ray.hc_spring2.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by YingLin.Hu on 2018/8/8 16:02.
 */
public class CommonKit {

    private static Logger logger = LoggerFactory.getLogger(CommonKit.class);
    /**
     * userlist根据id去重
     * @param userList
     * @return
     */
  /*  public static ArrayList<User> removeDuplicteUser(List<User> userList) {
        Set<User> s = new TreeSet<User>(new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                String v1 = u1.getId() != null ? u1.getId() : "0";
                String v2 = u2.getId() != null ? u2.getId() : "0";
                return v1.compareTo(v2);
            }
        });
        s.addAll(userList);
        return new ArrayList<User>(s);
    }*/
    /**
     * 根据前端传递的集合参数中取到指定属性的值
     */
    public static List getCols(List vo,String colName) {
        List<String> list = new ArrayList<>();
        for (Object obj : vo) {
            list.add((String) ((LinkedHashMap) obj).get(colName));
        }
        return list;
    }

    public static String join(String[] strs , String separator){
        String result = "";
        for (int i = 0; i < strs.length; i++) {
            if(i == 0){
                result += strs[i];
            }else{
                result += separator+strs[i];
            }
        }
        return result;
    }

    /**
     * 获取项目webapp目录
     * @return
     */
    public static String getWebPath(){
        String path = Thread.currentThread().getContextClassLoader().getResource("hclib").getPath();
        logger.info(path+"\\HCNetSDK");
        return path;
    }

    /**
     * 获取本机ip
     * @return
     */
    public static String getServerIp() {
        // 获取操作系统类型
        String sysType = System.getProperties().getProperty("os.name");
        String ip;
        if (sysType.toLowerCase().startsWith("win")) {  // 如果是Windows系统，获取本地IP地址
            String localIP = null;
            try {
                localIP = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (localIP != null) {
                return localIP;
            }
        } else {
            ip = getIpByEthNum("eth0"); // 兼容Linux
            if (ip != null) {
                return ip;
            }
        }
        return "获取服务器IP错误";
    }

    /**
     * 根据网络接口获取IP地址
     * @param ethNum 网络接口名，Linux下是eth0
     * @return
     */
    private static String getIpByEthNum(String ethNum) {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (ethNum.equals(netInterface.getName())) {
                    Enumeration addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = (InetAddress) addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "获取服务器IP错误";
    }

    public static void main(String[] args) {
        String ip = "10.192.44.101";
        String[] a = ip.split("\\.");
        String b = CommonKit.join(a,"");
        System.out.println(a+b);
    }

}
