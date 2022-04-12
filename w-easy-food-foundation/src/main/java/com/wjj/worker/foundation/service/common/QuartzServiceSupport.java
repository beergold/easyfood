package com.wjj.worker.foundation.service.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wjj.worker.persistence.dao.WJobDao;
import com.wjj.worker.persistence.dto.QuartzDto;
import com.wjj.worker.persistence.dto.QuartzInfoDto;
import com.wjj.worker.framework.enums.WjjApiResponseCode;
import com.wjj.worker.framework.exception.WjjApiException;
import com.wjj.worker.framework.request.WjjApiPage;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.framework.utils.WjjApiAssert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author BeerGod
 * <pre>
 *     job任务实现类
 * </pre>
 */
@Slf4j
@Service
public class QuartzServiceSupport implements QuartzService {

    @Autowired
    private WJobDao wJobDao;

    @Autowired
    private Scheduler scheduler;

    @Override
    public WjjApiPage<QuartzInfoDto> list(WjjApiPage<WjjApiParameter> page) {
        WjjApiPage<QuartzInfoDto> data = wJobDao.queryJobList(page);
        if (ObjectUtil.isNotEmpty(data) && ObjectUtil.isNotEmpty(data.getRecords())) {
            data.getRecords().forEach(e -> {
                TriggerKey triggerKey = TriggerKey.triggerKey(e.getTriggerName(), e.getTriggerGroup());
                try {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    if (ObjectUtil.isNotEmpty(trigger)) {
                        if (triggerState == Trigger.TriggerState.PAUSED) {
                            e.setNextTime("已暂停");
                        } else {
                            String nextTime = trigger.getNextFireTime() != null ? DateUtil.formatDateTime(trigger.getNextFireTime()) : null;
                            e.setNextTime(nextTime);
                        }
                    }
                    e.setTriggerExecState(triggerState);
                } catch (SchedulerException ex) {
                    ex.printStackTrace();
                }
            });
        }
        return data;
    }

    /**
     * 查询任务
     */
    @Override
    public List<QuartzInfoDto> list(WjjApiParameter parameter) {
        WjjApiPage<WjjApiParameter> data = new WjjApiPage<>(1, Integer.MAX_VALUE);
        data.setWjjParams(parameter);
        WjjApiPage<QuartzInfoDto> result = list(data);
        return result != null && ObjectUtil.isNotEmpty(result.getRecords()) ? result.getRecords() : null;
    }

    /**
     * 添加任务
     */
    @Override
    public void addJob(QuartzDto quartzDTO) throws SchedulerException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        String triggerName = StringUtils.isNotEmpty(quartzDTO.getTriggerName()) ? quartzDTO.getTriggerName() : IdUtil.fastSimpleUUID();
        String triggerGroup = StringUtils.isNotEmpty(quartzDTO.getTriggerGroup()) ? quartzDTO.getTriggerGroup() : quartzDTO.getJobGroup();
        JobKey jobKey = JobKey.jobKey(quartzDTO.getJobName(), quartzDTO.getJobGroup());
        WjjApiAssert.isTrue(ObjectUtil.isEmpty(scheduler.getJobDetail(jobKey)), WjjApiResponseCode.JOB_IS_EXIST);
        quartzDTO.setTriggerName(triggerName);
        quartzDTO.setTriggerGroup(triggerGroup);
        WjjApiParameter parameter = new WjjApiParameter();
        parameter.put("status", 1);
        parameter.put("triggerName", triggerName);
        parameter.put("triggerGroup", triggerGroup);
        List<QuartzInfoDto> data = list(parameter);
        if (ObjectUtil.isEmpty(data)) {
            WjjApiAssert.isOne(wJobDao.addJob(quartzDTO), WjjApiResponseCode.ADD_JOB_ERROR);
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        JobDetail jobDetail = JobBuilder.newJob(getClass(quartzDTO.getJobClassName()).getClass()).withIdentity(jobKey).build();
        CronScheduleBuilder scheduleBuilder;
        try {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzDTO.getCronExpression()).withMisfireHandlingInstructionDoNothing();
        } catch (RuntimeException e) {
            throw new WjjApiException(WjjApiResponseCode.SERVER_ERROR, e.getMessage());
        }
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 触发任务
     */
    @Override
    public void triggerJob(QuartzDto quartzDTO) throws Exception {
        JobKey key = JobKey.jobKey(quartzDTO.getJobName(), quartzDTO.getJobGroup());
        addJob(key, quartzDTO);
        scheduler.triggerJob(key);
    }

    /**
     * 暂停任务
     */
    @Override
    public void pauseJob(QuartzDto quartzDTO) throws SchedulerException {
        quartzDTO.setTriggerState(0);
        WjjApiAssert.isOne(wJobDao.updateJob(quartzDTO), WjjApiResponseCode.UPDATE_JOB_ERROR);
        JobKey key = JobKey.jobKey(quartzDTO.getJobName(), quartzDTO.getJobGroup());
        scheduler.pauseJob(key);
    }

    /**
     * 恢复任务
     */
    @Override
    public void resumeJob(QuartzDto quartzDTO) throws Exception {
        quartzDTO.setTriggerState(1);
        WjjApiAssert.isOne(wJobDao.updateJob(quartzDTO), WjjApiResponseCode.UPDATE_JOB_ERROR);
        JobKey key = JobKey.jobKey(quartzDTO.getJobName(), quartzDTO.getJobGroup());
        addJob(key, quartzDTO);
        scheduler.resumeJob(key);
    }

    /**
     * 移除任务
     */
    @Override
    public void removeJob(QuartzDto quartzDTO) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(quartzDTO.getTriggerName(), quartzDTO.getTriggerGroup());
        //停止触发器
        scheduler.pauseTrigger(triggerKey);
        //移除触发器
        scheduler.unscheduleJob(triggerKey);
        //删除任务
        scheduler.deleteJob(JobKey.jobKey(quartzDTO.getJobName(), quartzDTO.getJobGroup()));
    }

    /**
     * 更新任务
     */
    @Override
    public void updateJobCron(QuartzDto quartzDTO) throws SchedulerException {
        WjjApiAssert.isOne(wJobDao.updateJob(quartzDTO), WjjApiResponseCode.UPDATE_JOB_ERROR);
        TriggerKey triggerKey = TriggerKey.triggerKey(quartzDTO.getTriggerName(), quartzDTO.getTriggerGroup());
        CronScheduleBuilder scheduleBuilder;
        try {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzDTO.getCronExpression()).withMisfireHandlingInstructionDoNothing();
        } catch (RuntimeException e) {
            throw new WjjApiException(WjjApiResponseCode.SERVER_ERROR, e.getMessage());
        }
        CronTrigger trigger = ((CronTrigger) scheduler.getTrigger(triggerKey)).getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    public void addJob(JobKey key, QuartzDto quartzDTO) throws Exception {
        if (ObjectUtil.isEmpty(scheduler.getJobDetail(key))) {
            WjjApiParameter parameter = new WjjApiParameter();
            parameter.put("status", 1);
            parameter.put("triggerName", quartzDTO.getTriggerName());
            parameter.put("triggerGroup", quartzDTO.getTriggerGroup());
            List<QuartzInfoDto> data = list(parameter);
            if (ObjectUtil.isEmpty(data)) {
                throw new WjjApiException(WjjApiResponseCode.JOB_NOT_EXIST);
            }
            addJob(JSONObject.parseObject(JSON.toJSONString(data.get(0)), QuartzDto.class));
        }
    }
}