package com.dyhhhhh.config;

import com.dyhhhhh.common.ThreadLocalHolder;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpConfig.class);

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .proxySelector(new ThreadBoundProxySelector())
                .connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES))
                .addInterceptor(new LoggingInterceptor()) // 新增日志拦截器
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // 线程绑定的代理选择器
    private static class ThreadBoundProxySelector extends ProxySelector {
        @Override
        public List<Proxy> select(URI uri) {
            Proxy proxy = ThreadLocalHolder.getCurrentProxy();
            return proxy != null ?
                    Collections.singletonList(proxy) :
                    Collections.singletonList(Proxy.NO_PROXY);
        }

        @Override
        public void connectFailed(URI uri,
                                  SocketAddress sa,
                                  IOException ioe) {
            logger.error("连接失败: {}", sa);
        }
    }

    /**
     * 日志拦截器（打印代理信息）
     */
    private static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            // 继续执行请求
            return chain.proceed(request);
        }

    }

    private static String proxyAddress(Proxy proxy) {
        if (proxy.address() instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress) proxy.address();
            return addr.getHostString() + ":" + addr.getPort();
        }
        return proxy.address().toString();
    }

}
