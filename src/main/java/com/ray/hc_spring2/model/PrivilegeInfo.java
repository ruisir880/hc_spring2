package com.ray.hc_spring2.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by rui on 2018/8/12.
 */
@Entity
public class PrivilegeInfo implements Serializable {

    private static final long serialVersionUID = -8572709283492201065L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;// 主键.

    private String privilegeName;// 名称.

    private String description;


  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "SysRolePermission", joinColumns = { @JoinColumn(name = "permissionId") }, inverseJoinColumns = {
          @JoinColumn(name = "roleId") })
  private List<RoleInfo> roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}