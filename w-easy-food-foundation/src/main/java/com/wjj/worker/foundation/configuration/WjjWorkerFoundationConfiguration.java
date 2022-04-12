package com.wjj.worker.foundation.configuration;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wjj.worker.persistence.dto.QuartzDto;
import com.wjj.worker.persistence.dto.QuartzInfoDto;
import com.wjj.worker.framework.request.WjjApiParameter;
import com.wjj.worker.foundation.service.common.QuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author BeerGod
 * 自定义start
 */
@Configuration
public class WjjWorkerFoundationConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    Logger logger = LoggerFactory.getLogger(WjjWorkerFoundationConfiguration.class);

    @Autowired
    QuartzService quartzService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/job/view/**").addResourceLocations("classpath:static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/job/view/JobManager.html");
        registry.addRedirectViewController("/job/view/", "/");
        registry.addRedirectViewController("/JobManager.html", "/");
    }

    @PostConstruct
    public void initConfig() {
        WjjApiParameter parameter = new WjjApiParameter();
        parameter.put("status", 1);
        List<QuartzInfoDto> quartzInfoDTO = quartzService.list(parameter);
        boolean flag = ObjectUtil.isNotEmpty(quartzInfoDTO);
        logger.info("加载定时任务,定时任务个数:{}@", flag ? quartzInfoDTO.size() : 0);
        if (flag) {
            List<QuartzDto> jobList = JSONObject.parseArray(JSONObject.toJSONString(quartzInfoDTO), QuartzDto.class);
            jobList.forEach(e -> {
                try {
                    quartzService.addJob(e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        logger.info("定时任务加载完成");
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().and().authorizeRequests().antMatchers("/job/**").fullyAuthenticated().and().formLogin().and().authorizeRequests().antMatchers("/**").permitAll();
    }

}
