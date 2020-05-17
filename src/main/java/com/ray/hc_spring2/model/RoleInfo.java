package com.ray.hc_spring2.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by rui on 2018/8/12.
 */
@Entity
public class RoleInfo implements Serializable {

    private static final long serialVersionUID = 4521945572364047866L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 编号

    @Column(nullable = false)
    private String roleName; // 角色标识程序中判断使用,如"admin",这个是唯一的:

    private String description; // 角色描述,UI界面显示使用

    // 角色 -- 权限关系：多对多关系;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "RolePrivilegeMap", joinColumns = { @JoinColumn(name = "roleId") }, inverseJoinColumns = {
            @JoinColumn(name = "privilegeId") })
    private List<PrivilegeInfo> permissions;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<PrivilegeInfo> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PrivilegeInfo> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "roleName='" + roleName + '\'' +
                '}';
    }
}