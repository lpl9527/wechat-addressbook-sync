package com.supwisdom.platform.model;

import com.supwisdom.platform.framework.model.BaseModel;

/**
 * 部门实体类
 *
 */
public class Department extends BaseModel {

    //部门id，根部门id为1
    private String id;

    //部门名称。
    private String name;

    //父部门id。
    private String pid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}