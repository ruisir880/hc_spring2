package com.ray.hc_spring2.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ray.hc_spring2.core.constant.Constants;
import com.ray.hc_spring2.core.repository.DeviceRepository;
import com.ray.hc_spring2.core.service.DeviceService;
import com.ray.hc_spring2.core.service.PageQueryService;
import com.ray.hc_spring2.model.HcDevice;
import com.ray.hc_spring2.utils.HCNetTools;
import com.ray.hc_spring2.utils.NvrTools;
import com.ray.hc_spring2.web.dto.NvrChannelInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Component
public class HcCache {
    private static Logger logger = LoggerFactory.getLogger(HCNetTools.class);
    private Cache<String,String> ipDefenseAreaCache = CacheBuilder.newBuilder().softValues().build();
    private List<NvrChannelInfoDto> ipChannelCache = new ArrayList<>();

    private static HcCache hcCache;

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private PageQueryService pageQueryService;
    @Autowired
    private DeviceService deviceService;
    private NvrTools nvrTools = new NvrTools();

    public static HcCache getInstance(){
        if(hcCache == null){
            hcCache = new HcCache();
        }
        return hcCache;
    }

    public String getAreaByIp(String ip){
        try {
            if(StringUtils.isBlank(ip)){
                return Constants.UNKNOWN;
            }
            return ipDefenseAreaCache.get(ip, new Callable<String>() {
                 @Override
                 public String call() throws Exception {
                     HcDevice device = deviceRepository.findByIp(ip);
                     if(device !=null){
                         return device.getDefenseArea().getDefenseName();
                     }
                     return Constants.UNKNOWN;
                 }
             });
        } catch (ExecutionException e) {
            logger.error("search cache error",e);
        }
        return Constants.UNKNOWN;
    }


    public void clearIpDefenseAreaCache(String ip){
        ipDefenseAreaCache.invalidate(ip);

    }

    public List<NvrChannelInfoDto> getIpChannelCache(){
      /*  if(ipChannelCache.size()>0){
            return ipChannelCache;
        }*/
        HcDevice device = deviceService.findNvr();
        if (device != null) {
            if(nvrTools.register(device.getIp(),device.getPort(),device.getAccount(),device.getPassword())) {
                ipChannelCache = nvrTools.getNvrChannelInfo();
                nvrTools.release();
            }
        }
        return ipChannelCache;
    }

    public void updateIpChannelCache(List<NvrChannelInfoDto> channelInfoDtos){
        ipChannelCache=channelInfoDtos;
    }
}
