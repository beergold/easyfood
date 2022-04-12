package com.wjj.worker.framework.support;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.wjj.worker.framework.annotation.WjjApiParams;
import com.wjj.worker.framework.request.WjjApiValidRequest;
import com.wjj.worker.framework.utils.AspectUtil;
import com.wjj.worker.framework.utils.ValidatorUtils;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * @author BeerGod
 * <pre>
 *     API参数切面
 *     <pre>
 *         1.该切面将验证PandoraApiParams注解中表明的数据格式及声明
 *         2.返回正常时会调用指定方法SpriteValidRequest（第一个参数）,否则将返回原生自定义参数
 *     </pre>
 *     tips: 执行顺序早于WjjApiHandler,保证数据校验成功后才走权限处理
 * </pre>
 */
@Component
@Aspect
@Order(6)
public class WjjApiParamsSupport {

    Logger logger = LoggerFactory.getLogger(WjjApiParamsSupport.class);

    @Autowired
    ValidatorUtils validatorUtils;

    @Pointcut(value = "@annotation(com.wjj.worker.framework.annotation.WjjApiParams)")
    public void access() {
    }

    @SneakyThrows
    @Around(value = "access()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object[] paramArray = AspectUtil.getMethodArgs(joinPoint);
        HttpServletRequest request = AspectUtil.getRequest();
        HttpServletResponse response = AspectUtil.getResponse();
        if (request == null || response == null || ArrayUtil.isEmpty(paramArray)) {
            return null;
        }
        WjjApiParams pandoraApiParams = AspectUtil.getMethodAnnotation(joinPoint, WjjApiParams.class);
        if (pandoraApiParams == null) {
            return null;
        }
        WjjApiValidRequest spriteRequest = new WjjApiValidRequest(pandoraApiParams, request, response);
        boolean valid = pandoraApiParams.valid();
        Map<String, Object> parameter = spriteRequest.getParameter();
        //验证参数
        if (valid) {
            Class<?>[] clazz = pandoraApiParams.clazz();
            if (ArrayUtil.isNotEmpty(clazz)) {
                Arrays.stream(clazz).forEach(e -> {
                    Object data = validatorUtils.validData(parameter, e);
                    parameter.put(StrUtil.lowerFirst(ClassUtil.getClassName(e, true)), data);
                });
            }
        }
        Object data = paramArray[0];
        if (data instanceof WjjApiValidRequest) {
            paramArray[0] = spriteRequest;
        }
        return joinPoint.proceed(paramArray);
    }
}
