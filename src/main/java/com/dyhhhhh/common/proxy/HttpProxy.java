package com.dyhhhhh.common.proxy;

import okhttp3.OkHttpClient;

import java.net.Proxy;
import java.util.List;

/**
 * 扩展代理的接口
 */
public interface HttpProxy {

    /**
     * 获取类型
     * @return
     */
    String getType();
    /**
     * 刷新代理到代理池
     * @param tempClient:用于获取请求的对象
     * @param proxyPool:公共代理池
     * @return
     */
    void refreshProxyPool(OkHttpClient tempClient, List<Proxy> proxyPool);

    /**
     * 都必须具有解析json方法
     * @param json
     */
    List<Proxy> parseResponse(String json);
}
