package com.supwisdom.platform.util;

import com.supwisdom.platform.constant.WechatLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;

/**
 *  获取企业微信token工具类
 */
public class AuthHelper {
	
    private static Logger logger = LoggerFactory.getLogger(AuthHelper.class);  
    
    //获取access_token的接口地址,有效期为7200秒
    private static final String GET_ACCESS_TOKEN_URL =
            WechatLink.API_HOST + WechatLink.GET_TOKEN_URI
                    + "?corpid=" + WechatLink.CROP_ID + "&corpsecret=" + WechatLink.CROP_SECRET;

    /**
     * 获取access_token，token的有效期为2小时
     */
    public static String getAccessToken() throws Exception {

        //发起GET请求，获取返回结果
        JSONObject jsonResult = HttpHelper.doGet(GET_ACCESS_TOKEN_URL);
        System.out.println("token_url : " + GET_ACCESS_TOKEN_URL);

        //解析结果，获取accessToken
        String accessToken = "";
        if (null != jsonResult) {
            accessToken = jsonResult.getString("access_token");

            //异常处理
            if (0 != jsonResult.getInteger("errcode")) {
                throw new RuntimeException("获取token失败！原因： " + jsonResult.getString("errmsg"));
            }
        }
        return accessToken;
    }

    public static void main(String[] args) throws Exception {
        String accessToken = AuthHelper.getAccessToken();
        System.out.println(accessToken);
    }
}
