package com.ray.hc_spring2.model;

import com.ray.hc_spring2.core.HcCache;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class HcDevice implements Serializable {

    @Id
    //数据库在插入数据时，会自动给主键赋值
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// 用户id
    @Column(unique = true,nullable = false)
    private String ip;
    @Column(nullable = false)
    private String account;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String port;

    @Column
    private String deviceType;

    @ManyToOne
    @JoinColumn(name = "defense_area_id")
    private DefenseArea defenseArea;



    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DefenseArea getDefenseArea() {
        return defenseArea;
    }

    public void setDefenseArea(DefenseArea defenseArea) {
        this.defenseArea = defenseArea;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof HcDevice && ((HcDevice) obj).getId().equals(this.id) && ((HcDevice) obj).getIp().equals(ip);
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
