package com.wjj.worker.persistence.dto;

import lombok.Data;
import org.quartz.Trigger;

import java.math.BigInteger;

@Data
public class QuartzInfoDto {

    private String jobName;

    private String jobGroup;

    private String jobClassName;

    private String triggerName;

    private String triggerGroup;

    private Integer triggerState;

    private BigInteger repeatInterval;

    private BigInteger timesTriggered;

    private String cronExpression;

    private String timeZoneId;

    private String description;

    private String platform;

    private Integer no;

    private Trigger.TriggerState triggerExecState;

    private String nextTime;
}
