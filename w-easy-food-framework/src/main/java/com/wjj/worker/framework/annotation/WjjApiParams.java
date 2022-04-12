package com.wjj.worker.framework.annotation;

import java.lang.annotation.*;

/**
 * API 参数校验/封装类
 * <pre>
 *     验证参数数据
 *     如需使用时参数，第一位应为SpriteValidRequest，验证参数将直接放入参数中的parameter
 *     参数名称为clazz名称首字母小写，且提供map方式直接获取，举例，传入name属性且验证类为Student,那么返回参数如下：
 *
 *     key:student (包含所有属性未传入则为空)
 *     key:name
 * </pre>
 *
 * @author BeerGod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WjjApiParams {
    /**
     * 是否进行参数验证
     *
     * @return
     */
    boolean valid() default true;

    /**
     * 校验参数类，支持多个
     */
    Class<?>[] clazz() default {};

    /**
     * 参数校验位置
     * <pre>
     *     提供 raw params两种方式获取默认全部
     * </pre>
     */
    ValidParamPosition position() default ValidParamPosition.ALL;

    enum ValidParamPosition {
        //全部
        ALL,
        //raw 即json格式数据 相当于request.getReader中获取数据
        RAW,
        //params 相当于request.getParameter中获取数据
        PARAM
    }
}
