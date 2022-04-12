package com.wjj.worker.foundation.util;

import com.alibaba.fastjson.JSONObject;
import com.wjj.worker.persistence.dao.WJobDao;
import com.wjj.worker.framework.request.WjjApiParameter;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author BeerGod
 * job工具类
 */
@Component
public class JobUtil {

    @Autowired
    WJobDao wJobDao;


    /**
     * 添加job运行日志
     *
     * @param success
     * @param data
     * @return
     */
    public boolean addJobLog(boolean success, Object data, @NotNull JobExecutionContext jobExecutionContext) {
        WjjApiParameter parameter = new WjjApiParameter();
        parameter.put("success", success);
        parameter.put("result", data != null ? JSONObject.toJSONString(data) : null);
        parameter.put("triggerName", jobExecutionContext.getTrigger().getKey().getName());
        parameter.put("triggerGroup", jobExecutionContext.getTrigger().getKey().getGroup());
        parameter.put("jobName", jobExecutionContext.getJobDetail().getKey().getName());
        parameter.put("jobGroup", jobExecutionContext.getJobDetail().getKey().getGroup());
        return wJobDao.addJobLog(parameter) == 1;
    }
}
