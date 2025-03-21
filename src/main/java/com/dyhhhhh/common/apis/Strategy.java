package com.dyhhhhh.common.apis;

import com.dyhhhhh.config.RequestHttpConfig;


import java.util.HashMap;

//共享接口
public interface Strategy {
    void execute(Long activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig);
}
