package com.dyhhhhh.common;

import java.util.*;

//发送请求公共参数
public class BrowserInfo {

    // 存储浏览器和对应多个 User-Agent 的映射
    private static final Map<String, List<String>> browserUserAgents = new HashMap<>();

    static {
        // 初始化 Chrome 浏览器的 User-Agent 列表
        List<String> chromeUserAgents = new ArrayList<>();
        chromeUserAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        chromeUserAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");
        browserUserAgents.put("Chrome", chromeUserAgents);

        // 初始化 Firefox 浏览器的 User-Agent 列表
        List<String> firefoxUserAgents = new ArrayList<>();
        firefoxUserAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0");
        firefoxUserAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:90.0) Gecko/20100101 Firefox/90.0");
        browserUserAgents.put("Firefox", firefoxUserAgents);

        // 初始化 Safari 浏览器的 User-Agent 列表
        List<String> safariUserAgents = new ArrayList<>();
        safariUserAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15");
        safariUserAgents.add("Mozilla/5.0 (iPad; CPU OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Mobile/15E148 Safari/604.1");
        browserUserAgents.put("Safari", safariUserAgents);

        // 初始化 Edge 浏览器的 User-Agent 列表
        List<String> edgeUserAgents = new ArrayList<>();
        edgeUserAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59");
        edgeUserAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Edg/92.0.902.84");
        edgeUserAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36 Edg/93.0.961.47");
        browserUserAgents.put("Edge", edgeUserAgents);
    }

    /**
     * 随机获取浏览器和对应的 User-Agent
     * @return 包含浏览器名称和 User-Agent 的数组，索引 0 为浏览器名称，索引 1 为 User-Agent
     */
    public static String[] getRandomBrowserAndUserAgent() {
        Random random = new Random();
        // 获取所有浏览器名称的集合
        List<String> browsers = new ArrayList<>(browserUserAgents.keySet());
        // 随机选择一个浏览器名称
        String randomBrowser = browsers.get(random.nextInt(browsers.size()));
        // 获取该浏览器对应的 User-Agent 列表
        List<String> userAgents = browserUserAgents.get(randomBrowser);
        // 随机选择一个 User-Agent
        String randomUserAgent = userAgents.get(random.nextInt(userAgents.size()));
        return new String[]{randomBrowser, randomUserAgent};
    }
}
