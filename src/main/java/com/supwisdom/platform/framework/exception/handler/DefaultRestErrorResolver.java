    package com.supwisdom.platform.framework.exception.handler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.supwisdom.platform.framework.domain.DError;
import com.supwisdom.platform.framework.domain.RestError;
import com.supwisdom.platform.framework.exception.RestException;
import com.supwisdom.platform.framework.exception.SignificantRestException;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.LocaleResolver;


/**
 * {@link RestErrorResolver}系统默认的实现，根据异常和自定义的配置文件产生错误信息
 * 
 * @author fanlu
 *
 */
@Component
public class DefaultRestErrorResolver implements RestErrorResolver, InitializingBean {

  public static final String DEFAULT_EXCEPTION_MESSAGE = "common.unknownException";

  public static final int DEFAULT_EXCEPTION_CODE = HttpStatus.BAD_REQUEST.value();

  private static final String DEFAULT_EXCEPTION_MESSAGE_VALUE = "_exmsg";

  private Map<String, RestError> exceptionMappings = Collections.emptyMap();

  private Map<String, String> exceptionMappingDefinitions = Collections.emptyMap();

  private int defaultExCode = DEFAULT_EXCEPTION_CODE;
  private String defaultExMsg = DEFAULT_EXCEPTION_MESSAGE;

  private MessageSource messageSource;
  private LocaleResolver localeResolver;

  public void setDefaultExCode(int defaultExCode) {
    this.defaultExCode = defaultExCode;
  }

  public void setDefaultExMsg(String defaultExMsg) {
    this.defaultExMsg = defaultExMsg;
  }

  public void setExceptionMappingDefinitions(Map<String, String> exceptionMappingDefinitions) {
    this.exceptionMappingDefinitions = exceptionMappingDefinitions;
  }

  public LocaleResolver getLocaleResolver() {
    return localeResolver;
  }

  public void setLocaleResolver(LocaleResolver localeResolver) {
    this.localeResolver = localeResolver;
  }

  public MessageSource getMessageSource() {
    return messageSource;
  }

  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public void afterPropertiesSet() throws Exception {

    // populate with some defaults:
    Map<String, String> definitions = createDefaultExceptionMappingDefinitions();

    // add in user-specified mappings (will override defaults as necessary):
    if (this.exceptionMappingDefinitions != null && !this.exceptionMappingDefinitions.isEmpty()) {
      definitions.putAll(this.exceptionMappingDefinitions);
    }

    this.exceptionMappings = toRestErrors(definitions);

  }

