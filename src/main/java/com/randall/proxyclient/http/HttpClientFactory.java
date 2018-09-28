package com.randall.proxyclient.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.randall.proxyclient.client.GitProxyClient;
import com.randall.proxyclient.constant.HttpConfig;
import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-14 12:12
 */
@Configuration
@Data
public class HttpClientFactory {
    
    private final HttpConfig httpConfig;

    @Autowired
    public HttpClientFactory(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpHeaderInterceptor())
                .connectTimeout(httpConfig.getProxyLoadTimeout(), TimeUnit.SECONDS)
                .readTimeout(httpConfig.getProxyLoadTimeout(), TimeUnit.SECONDS).build();
    }
    
    @Bean
    public GitProxyClient gitProxyClient() {
        ObjectMapper mapper = new ObjectMapper();
        //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .baseUrl("https://raw.githubusercontent.com/")
                .client(okHttpClient())
                .build().create(GitProxyClient.class);
    }
}
