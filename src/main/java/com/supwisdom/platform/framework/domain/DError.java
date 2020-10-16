package com.supwisdom.platform.framework.domain;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 请求出错信息详情 created by fanlu on 04/05/2016
 */
public class DError {

    private String message;
    private int code;
    private Collection<IError> errors;

    public DError() {
    }

    public DError(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public DError(String message, int code, Collection<IError> errors) {
        this.message = message;
        this.code = code;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Collection<IError> errors) {
        this.errors = errors;
    }

    public static ErrorBuilder custom() {
        return new ErrorBuilder();
    }

    public static class ErrorBuilder {

        private String message;
        private int code;
        private Collection<IError> errors;

        public ErrorBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorBuilder setCode(int code) {
            this.code = code;
            return this;
        }

        public ErrorBuilder setErrors(Collection<IError> errors) {
            this.errors = errors;
            return this;
        }

        public ErrorBuilder addError(IError iError) {

            if (this.errors == null) {
                this.errors = new ArrayList<>();
            }
            this.errors.add(iError);
            return this;
        }

        public DError build() {
            return new DError(message, code, errors);
        }

    }

    /**
     * 错误项
     */
    public static class IError {

        private String message;
        private int errorcode;

        public IError() {
        }

        public IError(String message, int errorcode) {
            this.message = message;
            this.errorcode = errorcode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getErrorcode() {
            return errorcode;
        }

        public void setErrorcode(int errorcode) {
            this.errorcode = errorcode;
        }

    }

}
