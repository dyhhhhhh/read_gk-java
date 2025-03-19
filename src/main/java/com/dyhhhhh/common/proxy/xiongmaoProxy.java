package com.dyhhhhh.common.proxy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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

/**
 * 熊猫代理具体实现
 */
@Component
public class xiongmaoProxy extends AbstractHttpProxy {

    private static final Logger logger = LoggerFactory.getLogger(xiongmaoProxy.class);

    @Autowired
    private ProxyProperties proxyProperties;

    @Override
    public String getType() {
        return "xiongmao";
    }

    @Override
    public void refreshProxyPool(OkHttpClient tempClient, List<Proxy> proxyPool) {
       super.refreshProxyPool(tempClient, proxyPool);
    }

    /**
     * 解析熊猫代理JSON数据
     */
    public void parseResponse(String json, List<Proxy> proxyPool) {
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            JSONArray proxies = jsonObject.getJSONArray("obj");
            proxies.forEach(p -> {
                try {
                    String ip = ((JSONObject) p).getString("ip");
                    int port = ((JSONObject) p).getInteger("port");
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                    if (!proxyPool.contains(proxy)) {
                        proxyPool.add(proxy);
                        logger.debug("{}代理新增 {}:{}", getType(),ip, port);
                    }
                } catch (Exception e) {
                    logger.warn("解析{}代理数据异常: {}", getType(),p);
                }
            });
        } catch (Exception e) {
            logger.error("{}代理数据解析失败: {}", getType(),json);
        }

    }
}
