package com.dyhhhhh.common.apis;



import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 打开外部网站活动
 */
@Component
public class WebLinkStrategy extends PageStrategy {
    //直接执行页面逻辑
    @Override
    public void execute(Long activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig) {
        super.execute(activityId, activityDetails,httpConfig);
    }
}
