package com.dyhhhhh.config;

import com.dyhhhhh.common.proxy.HttpProxy;
import com.dyhhhhh.common.proxy.ProxyManager;
import com.dyhhhhh.common.proxy.xiongmaoProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyManagerConfig {

    @Bean
    public HttpProxy xiongmao() {
        return new xiongmaoProxy();
    }

}
