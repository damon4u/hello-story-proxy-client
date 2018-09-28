package com.randall.proxyclient.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description:
 * http配置参数
 * 修改此类需要maven重新编译，yml里面才能有提示
 *
 * @author damon4u
 * @version 2018-09-17 15:44
 */
@Component
@ConfigurationProperties("app.http")
@Data
public class HttpConfig {
    
    private int proxyLoadTimeout;
    
    private int proxyValidateTimeout;
}
