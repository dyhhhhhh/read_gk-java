package com.dyhhhhh.common.proxy;

import com.dyhhhhh.common.ThreadLocalHolder;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.Proxy;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 代理管理器
 */
@Component
public class ProxyManager {

    private static final Logger logger = LoggerFactory.getLogger(ProxyManager.class);
    @Getter
    private final List<Proxy> proxyPool = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    @Value("${proxy.poolSize}")
    private int TARGET_POOL_SIZE;

    private final OkHttpClient tempClient = new OkHttpClient();

    private final List<HttpProxy> activeProxies;
    @Getter
    private boolean proxyEnabled; // 新增代理启用状态标识

    public ProxyManager(ProxyFactory proxyFactory) {
        this.activeProxies = proxyFactory.getActiveProxies();
        this.proxyEnabled = !activeProxies.isEmpty();
    }
    //初始化代理池
    @PostConstruct
    public void init() {
        if (proxyEnabled) {
            refreshProxyPool();
        } else {
            logger.info("代理功能已禁用，所有请求将直接发送");
        }
    }

    /**
     * 对当前线程绑定代理
     *
     * @return Proxy
     */
    public synchronized Proxy bindProxyToThread() {
        //如果当前没有 enabled 为true的代理，则直接不使用代理
        if (!proxyEnabled) {
            ThreadLocalHolder.clearProxy(); // 确保清除可能的残留代理
            return null;
        }
        Proxy current = ThreadLocalHolder.getCurrentProxy();
        if (current != null && proxyPool.contains(current)) {
            return current;
        }

        // 强制刷新直到代理池不为空或达到最大尝试次数
        int refreshAttempts = 3;
        while (proxyPool.isEmpty() && refreshAttempts-- > 0) {
            refreshProxyPool();
        }

        if (proxyPool.isEmpty()) {
            logger.warn("代理池刷新失败，尝试重新获取新代理...");
            //最后一次
            refreshProxyPool();
            if (proxyPool.isEmpty()) { // 刷新后仍然为空
                logger.error("代理池刷新失败，本次请求将不使用代理");
                return null;
            }
        }

        Proxy newProxy = proxyPool.get(random.nextInt(proxyPool.size()));
        ThreadLocalHolder.setCurrentProxy(newProxy);
        return newProxy;
    }

    /**
     * 代理失效策略
     *
     * @param proxy
     */
    public void markProxyFailed(Proxy proxy) {
        //为false时不存在代理，直接跳过
        if (!proxyEnabled) return;

        proxyPool.remove(proxy);
        if (proxy.equals(ThreadLocalHolder.getCurrentProxy())) {
            ThreadLocalHolder.clearProxy();
        }
    }
    /**
     * 从 熊猫代理 获取新的ip
     * link:<a href="https://www.xiongmaodaili.com/">...</a>
     */
    public void refreshProxyPool() {
        if (!proxyEnabled) return;

        if (activeProxies.isEmpty()) {
            logger.warn("未发现代理配置，将不使用代理！");
            return;
        }
        // 清空无效代理池并重新获取
        proxyPool.clear();
        //循环取出ip，直至大于=代理池大小或消费完取出次数
        int maxRetries = 5;
        //用于模除取余
        int currentIndex = 0;
        while (proxyPool.size() < TARGET_POOL_SIZE && maxRetries-- > 0) {
            HttpProxy httpProxy = activeProxies.get(currentIndex % activeProxies.size());
            httpProxy.refreshProxyPool(tempClient,proxyPool);
            currentIndex++;
            if (proxyPool.isEmpty()) {
                logger.info("正在尝试第 {} 次获取代理...", (5 - maxRetries));
            }
        }
        logger.info("代理池更新完成，当前IP数量：{}", proxyPool.size());
    }

}
