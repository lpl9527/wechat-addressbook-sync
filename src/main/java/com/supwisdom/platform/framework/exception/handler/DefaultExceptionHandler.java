package com.supwisdom.platform.framework.exception.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.supwisdom.platform.framework.domain.RestError;
import com.supwisdom.platform.framework.exception.ManagerException;
import com.supwisdom.platform.framework.exception.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;


@RestControllerAdvice
public class DefaultExceptionHandler {

    @Autowired
    private RestErrorResolver restErrorResolver;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestError ExceptionHandler(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        RestError error = restErrorResolver.resolveError(webRequest, handler, ex);

        return error;
    }

    @ExceptionHandler(value = RestException.class)
    @ResponseBody
    public RestError RestExceptionHandler(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        RestError error = restErrorResolver.resolveError(webRequest, handler, ex);

        return error;
    }

    @ExceptionHandler(value = ManagerException.class)
    @ResponseBody
    public RestError ManagerExceptionHandler(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        RestError error = restErrorResolver.resolveError(webRequest, handler, ex);

        return error;
    }
}
