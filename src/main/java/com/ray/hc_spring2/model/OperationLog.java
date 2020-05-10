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
    private Date generationTime;


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



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(Date generationTime) {
        this.generationTime = generationTime;
    }
}
