package com.dyhhhhh.common.service;

import java.util.List;

/**
 * 运行逻辑
 */

public interface StartService {


    // 统一入口方法（支持多种参数组合）
    void start(Object identifier);

    void start(Object identifier, List<String> courseIds);

    // 初始化用户上下文（统一处理 AccountDTO 和 Cookie）
    void initUserContext(Object identifier);

}
