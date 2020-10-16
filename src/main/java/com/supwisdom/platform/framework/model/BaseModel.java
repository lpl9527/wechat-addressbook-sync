package com.supwisdom.platform.framework.model;

import java.io.Serializable;

/**
 * 基础类
 * @author StevenChang
 *
 */
public class BaseModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8002286879041932651L;
	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
