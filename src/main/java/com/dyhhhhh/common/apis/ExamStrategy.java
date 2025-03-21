package com.dyhhhhh.common.apis;

import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 考试的具体执行
 */
@Component
public class ExamStrategy implements Strategy{
    @Override
    public void execute(Long activityId, HashMap<String, Object> activityDetails,RequestHttpConfig httpConfig) {
        System.out.println("跳过 考试");
    }
}