  protected final Map<String, String> createDefaultExceptionMappingDefinitions() {

    Map<String, String> m = new LinkedHashMap<>();

    // 400
    applyDefinition(m, HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
    applyDefinition(m, MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST);
    applyDefinition(m, TypeMismatchException.class, HttpStatus.BAD_REQUEST);
    applyDef(m, "javax.validation.ValidationException", HttpStatus.BAD_REQUEST);

    // 404
    //applyDefinition(m, NoSuchRequestHandlingMethodException.class, HttpStatus.NOT_FOUND);
    applyDef(m, "org.hibernate.ObjectNotFoundException", HttpStatus.NOT_FOUND);

    // 405
    applyDefinition(m, HttpRequestMethodNotSupportedException.class, HttpStatus.METHOD_NOT_ALLOWED);

    // 406
    applyDefinition(m, HttpMediaTypeNotAcceptableException.class, HttpStatus.NOT_ACCEPTABLE);

    // 409
    // can't use the class directly here as it may not be an available
    // dependency:
    applyDef(m, "org.springframework.dao.DataIntegrityViolationException", HttpStatus.CONFLICT);

    // 415
    applyDefinition(m, HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE);

    return m;
  }

  private void applyDefinition(Map<String, String> m, Class<?> clazz, HttpStatus status) {
    applyDef(m, clazz.getName(), status);
  }

  private void applyDef(Map<String, String> m, String key, HttpStatus status) {
    m.put(key, definitionFor(status));
  }

  private String definitionFor(HttpStatus status) {
    return status.value() + ", " + DEFAULT_EXCEPTION_MESSAGE_VALUE;
  }

  @Override
  public RestError resolveError(ServletWebRequest request, Object handler, Exception ex) {
    RestError template = getRestErrorTemplate(ex);

    if (template == null) {
      // return new RestError(new DError(DEFAULT_EXCEPTION_MESSAGE,
      // DEFAULT_EXCEPTION_CODE, new ArrayList<IError>()));
      throw (RuntimeException) ex;
    }

    String msg = getMessage(template, request, ex);
    int code = getCode(template, request, ex);

    DError.ErrorBuilder builder = DError.custom().setMessage(msg).setCode(code);

    Throwable[] throwables = ExceptionUtils.getThrowables(ex);
    if (throwables != null && throwables.length > 0) {
      for (Throwable throwable : throwables) {
        builder.addError(new DError.IError(getMessage(DEFAULT_EXCEPTION_MESSAGE_VALUE, request, throwable),
            getCode(template, request, throwable)));
      }
    }
    ex.printStackTrace();
    return new RestError(builder.build());
  }

  protected int getCode(RestError template, ServletWebRequest request, Throwable ex) {
    int code = template.getError().getCode();
    if (code <= 0) {

      if (ex instanceof RestException) {
        code = ((RestException) ex).status.value();
      } else if (ex instanceof SignificantRestException) {
        code = ((SignificantRestException) ex).getStatus().value();
      } else {
        code = defaultExCode;
      }
    }
    return code;
  }

  protected String getMessage(RestError template, ServletWebRequest request, Throwable ex) {
    return getMessage(template.getError().getMessage(), request, ex);
  }

  /**
   * Returns the response status message to return to the client, or
   * {@code null} if no status message should be returned.
   *
   * @return the response status message to return to the client, or
   *         {@code null} if no status message should be returned.
   */
  protected String getMessage(String msg, ServletWebRequest webRequest, Throwable ex) {

    if (msg != null) {
      if (msg.equalsIgnoreCase("null") || msg.equalsIgnoreCase("off")) {
        msg = defaultExMsg;
      }

      if (msg.equalsIgnoreCase(DEFAULT_EXCEPTION_MESSAGE_VALUE)) {
        msg = ex.getMessage();
        if (msg == null) {
          msg = ex.getClass().getName();
        }
      }

    } else {
      msg = defaultExMsg;
    }

    if (messageSource != null) {
      Locale locale = null;

      if (localeResolver != null) {
        locale = localeResolver.resolveLocale(webRequest.getRequest());
      }

      msg = messageSource.getMessage(msg, null, msg, locale);
    }

    return msg;
  }

  /**
   * Returns the config-time 'template' RestError instance configured for the
   * specified Exception, or {@code null} if a match was not found.
   * <p/>
   * The config-time template is used as the basis for the RestError constructed
   * at runtime.
   * 
   * @param ex
   * @return the template to use for the RestError instance to be constructed.
   */
  private RestError getRestErrorTemplate(Throwable ex) {
    Map<String, RestError> mappings = this.exceptionMappings;

    if (CollectionUtils.isEmpty(mappings)) {
      return null;
    }

    RestError template = null;
    int deepest = Integer.MAX_VALUE;

    for (Map.Entry<String, RestError> entry : mappings.entrySet()) {
      String key = entry.getKey();
      int depth = getDepth(key, ex);

      if (depth >= 0 && depth < deepest) {
        deepest = depth;
        template = entry.getValue();
      }
    }

    return template;
  }

  /**
   * Return the depth to the superclass matching.
   * <p>
   * 0 means ex matches exactly. Returns -1 if there's no match. Otherwise,
   * returns depth. Lowest depth wins.
   */
  protected int getDepth(String exceptionMapping, Throwable ex) {
    return getDepth(exceptionMapping, ex.getClass(), 0);
  }

  private int getDepth(String exceptionMapping, Class<?> exceptionClass, int depth) {
    if (exceptionClass.getName().contains(exceptionMapping)) {
      // Found it!
      return depth;
    }
    // If we've gone as far as we can go and haven't found it...
    if (exceptionClass.equals(Throwable.class)) {
      return -1;
    }
    return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
  }

  protected Map<String, RestError> toRestErrors(Map<String, String> smap) {
    if (CollectionUtils.isEmpty(smap)) {
      return Collections.emptyMap();
    }

    Map<String, RestError> map = new LinkedHashMap<>(smap.size());

    for (Map.Entry<String, String> entry : smap.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      RestError template = toRestError(value);
      map.put(key, template);
    }

    return map;
  }

  protected RestError toRestError(String exceptionConfig) {

    String[] values = StringUtils.commaDelimitedListToStringArray(exceptionConfig);

    if (values == null || values.length == 0) {
      throw new IllegalStateException("Invalid config mapping.  Exception names must map to a string configuration.");
    }

    DError.ErrorBuilder builder = DError.custom();

    boolean codeSet = false;
    boolean msgSet = false;

    for (String value : values) {

      String trimmedVal = StringUtils.trimWhitespace(value);

      // check to see if the value is an explicitly named key/value pair:
      String[] pair = StringUtils.split(trimmedVal, "=");

      if (pair != null) {
        // explicit attribute set:
        String pairKey = StringUtils.trimWhitespace(pair[0]);
        if (!StringUtils.hasText(pairKey)) {
          pairKey = null;
        }
        String pairValue = StringUtils.trimWhitespace(pair[1]);
        if (!StringUtils.hasText(pairValue)) {
          pairValue = null;
        }

        if ("code".equalsIgnoreCase(pairKey)) {
          int code = getRequiredInt(pairKey, pairValue);
          builder.setCode(code);
          codeSet = true;
        } else if ("msg".equalsIgnoreCase(pairKey)) {
          builder.setMessage(pairValue);
          msgSet = true;
        }
      } else {
        // not a key/value pair - use heuristics to determine what value is
        // being set:
        int val;
        if (!codeSet) {
          val = getInt("code", trimmedVal);
          if (val > 0) {
            builder.setCode(val);
            codeSet = true;
            continue;
          }
        }

        if (!msgSet) {
          builder.setMessage(trimmedVal);
          msgSet = true;
          continue;
        }
      }
    }

    return new RestError(builder.build());
  }

  private static int getRequiredInt(String key, String value) {
    try {
      int anInt = Integer.valueOf(value);
      return Math.max(-1, anInt);
    } catch (NumberFormatException e) {
      String msg = "Configuration element '" + key + "' requires an integer value.  The value " + "specified: " + value;
      throw new IllegalArgumentException(msg, e);
    }
  }

  private static int getInt(String key, String value) {
    try {
      return getRequiredInt(key, value);
    } catch (IllegalArgumentException iae) {
      return 0;
    }
  }

}
