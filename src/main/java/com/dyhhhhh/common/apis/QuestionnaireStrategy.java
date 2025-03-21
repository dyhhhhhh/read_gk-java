package com.dyhhhhh.common.apis;

import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 调查问卷任务
 */
@Component
public class QuestionnaireStrategy implements Strategy{
    @Override
    public void execute(String activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig) {
        System.out.println("跳过 调查问卷 ");
    }
}
