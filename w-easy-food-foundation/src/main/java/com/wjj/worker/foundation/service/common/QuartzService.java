package com.wjj.worker.foundation.service.common;

import cn.hutool.core.util.ObjectUtil;
import com.wjj.worker.persistence.dto.QuartzDto;
import com.wjj.worker.persistence.dto.QuartzInfoDto;
import com.wjj.worker.framework.enums.WjjApiResponseCode;
import com.wjj.worker.framework.exception.WjjApiException;
import com.wjj.worker.framework.request.WjjApiPage;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.WjjApiAssert;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.SchedulerException;

import java.util.List;

public interface QuartzService {

    /**
     * 查询任务(分页)
     */
    WjjApiPage<QuartzInfoDto> list(WjjApiPage<WjjApiParameter> page);

    /**
     * 查询任务
     */
    List<QuartzInfoDto> list(WjjApiParameter page);

    /**
     * 添加任务
     */
    void addJob(QuartzDto quartzDTO) throws Exception;

    /**
     * 触发任务
     */
    void triggerJob(QuartzDto quartzDTO) throws Exception;

    /**
     * 暂停任务
     */
    void pauseJob(QuartzDto quartzDTO) throws SchedulerException;

    /**
     * 恢复任务
     */
    void resumeJob(QuartzDto quartzDTO) throws Exception;

    /**
     * 移除任务
     */
    void removeJob(QuartzDto quartzDTO) throws SchedulerException;

    /**
     * 更新任务
     *
     */
    void updateJobCron(QuartzDto quartzDTO) throws SchedulerException;

    /**
     * 获取默认实现类实例
     *
     * @param classname 包全类名
     * @return
     */
    @SneakyThrows
    default Job getClass(String classname) {
        Class<?> clazz;
        try {
            clazz = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new WjjApiException(WjjApiResponseCode.CLASS_NOT_FOUND);
        }
        WjjApiAssert.isTrue(ObjectUtil.isNotEmpty(clazz), WjjApiResponseCode.CLASS_NOT_FOUND);
        Object o = clazz.newInstance();
        WjjApiAssert.isTrue(o instanceof Job, WjjApiResponseCode.CLASS_NOT_FOUND);
        return (Job) o;
    }
}
