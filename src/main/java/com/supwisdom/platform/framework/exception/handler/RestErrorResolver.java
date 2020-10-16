package com.supwisdom.platform.framework.exception.handler;

import com.supwisdom.platform.framework.domain.RestError;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 异常处理接口，根据异常产生错误信息
 * 
 * @author fanlu
 *
 */
public interface RestErrorResolver {

    RestError resolveError(ServletWebRequest request, Object handler, Exception ex);

}
