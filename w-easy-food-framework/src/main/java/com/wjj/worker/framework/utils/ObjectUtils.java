package com.wjj.worker.framework.utils;

import cn.hutool.core.util.ObjectUtil;

import java.util.List;
import java.util.Map;

/**
 * @author BeerGod
 * ObjectUtil的扩展
 */
public class ObjectUtils extends ObjectUtil {
    /**
     * 校验data中的每个对象的keys对应的value是否为空
     *
     * @param data           list
     * @param ignoreMainData 是否忽略data为空
     * @param keys           key
     * @return
     */
    public static boolean isNotEmpty(List<? extends Map<String, ?>> data, boolean ignoreMainData, String... keys) {
        if (isEmpty(data)) {
            return ignoreMainData;
        }
        for (Map<String, ?> map : data) {
            if (!isNotEmpty(map, keys)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(List<? extends Map<String, ?>> data, String... keys) {
        return isNotEmpty(data, false, keys);
    }

    public static boolean isNotEmpty(Map<String, ?> data, String... keys) {
        if(isEmpty(data)){
            return false;
        }
        for (String key : keys) {
            if (isEmpty(data.get(key))) {
                return false;
            }
        }
        return true;
    }
}
