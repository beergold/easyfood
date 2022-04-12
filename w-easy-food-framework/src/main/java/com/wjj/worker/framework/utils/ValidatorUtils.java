package com.wjj.worker.framework.utils;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wjj.worker.framework.enums.WjjApiResponseCode;
import com.wjj.worker.framework.exception.WjjApiValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * 此类用于校验参数是否在符合指定po类的定义规则
 *
 * @author BeerGod
 */
@Component
public class ValidatorUtils {

    @Autowired
    Validator validator;

    /**
     * 获取验证不通过的列表
     *
     * @param t
     * @param <T>
     * @return
     */
    public <T> Set<ConstraintViolation<T>> getValid(T t) {
        return validator.validate(t);
    }

    /**
     * 验证数据
     */
    public <T> Object validData(Object parameter, Class<T> clazz) {
        Object obj = JSONObject.parseObject(JSONObject.toJSONString(parameter), clazz);
        Set<ConstraintViolation<Object>> constraintViolationSet = getValid(obj);
        if (ObjectUtil.isNotEmpty(constraintViolationSet)) {
            Iterator<ConstraintViolation<Object>> data = constraintViolationSet.iterator();
            throw new WjjApiValidException(WjjApiResponseCode.METHOD_ARGUMENT_NOT_VALID.code().toString(), data.next().getMessageTemplate());
        }
        return obj;
    }
}