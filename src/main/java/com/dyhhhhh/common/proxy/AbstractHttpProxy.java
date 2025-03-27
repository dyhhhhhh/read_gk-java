package com.dyhhhhh.common.proxy;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public abstract class AbstractHttpProxy implements HttpProxy {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpProxy.class);

    @Autowired
    protected ProxyProperties proxyProperties;

    private Optional<String> resolveApiUrl() {
        ProxyProperties.Provider provider = proxyProperties.getProvidersByType(getType());
        if (provider == null) {
            System.err.println("未找到代理配置: " + getType());
            return Optional.empty();
        }
        String url = provider.getApiUrl();
        if (StringUtils.isEmpty(url)) {
            System.err.println("URL未配置: " + getType());
            return Optional.empty();
        }
        return Optional.of(url);
    }

    /**
     * 通用的请求代理方法，抽成父类
     *
     * @param tempClient:用于获取请求的对象
     * @param proxyPool:公共代理池
     */
    @Override
    public void refreshProxyPool(OkHttpClient tempClient, List<Proxy> proxyPool) {
        //模板方法修改为三次重试，增强可靠性
        resolveApiUrl().ifPresent(url -> {
            int retry = 3;
            while (retry-- > 0){
                //请求熊猫代理https
                Request request = new Request.Builder().url(url).build();
                try (Response response = tempClient.newCall(request).execute()) {
                    //强行休眠一秒
                    Thread.sleep(1200L);
                    if (response.isSuccessful()) {
                        //解析返回的数据，存放到代理池中
                        List<Proxy> proxies = parseResponse(response.body().string());
                        proxies.forEach(proxy -> {
                            if (!proxyPool.contains(proxy) && !Objects.isNull(proxy)) {
                                logger.debug("{} 新增代理:{}", getType(), formatProxy(proxy));
                                proxyPool.add(proxy);
                            }
                        });
                        //成功就直接退出
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("代理更新失败: " + e.getMessage());
                }
            }

        });
    }

    //格式化解析代理
    public static String formatProxy(Proxy proxy) {
        if (proxy.type() == Proxy.Type.DIRECT) return "直连";
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        return String.format("%s://%s:%d",
                proxy.type().name(),
                addr.getHostString(),
                addr.getPort());
    }

}
