package com.wjj.worker.foundation.controller;

import com.wjj.worker.framework.annotation.WjjApiParams;
import com.wjj.worker.framework.request.WjjApiPage;
import com.wjj.worker.framework.request.WjjApiValidRequest;
import com.wjj.worker.framework.response.WjjCommonApiResponse;
import com.wjj.worker.persistence.dto.QuartzDto;
import com.wjj.worker.persistence.dto.QuartzInfoDto;
import com.wjj.worker.foundation.service.common.QuartzService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author BeerGod
 * 定时任务
 */
@RestController
@RequestMapping("job")
public class QuartzController {

    @Autowired
    ServerProperties serverProperties;

    @Autowired
    private QuartzService quartzService;

    @PostMapping("/query")
    @WjjApiParams
    public WjjCommonApiResponse list(WjjApiValidRequest request) {
        WjjApiPage<QuartzInfoDto> data = quartzService.list(request.getPageParameter());
        return new WjjCommonApiResponse().success(data);
    }

    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public WjjCommonApiResponse save(@RequestBody QuartzDto quartzDTO) throws Exception {
        quartzService.addJob(quartzDTO);
        return new WjjCommonApiResponse().success(true);
    }

    @PostMapping("/trigger")
    @Transactional(rollbackFor = Exception.class)
    public WjjCommonApiResponse trigger(@RequestBody QuartzDto quartzDTO) throws Exception {
        quartzService.triggerJob(quartzDTO);
        return new WjjCommonApiResponse().success(true);
    }

    @PostMapping("/pause")
    @Transactional(rollbackFor = Exception.class)
    public WjjCommonApiResponse pause(@RequestBody QuartzDto quartzDTO) throws SchedulerException {
        quartzService.pauseJob(quartzDTO);
        return new WjjCommonApiResponse().success(true);
    }

    @PostMapping("/resume")
    @Transactional(rollbackFor = Exception.class)
    public WjjCommonApiResponse resume(@RequestBody QuartzDto quartzDTO) throws Exception {
        quartzService.resumeJob(quartzDTO);
        return new WjjCommonApiResponse().success(true);
    }

    @PostMapping("/remove")
    public WjjCommonApiResponse remove(@RequestBody QuartzDto quartzDTO) throws SchedulerException {
        quartzService.removeJob(quartzDTO);
        return new WjjCommonApiResponse().success(true);
    }

    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public WjjCommonApiResponse update(@RequestBody QuartzDto quartzDTO) throws SchedulerException {
        quartzService.updateJobCron(quartzDTO);
        return new WjjCommonApiResponse().success(true);
    }
}
