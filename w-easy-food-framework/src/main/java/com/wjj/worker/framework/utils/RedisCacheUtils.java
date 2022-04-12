package com.wjj.worker.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存工具类
 *
 * @author BeerGod
 */
@Component
public class RedisCacheUtils extends RedisCache {

    Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 加锁
     *
     * @param lockKey    锁住的key
     * @param value      内容
     * @param expireTime 加锁时长单位秒
     * @return 加锁是否成功
     * @author BeerGod
     */
    public Boolean lock(String lockKey, String value, long expireTime) {
        String script = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
        List<String> args = Arrays.asList(value, String.valueOf(expireTime));
        return executeScript(script, lockKey, args);
    }


    /**
     * 释放锁
     *
     * @param lockKey 解锁key
     * @param value   传入内容与指定key内容一致时解锁
     * @return
     * @author BeerGod
     */
    public boolean unLock(String lockKey, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        List<String> args = Collections.singletonList(value);
        return executeScript(script, lockKey, args);
    }

    public Boolean executeScript(String script, String lockKey, List<String> args) {
        try {
            Long result = stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(script, Collections.singletonList(lockKey), args);
                } else if (nativeConnection instanceof RedisProperties.Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(script, Collections.singletonList(lockKey), args);
                }
                return null;
            });
            if (result != null && 1 == result) {
                return true;
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTraceMessage(e));
            return false;
        }
        return false;
    }


}
