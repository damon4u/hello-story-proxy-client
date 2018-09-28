package com.randall.proxyclient.job;

import com.randall.proxyclient.loader.ProxyLoader;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


/**
 * Description:
 *  定时取出数据库中的代理，遍历验证有效性，将无效的代理删除
 *
 * @author damon4u
 * @version 2018-09-10 11:57
 */
@Component
public class ProxyValidateJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyValidateJob.class);
    
    @Autowired
    private ProxyLoader proxyLoader;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        LOGGER.info("ProxyValidateJob start.");
        try {
            proxyLoader.validateFromDb();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("ProxyValidateJob end.");
    }
}
