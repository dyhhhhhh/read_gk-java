package com.dyhhhhh.common.apis;

import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class HomeworkStrategy implements Strategy{
    @Override
    public void execute(Long activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig) {
        System.out.println("跳过 大作业");
    }
}
