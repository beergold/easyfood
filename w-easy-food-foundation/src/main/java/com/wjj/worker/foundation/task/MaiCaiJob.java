package com.wjj.worker.foundation.task;

import com.wjj.worker.foundation.service.MaiCaiManager;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author BeerGod
 * <pre>
 *     抢叮咚买菜
 * </pre>
 */
@Component
@DisallowConcurrentExecution
public class MaiCaiJob implements Job {

    @Autowired
    MaiCaiManager maiCaiManager;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        maiCaiManager.doWork();
    }
}
