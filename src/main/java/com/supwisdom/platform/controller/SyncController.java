package com.supwisdom.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.supwisdom.platform.model.ResponseEntity;
import com.supwisdom.platform.schedule.WechatScheduleTask;
import com.supwisdom.platform.util.AuthHelper;
import com.supwisdom.platform.util.HttpHelper;
import com.supwisdom.platform.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 同步控制器，用于提供接口进行手动同步
 */
@RestController
public class SyncController {

    private WechatScheduleTask wechatScheduleTask;

    @Autowired
    public void setWechatScheduleTask(WechatScheduleTask wechatScheduleTask) {
        this.wechatScheduleTask = wechatScheduleTask;
    }

    /**
     * 获取token
     */
    @GetMapping("/sync/token")
    public String getToken() {
        try {
            String accessToken = AuthHelper.getAccessToken();
            return accessToken;
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 同步部门
     */
    @GetMapping("/sync/org")
    public Map<String, ResponseEntity> syncOrg() {

        Map<String, ResponseEntity> result = new HashMap<>();
        //同步部门
        ResponseEntity orgResponseEntity = wechatScheduleTask.syncOrganizeTask();

        result.put("orgResult", orgResponseEntity);

        return result;
    }

    /**
     * 同步人员
     */
    @GetMapping("/sync/user")
    public Map<String, ResponseEntity> syncUser() {

        Map<String, ResponseEntity> result = new HashMap<>();
        //同步人员
        ResponseEntity userResponseEntity = wechatScheduleTask.syncUserTask();

        result.put("userResult", userResponseEntity);

        return result;
    }

    /**
     * 同步部门和人员
     */
    @RequestMapping("/sync")
    public Map<String, ResponseEntity> sync() {

        Map<String, ResponseEntity> result = new HashMap<>();
        //同步部门
        ResponseEntity orgResponseEntity = wechatScheduleTask.syncOrganizeTask();
        //同步人员
        ResponseEntity userResponseEntity = wechatScheduleTask.syncUserTask();

        result.put("orgResult", orgResponseEntity);
        result.put("userResult", userResponseEntity);

        return result;
    }

    /**
     * 获取同步结果
     */
    @GetMapping("/sync/result")
    public JSONObject syncResult(@RequestParam String jobId) {
        JSONObject jsonObject = null;
        try {
            //获取同步结果
            String accessToken = AuthHelper.getAccessToken();
            String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/getresult?access_token=" + accessToken + "&jobid=" + jobId;
            jsonObject = HttpHelper.doGet(url);
            return jsonObject;
        }catch (Exception e) {
            e.printStackTrace();
            return jsonObject;
        }
    }

}
