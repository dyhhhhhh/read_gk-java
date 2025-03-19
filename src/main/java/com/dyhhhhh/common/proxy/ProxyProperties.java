package com.dyhhhhh.common.proxy;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {
    private List<Provider> providers = new ArrayList<>();
    //一一对应yml配置的类
    @Data
    public static class Provider {
        private String type;
        private boolean enabled;
        private String apiKey; // 对应 yaml 中的 api-key
        private String apiUrl;
    }
    //获取对应的类型
    public ProxyProperties.Provider getProvidersByType(String type) {
        List<Provider> providerList = providers.stream()
                .filter(provider -> type.equals(provider.getType()) && provider.isEnabled())
                .collect(Collectors.toList());
        if (providerList.size() == 1) {
            return providerList.get(0);
        } else if (providerList.size() > 1) {
            throw new RuntimeException("%s代理type重复，请检查配置!".formatted(type));
        } else {
            throw new RuntimeException("%s代理没有被配置".formatted(type));
        }

    }
}
