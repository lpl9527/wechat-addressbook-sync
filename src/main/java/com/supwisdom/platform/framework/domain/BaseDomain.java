package com.supwisdom.platform.framework.domain;

import java.io.Serializable;

public abstract class BaseDomain implements Serializable {

    private static final long serialVersionUID = -6213813976320269799L;
    /**
     * 每一个表的主键
     */

    private String id;

    /**
     * 额外的信息
     */

    private Object extras;

    /**
     * 获取主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID属性,主要用于人工指定键值
     */
    public void setId(String id) {
        this.id = id;
    }

    public Object getExtras() {
        return extras;
    }

    public void setExtras(Object extras) {
        this.extras = extras;
    }

}
