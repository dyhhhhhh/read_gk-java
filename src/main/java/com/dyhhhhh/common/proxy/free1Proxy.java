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
import java.util.function.Consumer;

/**
 * 免费代理1 具体实现
 */
@Component
public class free1Proxy extends AbstractHttpProxy{

    private static final Logger logger = LoggerFactory.getLogger(free1Proxy.class);

    @Autowired
    private ProxyProperties proxyProperties;
    @Override
    public String getType() {
        return "free1";
    }

    @Override
    public void refreshProxyPool(OkHttpClient tempClient, List<Proxy> proxyPool) {
        super.refreshProxyPool(tempClient,proxyPool);
    }
    /**
     * 解析free1 JSON数据
     */
    @Override
    public void parseResponse(String json, List<Proxy> proxyPool) {
        try {
            JSONObject root = JSON.parseObject(json);
            if (root.getIntValue("code") != 200) {
                logger.warn("代理响应异常: {}", root.getString("message"));
                return;
            }
            JSONArray proxies = root.getJSONObject("data").getJSONArray("proxies");

            proxies.forEach(p -> {
                String proxyStr = p.toString();
                //解析返回的代理格式
                if (!proxyStr.contains(":")) {
                    logger.warn("无效代理格式: {}", proxyStr);
                    return;
                }

                String[] parts = proxyStr.split(":");
                if (parts.length != 2) {
                    logger.warn("代理格式错误: {}", proxyStr);
                    return;
                }
                try {
                    String ip = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                    if (!proxyPool.contains(proxy)) {
                        proxyPool.add(proxy);
                        logger.debug("{} 新增代理: {}:{}", getType(),ip, port);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("非法端口号: {}", proxyStr);
                }
            });
        } catch (Exception e) {
            logger.error("{}代理数据解析失败: {}", getType(),json);
        }
    }
}
