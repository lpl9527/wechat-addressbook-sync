package com.supwisdom.platform.framework.domain;


/**
 * 请求出错时的REST返回结果 created by fanlu on 04/05/2016
 */
public class RestError extends BaseResult {

    /**
     * 
     */
    private static final long serialVersionUID = -1260648177571131472L;
    private DError error;

    public RestError() {
    }

    public RestError(Status status, Object response) {
        super(status, response);
    }

    public RestError(DError error) {
        this.error = error;
    }

    public DError getError() {
        return error;
    }

    public void setError(DError error) {
        this.error = error;
    }

}
