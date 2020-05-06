package com.ray.hc_spring2.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class OperationLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;// 用户id
    @Column(nullable = false)
    private String operation;
    @Column(nullable = false)
    private String user;
    @Column(nullable = false)
    private Date date;


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
