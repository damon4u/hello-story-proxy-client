package com.randall.proxyclient.loader;

import com.randall.proxyclient.client.GitProxyClient;
import com.randall.proxyclient.constant.HttpConfig;
import com.randall.proxyclient.dao.ProxyDao;
import com.randall.proxyclient.entity.Proxy;
import com.randall.proxyclient.http.HttpProxyClientFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-08-28 10:57
 */
@Component
public class ProxyLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyLoader.class);

    private static Pattern NAME_PATTERN = Pattern.compile("<title>(.*) - 网易云音乐</title>");

    @Autowired
    private ProxyDao proxyDao;
    
    @Autowired
    private GitProxyClient gitProxyClient;
    
    @Autowired
    private HttpConfig httpConfig;
    
    public void loadProxy() throws Exception {
        loadFromGit();
    }
    
    private void loadFromGit() throws Exception {
        LOGGER.info("loadFromGit start.");
        Response<List<Proxy>> response = gitProxyClient.loadProxy().execute();
        List<Proxy> proxyList = response.body();
        filterProxy(proxyList);
        LOGGER.info("loadFromGit done.");
    }
    
    /**
     * 通过获取歌曲信息验证代理有效性
     */
    private boolean validateGetSong(Proxy proxy, int timeoutInSecond) throws Exception {
        Response<String> response = HttpProxyClientFactory.musicClient(proxy, timeoutInSecond)
                .songInfo(209996).execute();
        if (response == null) {
            return false;
        }
        String songInfo = response.body();
        if (StringUtils.isBlank(songInfo)) {
            return false;
        }
        Matcher nameMatcher = NAME_PATTERN.matcher(songInfo);
        String name = "";
        if (nameMatcher.find()) {
            name = nameMatcher.group(1);
        }
        return StringUtils.isNotBlank(name);
    }

    private void filterProxy(List<Proxy> proxyList) {
        if (CollectionUtils.isNotEmpty(proxyList)) {
            final CountDownLatch latch = new CountDownLatch(proxyList.size());
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            proxyList.forEach(proxy -> executorService.execute(new FilterJob(proxy, latch)));
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
            executorService.shutdownNow();
        }
    }

    /**
     * 抓取时的代理过滤任务
     */
    class FilterJob implements Runnable {
        
        private Proxy proxy;
        
        private CountDownLatch latch;

        FilterJob(Proxy proxy, CountDownLatch latch) {
            this.proxy = proxy;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                // 先只要http类型的
                if (proxy.getProto().startsWith("http")
                        && validateGetSong(proxy, httpConfig.getProxyValidateTimeout())) {
                    LOGGER.info("================> {}", proxy);
                    proxyDao.save(new Proxy(proxy.getIp(), proxy.getPort(), proxy.getProto()));
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                latch.countDown();
            }
        }
    }

    public void validateFromDb() {
        List<Proxy> allProxy = proxyDao.getAllProxy();
        if (CollectionUtils.isNotEmpty(allProxy)) {
            final CountDownLatch latch = new CountDownLatch(allProxy.size());
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            allProxy.forEach(proxy -> executorService.execute(new ValidateJob(proxy, latch)));
            for (Proxy proxy : allProxy) {
                executorService.execute(new ValidateJob(proxy, latch));
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
            executorService.shutdownNow();
        }
    }

    /**
     * 验证数据库中的代理任务
     */
    class ValidateJob implements Runnable {

        private Proxy proxy;

        private CountDownLatch latch;

        ValidateJob(Proxy proxy, CountDownLatch latch) {
            this.proxy = proxy;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                if (validateGetSong(proxy, httpConfig.getProxyValidateTimeout())) {
                    proxyDao.delete(proxy.getId());
                    LOGGER.error("disable proxy={}", proxy.getProxyStr());
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                latch.countDown();
            }
        }
    }
    
}
