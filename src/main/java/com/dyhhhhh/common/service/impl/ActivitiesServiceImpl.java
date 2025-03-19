package com.dyhhhhh.common.service.impl;

import com.alibaba.fastjson2.JSON;
import com.dyhhhhh.bean.ModuleActivitiesBean;
import com.dyhhhhh.bean.activities.AllActivities;
import com.dyhhhhh.bean.activities.Exam;
import com.dyhhhhh.bean.activities.LearningActivities;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.CommonClass;
import com.dyhhhhh.common.service.ActivitiesService;
import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivitiesServiceImpl implements ActivitiesService {

    @Autowired
    private RequestHttpConfig httpConfig;

    /**
     * 获取模块下面所有的活动
     */
    @Override
    public AllActivities getModuleActivities(String courses_id, ModuleActivitiesBean moduleActivitiesBean) {
        String response = httpConfig.get(ApiEndpoints.BASE_URL + ApiEndpoints.Course.ACTIVITIES.formatted(courses_id), moduleActivitiesBean.toString());
        HashMap<String, Object> parse = (HashMap<String, Object>) JSON.parse(response);
        //存储课程和考试
        AllActivities all_activities = new AllActivities();

        List<Map<String, Object>> exams = (List<Map<String, Object>>) parse.get("exams");
        List<Map<String, Object>> learning_activities = (List<Map<String, Object>>) parse.get("learning_activities");
        List<Exam> parseExam = parse(exams, Exam.class);
        List<LearningActivities> parseLearningActivities = parse(learning_activities, LearningActivities.class);
        all_activities.setLearningActivitiesList(parseLearningActivities);
        all_activities.setExamList(parseExam);
        return all_activities;
    }

    /**
     * 解析活动
     * @param
     * @return
     */
    private <T extends CommonClass<T>> List<T> parse(List<Map<String, Object>> mapList, Class<T> tClass) {
        List<T> list = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            try{
                T instance = tClass.getDeclaredConstructor().newInstance();
                instance = instance.fromMap(map);
                list.add(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
