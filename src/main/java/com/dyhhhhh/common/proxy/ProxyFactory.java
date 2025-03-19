package com.dyhhhhh.common.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProxyFactory {

    private final List<HttpProxy> allProxies;
    private final ProxyProperties proxyProperties;

    @Autowired
    public ProxyFactory(List<HttpProxy> allProxies, ProxyProperties proxyProperties) {
        this.allProxies = allProxies;
        this.proxyProperties = proxyProperties;
    }

    // 获取所有启用的代理（根据配置）
    public List<HttpProxy> getActiveProxies() {
        // 从配置中读取 enabled=true 的代理类型，返回对应实例
        return allProxies.stream()
                .filter(proxy -> isProxyEnabled(proxy.getType()))
                .collect(Collectors.toList());
    }

    /**
     * 检查指定类型代理是否启用
     */
    private boolean isProxyEnabled(String proxyType) {
        return proxyProperties.getProviders().stream()
                .anyMatch(p -> p.getType().equals(proxyType) && p.isEnabled());
    }

}
