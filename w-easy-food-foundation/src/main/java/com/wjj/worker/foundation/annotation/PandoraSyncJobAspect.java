package com.wjj.worker.foundation.annotation;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wjj.worker.framework.utils.AspectUtil;
import com.wjj.worker.framework.utils.ExceptionUtil;
import com.wjj.worker.framework.utils.RedisCacheUtils;
import com.wjj.worker.foundation.constant.JobConstant;
import com.wjj.worker.foundation.util.JobUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;


/**
 * @author BeerGod
 * PandoraSyncJob 实现类
 */
@Component
@Aspect
public class PandoraSyncJobAspect {
    @Autowired
    RedisCacheUtils cacheUtils;
    @Autowired
    JobUtil jobUtil;

    Logger logger = LoggerFactory.getLogger(PandoraSyncJobAspect.class);

    @Pointcut(value = "@annotation(com.wjj.worker.foundation.annotation.WjjSyncJob)")
    public void access() {

    }


    @Around(value = "access()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        WjjSyncJob wjjSyncJob = AspectUtil.getMethodAnnotation(joinPoint, WjjSyncJob.class);
        String key = wjjSyncJob.key();
        logger.info("easyfood job 开始执行,执行job key:{}@,执行时间:{}", key, DateUtil.formatDateTime(new Date()));
        Boolean flag = cacheUtils.lock(JobConstant.REDIS_LOCK_KEY + key, "1", wjjSyncJob.lockMaxTime());
        logger.info("easyfood job 尝试加锁，job key:{}，加锁结果:{}@@", key, flag);
        //加锁失败
        if (!flag) {
            logger.info("easyfood job 加锁失败,不执行业务方法,job key:{}", key);
            return null;
        }
        Object[] args = AspectUtil.getMethodArgs(joinPoint);
        //加锁成功，执行业务方法
        Object result = joinPoint.proceed(args);
        //判断是否为异常
        if (result instanceof Exception) {
            return null;
        }
        JobExecutionContext jobExecutionContext = (JobExecutionContext) args[0];
        Object o = jobExecutionContext.getResult();
        logger.info("easyfood job 执行完成，jobKey:{}@,执行结果:{}", key, o);
        //执行失败
        if (!(o instanceof Map) || ObjectUtil.isEmpty(o)) {
            jobUtil.addJobLog(Boolean.FALSE, o, jobExecutionContext);
            unLock(key);
            return result;
        }
        Map obj = (Map) o;
        Boolean success = (Boolean) obj.get("success");
        Object data = obj.get("data");
        //执行成功
        if (success) {
            jobUtil.addJobLog(Boolean.TRUE, data, jobExecutionContext);
        } else {
            jobUtil.addJobLog(Boolean.FALSE, data, jobExecutionContext);
        }
        unLock(key);
        return result;
    }

    @AfterThrowing(value = "access()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        WjjSyncJob wjjSyncJob = AspectUtil.getMethodAnnotation(joinPoint, WjjSyncJob.class);
        String key = wjjSyncJob.key();
        unLock(key);
        JobExecutionContext jobExecutionContext = (JobExecutionContext) AspectUtil.getMethodArgs(joinPoint)[0];
        jobUtil.addJobLog(Boolean.FALSE, ExceptionUtil.getStackTraceMessage(ex), jobExecutionContext);
    }

    /**
     * 解锁
     *
     * @param key
     * @return
     */
    public Boolean unLock(String key) {
        //解锁
        return cacheUtils.unLock(JobConstant.REDIS_LOCK_KEY + key, "1");
    }

}
