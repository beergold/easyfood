package com.wjj.worker.framework.utils;

import cn.hutool.extra.servlet.ServletUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author BeerGod
 * 切面工具类
 */
public class AspectUtil {

    /**
     * 获取入参参数
     *
     * @param joinPoint
     * @return
     */
    public static Object[] getMethodArgs(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

    /**
     * 获取切面相对应的注解
     *
     * @param joinPoint
     * @return
     */
    public static <T extends Annotation> T getMethodAnnotation(JoinPoint joinPoint, Class<T> clazz) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getAnnotation(clazz);
    }

    public static Method getMethod(JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    public static HttpServletRequest getRequest() {
        Object requestAttribute = RequestContextHolder.getRequestAttributes();
        if (requestAttribute == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttribute).getRequest();
        if (ServletUtil.isMultipart(request)) {
            return new StandardMultipartHttpServletRequest(request);
        }
        return request;
    }

    public static HttpServletResponse getResponse() {
        Object requestAttribute = RequestContextHolder.getRequestAttributes();
        if (requestAttribute == null) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttribute).getResponse();
    }
}
