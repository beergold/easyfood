package com.wjj.worker.framework.exception;


import com.alibaba.fastjson.JSONException;
import com.wjj.worker.framework.enums.WjjApiResponseCode;
import com.wjj.worker.framework.response.WjjCommonApiResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 自定义异常拦截器
 *
 * @author BeerGod
 */
@ControllerAdvice
@ResponseBody
public class WjjApiExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(WjjApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WjjCommonApiResponse handleException(MethodArgumentNotValidException e) {
        logger.error(ExceptionUtils.getStackTrace(e));
        return new WjjCommonApiResponse().error(WjjApiResponseCode.METHOD_ARGUMENT_NOT_VALID, e.getBindingResult());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public WjjCommonApiResponse handleException(HttpMessageNotReadableException e) {
        logger.error(ExceptionUtils.getStackTrace(e));
        return new WjjCommonApiResponse().error(WjjApiResponseCode.JSON_READER_ERROR);
    }

    @ExceptionHandler(JSONException.class)
    public WjjCommonApiResponse handleException(JSONException e) {
        logger.error(ExceptionUtils.getStackTrace(e));
        return new WjjCommonApiResponse().error(WjjApiResponseCode.JSON_READER_ERROR);
    }

    @ExceptionHandler(WjjApiException.class)
    public WjjCommonApiResponse handleException(WjjApiException e) {
        logger.error(ExceptionUtils.getStackTrace(e));
        return new WjjCommonApiResponse().error(e);
    }

    @ExceptionHandler(WjjApiValidException.class)
    public WjjCommonApiResponse handleException(WjjApiValidException e) {
        logger.error("接口验证不通过,requestId:{}@,不通过原因:{}@", e.getId(), e.getMsg());
        return new WjjCommonApiResponse().error(e);
    }

    @ExceptionHandler(Exception.class)
    public WjjCommonApiResponse handleException(Exception e) {
        logger.error(ExceptionUtils.getStackTrace(e));
        return new WjjCommonApiResponse().error(WjjApiResponseCode.SERVER_ERROR);
    }
}
