package com.dyhhhhh.common;


import com.dyhhhhh.bean.CommonInfo;

import java.net.Proxy;


//线程管理个人信息
public class ThreadLocalHolder {
    // 使用 ThreadLocal 存储当前线程的
    private static final ThreadLocal<PersonalInformation> USER_INFO = new ThreadLocal<>();
    private static final ThreadLocal<CommonInfo> COMMON_INFO = ThreadLocal.withInitial(CommonInfo::new);

    // 新增代理存储
    private static final ThreadLocal<Proxy> CURRENT_PROXY = new ThreadLocal<>();
    private static final ThreadLocal<Integer> RETRY_COUNT = ThreadLocal.withInitial(() -> 0);


    public static Proxy getCurrentProxy() {
        return CURRENT_PROXY.get();
    }

    public static void setCurrentProxy(Proxy proxy) {
        CURRENT_PROXY.set(proxy);
    }

    public static void clearProxy() {
        CURRENT_PROXY.remove();
    }

    public static void incrementRetry() {
        RETRY_COUNT.set(RETRY_COUNT.get() + 1);
    }

    public static void resetRetry() {
        RETRY_COUNT.set(0);
    }

    public static boolean isOverRetryLimit(int max) {
        return RETRY_COUNT.get() >= max;
    }

    /**
     * 将 信息 存储到当前线程
     *
     * @param personalInformationMap 键值对
     */
    public static void setPersonalInformation(PersonalInformation personalInformationMap) {
        USER_INFO.set(personalInformationMap);
    }

    public static CommonInfo currentCommonInfo() {
        return COMMON_INFO.get();
    }

    /**
     * 从当前线程获取 信息
     *
     * @return Cookie 键值对
     */
    public static PersonalInformation getPersonalInformation() {
        PersonalInformation info = USER_INFO.get();
        if (info == null) {
            throw new IllegalStateException("用户信息未初始化！");
        }
        return info;
    }

    /**
     * 清除当前线程的 信息
     */
    public static void clear() {
        USER_INFO.remove();
        COMMON_INFO.remove();
    }

}
