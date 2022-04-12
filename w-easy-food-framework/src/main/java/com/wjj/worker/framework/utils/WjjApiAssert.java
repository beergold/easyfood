package com.wjj.worker.framework.utils;

import com.wjj.worker.framework.enums.WjjApiEnumsInterFace;
import com.wjj.worker.framework.exception.WjjApiException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import java.util.Optional;

/**
 * 定义一些高频使用的的断言
 *
 * @author BeerGod
 */
public class WjjApiAssert extends Assert {

    public static void state(boolean condition, WjjApiEnumsInterFace responseCode) {
        if (!condition) {
            throw new WjjApiException(responseCode);
        }
    }

    public static void isOne(int num, WjjApiEnumsInterFace responseCode) {
        state(num == 1, responseCode);
    }

    public static void isNone(int num, WjjApiEnumsInterFace responseCode) {
        state(num < 1, responseCode);
    }

    public static void notNull(@Nullable Object object, WjjApiEnumsInterFace responseCode) {
        Optional.ofNullable(object).orElseThrow(() -> {
            return new WjjApiException(responseCode);
        });
    }

    public static void isTrue(boolean condition, WjjApiEnumsInterFace responseCode) {
        state(condition, responseCode);
    }

}
