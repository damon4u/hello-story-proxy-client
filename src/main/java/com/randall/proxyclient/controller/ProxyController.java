package com.randall.proxyclient.controller;

import com.randall.proxyclient.dao.ProxyDao;
import com.randall.proxyclient.entity.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-20 11:51
 */
@RestController
public class ProxyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyController.class);
    
    @Autowired
    private ProxyDao proxyDao;

    /**
     * 随机获取一个代理
     * @return IP代理
     */
    @GetMapping("/proxy")
    public Proxy proxy() {
        int count = proxyDao.count();
        Proxy proxy = proxyDao.getByIndex(ThreadLocalRandom.current().nextInt(count));
        LOGGER.info("proxy={}", proxy);
        return proxy;
    }

    /**
     * 删除无效代理
     * @param id 代理id
     */
    @DeleteMapping("/proxy/{id}")
    public int delete(@PathVariable Integer id) {
        LOGGER.info("delete id={}", id);
        return proxyDao.delete(id);
    }
}
