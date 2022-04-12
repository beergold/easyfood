package com.wjj.worker.persistence.dto;

import lombok.Data;

/**
 * @author BeerGod
 * job实体类
 */
@Data
public class QuartzDto {

    private String number;

    private String jobName;

    private String jobGroup;

    private String triggerName;

    private String triggerGroup;

    private String cronExpression;

    private String jobClassName;

    private Integer triggerState;
    
}
