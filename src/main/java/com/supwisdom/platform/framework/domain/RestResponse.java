package com.supwisdom.platform.framework.domain;


import com.supwisdom.platform.framework.util.ExceptionUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("API返回结果实体")
public class RestResponse<T> {
    @ApiModelProperty("错误消息")
    private String message;
    @ApiModelProperty("返回数据")
    private T data;
    @ApiModelProperty("代码：0(成功)，-1（失败），其他业务自定义")
    private int code;

    public RestResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static <T> RestResponse<T> success(T data) {
        RestResponse<T> restResult = new RestResponse<>();
        restResult.setCode(0);
        restResult.setData(data);
        return restResult;
    }

    public static <T> RestResponse<T> fail(String message) {
        RestResponse<T> restResult = new RestResponse<>();
        restResult.setCode(-1);
        restResult.setMessage(ExceptionUtil.getMessage(message));
        return restResult;
    }

    public static <T> RestResponse<T> fail(int code, String message) {
        RestResponse<T> restResult = new RestResponse<>();
        restResult.setCode(code);
        restResult.setMessage(ExceptionUtil.getMessage(message));
        return restResult;
    }

}
