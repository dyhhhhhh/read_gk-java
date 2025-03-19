package com.dyhhhhh.bean.activities;

import com.dyhhhhh.common.CommonClass;

import java.util.Map;

/**
 * 活动领域对象
 */
public class LearningActivities extends CommonClass<LearningActivities> {
    @Override
    public LearningActivities fromMap(Map<String, Object> data) {
        LearningActivities learningActivities = new LearningActivities();
        learningActivities.setId(String.valueOf(data.get("id")));
        learningActivities.setType(String.valueOf(data.get("type")));
        learningActivities.setTitle(String.valueOf(data.get("title")));
        return learningActivities;
    }

}
