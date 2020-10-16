package com.supwisdom.platform.framework.exception;

import org.springframework.http.HttpStatus;

/**
 * REST接口报错时的异常
 * 
 * @author yao.chen
 *
 */
public class RestException extends RuntimeException {

    private static final long serialVersionUID = -82749590205354536L;

    public HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public RestException() {
    }

    public RestException(HttpStatus status) {
        this.status = status;
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public RestException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

}
