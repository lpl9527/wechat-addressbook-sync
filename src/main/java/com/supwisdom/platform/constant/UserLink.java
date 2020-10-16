package com.supwisdom.platform.constant;

/**
 * 通讯录用户管理接口
 */
public class UserLink {

    /**
     * 通讯录增量更新用户接口
     */
    public static final String USER_BATCH_UPDATE_URI = "/cgi-bin/batch/syncuser";
    /**
     * 批量删除用户接口
     */
    public static final String BATCH_DEL_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/batchdelete?access_token=";
}
