package com.dyhhhhh.common.apis;

import com.dyhhhhh.bean.activities.LearningActivities;
import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.common.enums.ActivityType;
import com.dyhhhhh.config.RequestHttpConfig;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

//上下文类，用于执行活动类型的方法
@Setter
@Component
public class Context {
    @Autowired
    private RequestHttpConfig httpConfig;
    @Autowired
    private CommonApisService commonApisService;
    @Autowired
    private StrategyFactory strategyFactory;

    public void executeStrategy(LearningActivities learningActivities) {
        // 获取活动类型
        ActivityType type = ActivityType.fromTypeName(learningActivities.getType());
        // 从工厂获取策略
        Strategy strategy = strategyFactory.getStrategy(type);
        HashMap<String, Object> details = commonApisService.getActivityDetails(learningActivities.getId());
        //开始执行
        strategy.execute(learningActivities.getId(),details,httpConfig);
    }
}
