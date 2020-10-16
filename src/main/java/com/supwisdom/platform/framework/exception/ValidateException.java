package com.supwisdom.platform.framework.exception;

public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 3089412213794056504L;

    public ValidateException() {
        super();
    }

    public ValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateException(String message) {
        super(message);
    }

    public ValidateException(Throwable cause) {
        super(cause);
    }

}
