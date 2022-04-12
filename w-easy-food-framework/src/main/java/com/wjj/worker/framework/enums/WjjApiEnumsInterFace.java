package com.wjj.worker.framework.enums;

/**
 * @author BeerGod
 */
public interface WjjApiEnumsInterFace<T> {
    /**
     * 定于枚举code
     *
     * @return
     */
    T code();

    /**
     * 定于枚举具体信息
     *
     * @return
     */
    String msg();
}
