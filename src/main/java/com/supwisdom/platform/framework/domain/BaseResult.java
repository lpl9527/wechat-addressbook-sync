package com.supwisdom.platform.framework.domain;

import java.io.Serializable;

/**
 * Ajax处理结果对象
 * @author hush
 */
public class BaseResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6833603354573088719L;
    /**
     * 状态信息,正确返回OK,否则返回 ERROR
     */
    private Status status;
    /**
     * 消息对象,在当前状态下返回的信息内容
     */
    private Object response;

    public BaseResult() {
        super();
    }

    /**
     * 对象初始化
     * @param status  状态
     * @param message  消息
     */
    public BaseResult(Status status, Object response) {
        this.status = status;
        this.response = response;
    }

    /**
     * 结果类型信息
     */
    public enum Status {
        OK, ERROR
    }

    /**
     * 添加成功结果信息
     * @param message 返回的信息可以是字符串
     */
    public void addOK(Object response) {
        this.response = response;
        this.status = Status.OK;
    }

    /**
     * 添加错误消息
     * @param message
     */
    public void addError(Object response) {
        this.response = response;
        this.status = Status.ERROR;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

}
