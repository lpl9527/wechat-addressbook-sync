package com.supwisdom.platform.model;

import com.supwisdom.platform.framework.model.BaseModel;

public class Teacher extends BaseModel {

    private String gh;      //工号

    private String xm;      //姓名

    private String depts;     //企业微信单位码

    private String mobile;    //手机号

    private String email;     //邮箱号

    private String gender;    //性别

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGh() {
        return gh;
    }

    public void setGh(String gh) {
        this.gh = gh;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getDepts() {
        return depts;
    }

    public void setDepts(String depts) {
        this.depts = depts;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
