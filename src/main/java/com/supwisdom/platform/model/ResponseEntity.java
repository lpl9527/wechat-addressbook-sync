package com.supwisdom.platform.model;

/**
 * 封装同步的返回结果
 */
public class ResponseEntity {

    private String filePath;    //生成的ftl文件路径

    private String jobId;       //同步结果的jobId

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
