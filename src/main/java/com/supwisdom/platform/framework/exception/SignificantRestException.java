package com.supwisdom.platform.framework.exception;

import org.springframework.http.HttpStatus;

/**
 * 有意义的Rest异常，用于前端提示 created by fanlu on 06/15/2016
 */
public class SignificantRestException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 4055306559624907465L;

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public SignificantRestException() {

    }

    public SignificantRestException(String message) {
        super(message);
    }

    public SignificantRestException(HttpStatus stauts) {
        super();
        this.status = stauts;
    }

    public SignificantRestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
