package com.randall.proxyclient.job;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *  定时任务调度器
 *
 * @author damon4u
 * @version 2018-09-14 18:37
 */
@Configuration
public class JobScheduler {

    private static final String JOB_NAME_PROXY_LOAD = "proxyLoadJob";
    private static final String JOB_NAME_PROXY_VALIDATE = "proxyValidateJob";
    private static final String GROUP_NAME_PROXY = "proxyLoadGroup";

    @Bean
    public JobDetail proxyJob() {
        return JobBuilder.newJob(ProxyLoadJob.class)
                .withIdentity(JOB_NAME_PROXY_LOAD, GROUP_NAME_PROXY)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger proxyJobTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/20 * * * ?");
        return TriggerBuilder.newTrigger()
                .withSchedule(scheduleBuilder)
                .withIdentity(JOB_NAME_PROXY_LOAD, GROUP_NAME_PROXY)
                .forJob(proxyJob())
                .build();
    }

    @Bean
    public JobDetail proxyValidateJob() {
        return JobBuilder.newJob(ProxyValidateJob.class)
                .withIdentity(JOB_NAME_PROXY_VALIDATE, GROUP_NAME_PROXY)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger proxyValidateJobTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/15 * * * ?");
        return TriggerBuilder.newTrigger()
                .withSchedule(scheduleBuilder)
                .withIdentity(JOB_NAME_PROXY_VALIDATE, GROUP_NAME_PROXY)
                .forJob(proxyValidateJob())
                .build();
    }
}
