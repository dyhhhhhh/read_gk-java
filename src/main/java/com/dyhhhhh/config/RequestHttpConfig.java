package com.dyhhhhh.config;

import com.alibaba.fastjson2.JSON;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.common.proxy.ProxyManager;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 发送请求
 */
@Component
public class RequestHttpConfig {
    //注入bean配置
    private final OkHttpClient okHttpClient;
    private final ProxyManager proxyManager;

    private static final Logger logger = LoggerFactory.getLogger(RequestHttpConfig.class);

    // 固定延迟范围（秒）
    @Value("${request.delay.min_delay}")
    private int MIN_DELAY;
    @Value("${request.delay.max_delay}")
    private int MAX_DELAY;

    @Autowired
    public RequestHttpConfig(OkHttpClient okHttpClient,
                             ProxyManager proxyManager) {
        this.okHttpClient = okHttpClient;
        this.proxyManager = proxyManager;
    }

    //=============== get请求 ===============//
    public String get(String url) {
        return get(url, "", true);
    }

    public String getImmediately(String url) {
        return get(url, "", false);
    }

    public String get(String url, String param) {
        return get(url, param, true);
    }

    public String get(String url, String param, boolean enableDelay) {
        return executeRequest(buildGetCallable(url, param, enableDelay));
    }

    //=============== post请求 ===============//
    public String post(String url, Object body) {
        return post(url, "", body, true);
    }

    public String postImmediately(String url, Object body) {
        return post(url, "", body, false);
    }

    public String post(String url, String pathParam, Object body) {
        return post(url, pathParam, body, true);
    }

    public String post(String url, String pathParam, Object body, boolean enableDelay) {
        return executeRequest(buildPostCallable(url, pathParam, body, enableDelay));
    }

    /**
     * 构造get请求
     *
     * @param url
     * @param param
     * @param enableDelay:是否延迟
     * @return
     */
    private Callable<Response> buildGetCallable(String url, String param, boolean enableDelay) {
        return () -> {
            applyDelayIfNeeded(url + param, enableDelay);
            PersonalInformation info = ThreadLocalHolder.getPersonalInformation();
            return okHttpClient.newCall(new Request.Builder()
                    .url(url + param)
                    .header("cookie", info.getCookie())
                    .header("user-agent", info.getBrowser_user_agent()[1])
                    .build()).execute();
        };
    }

    /**
     * 构造post请求
     *
     * @param url
     * @param pathParam
     * @param body
     * @param enableDelay:是否延迟
     * @return
     */
    private Callable<Response> buildPostCallable(String url, String pathParam, Object body, boolean enableDelay) {
        return () -> {
            applyDelayIfNeeded(url + pathParam, enableDelay);
            PersonalInformation info = ThreadLocalHolder.getPersonalInformation();
            MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
            RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(body));

            return okHttpClient.newCall(new Request.Builder()
                    .url(url + pathParam)
                    .post(requestBody)
                    .header("cookie", info.getCookie())
                    .header("user-agent", info.getBrowser_user_agent()[1])
                    .header("content-type", mediaType.toString())
                    .build()).execute();
        };
    }

    //根据需要来设置延迟
    private void applyDelayIfNeeded(String fullUrl, boolean enableDelay) {
        if (enableDelay) {
            try {
                int delaySeconds = ThreadLocalRandom.current().nextInt(MIN_DELAY, MAX_DELAY + 1);
                logger.debug("请求延迟: {}s - {}", delaySeconds, fullUrl);
                Thread.sleep(delaySeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 发起请求主体
     *
     * @param requestCallable
     * @return
     */
    private String executeRequest(Callable<Response> requestCallable) {

        try {
            if (proxyManager.isProxyEnabled() && !proxyManager.getProxyPool().isEmpty()) {
                Proxy proxy = proxyManager.bindProxyToThread();
                logger.debug("使用代理: {}", formatProxy(proxy));
            }

            try (Response response = requestCallable.call()) {
                ThreadLocalHolder.resetRetry();
                return response.body().string();
            }
        } catch (IOException e) {
            handleProxyFailure();
            if (ThreadLocalHolder.isOverRetryLimit(3)) {
                throw new RuntimeException("超过最大重试次数", e);
            }
            return executeRequest(requestCallable); // 递归重试
        } catch (Exception e) {
            throw new RuntimeException("请求异常", e);
        }
    }

    //处理ip代理失效，从当前线程管理中删除。
    private void handleProxyFailure() {
        Proxy currentProxy = ThreadLocalHolder.getCurrentProxy();
        if (currentProxy != null) {
            logger.warn("代理失效: {}", formatProxy(currentProxy));
            proxyManager.markProxyFailed(currentProxy);
            ThreadLocalHolder.clearProxy();
        }
        ThreadLocalHolder.incrementRetry();
    }

    //格式化解析代理
    private String formatProxy(Proxy proxy) {
        if (proxy.type() == Proxy.Type.DIRECT) return "直连";
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        return String.format("%s://%s:%d",
                proxy.type().name(),
                addr.getHostString(),
                addr.getPort());
    }
}
