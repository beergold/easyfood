package com.wjj.worker.foundation.annotation;

import java.lang.annotation.*;

/**
 * 异步job注解
 * 如记录日志必须在JobExecutionContext设置返回内容
 * 返回一个map其中包换success 以及 data用于记录日志
 *
 * @author BeerGod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PandoraSyncJob {

    /**
     * 通过此key加分布式锁，模拟zookeeper 同时只有一台机器可调度此key的job
     *
     * @return
     */
    String key();

    /**
     * 加锁最大等待时间，默认120秒
     *
     * @return
     */
    int lockMaxTime() default 120;
}
