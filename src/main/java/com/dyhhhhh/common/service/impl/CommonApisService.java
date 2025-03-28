package com.dyhhhhh.common.service.impl;

import com.alibaba.fastjson2.JSON;
import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.config.RequestHttpConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 公共的api
 */
@Component
public class CommonApisService {

    @Autowired
    private RequestHttpConfig httpConfig;

    /**
     * 用户首次进入发送用户访问请求
     */
    public void send_user_visits(HashMap<String, Object> extra_param) {
        //用户首次进入发送该请求
        //组装参数
        HashMap<String, Object> param = new HashMap<>();
        PersonalInformation personalInformation = ThreadLocalHolder.getPersonalInformation();
        param.put("browser", personalInformation.getBrowser_user_agent()[0]);
        param.put("dep_code", personalInformation.getDep_code());
        param.put("dep_id", personalInformation.getDep_id());
        param.put("dep_name", personalInformation.getDep_name());
        param.put("is_teacher", personalInformation.is_teacher());
        param.put("org_code", personalInformation.getOrg_code());
        param.put("org_id", personalInformation.getOrg_id());
        param.put("org_name", personalInformation.getOrg_name());
        param.put("user_agent", personalInformation.getBrowser_user_agent()[1]);
        param.put("user_id", personalInformation.getUser_id());
        param.put("user_name", personalInformation.getUser_name());
        param.put("user_no", personalInformation.getUser_no());
        param.put("visit_duration", ThreadLocalRandom.current().nextInt(196 + 10));
        //定制化追加额外参数
        param.putAll(extra_param);
        String result = httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.User.VISITS, "", param);
        if (StringUtils.isBlank(result)) {
            System.out.println("发送user_visits");
        }
    }

    /**
     * 获取活动的详细信息
     *
     * @param activityId:活动id
     */
    public HashMap<String, Object> getActivityDetails(String activityId) {
        return (HashMap<String, Object>) JSON.parse(httpConfig.get(ApiEndpoints.BASE_URL + ApiEndpoints.Activity.ACTIVITIES.formatted(activityId), ""));
    }

    /**
     * 检测当前活动的状态
     *
     * @param activityId
     * @param data
     * @return
     */

    public HashMap<String, Object> activities_state(Long activityId, Object data) {
        String result = httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.Course.ACTIVITIES_READ.formatted(activityId), "", data);
        return (HashMap<String, Object>) JSON.parse(result);
    }



    /**
     * 检测活动是否已完成
     */
    public Boolean is_full(Long activityId, Object data) {
        HashMap<String, Object> checked = activities_state(activityId, data);
        String completeness = checked.get("completeness").toString();
        return "full".equals(completeness);
    }

    /**
     * 发送正在学习中
     *
     * @param activityType
     */
    public void post_learning_activity(String activityType, HashMap<String, Object> extra_param) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        System.out.println("发送学习中...");
        PersonalInformation personalInformation = ThreadLocalHolder.getPersonalInformation();

        // 使用 Map 来装参数
        Map<String, Object> paramMap = new HashMap<>();

        // 类的拷贝，将 PersonalInformation 的属性放入 Map 中
        try {
            BeanUtils.populate(paramMap, BeanUtils.describe(personalInformation));
        } catch (Exception e) {
            e.printStackTrace();
        }
        paramMap.put("action", "open");
        paramMap.put("activity_id", commonInfo.getActivity_id());
        paramMap.put("activity_name", null);
        paramMap.put("activity_type", activityType);
        paramMap.put("assessment_type", "normal");
        paramMap.put("channel", "web");
        paramMap.put("course_code", commonInfo.getCourse_code());
        paramMap.put("course_id", commonInfo.getCourse_id());
        paramMap.put("course_name", commonInfo.getCourse_name());
        paramMap.put("enrollment_role", "student");
        paramMap.put("master_course_id", commonInfo.getMaster_course_id());
        paramMap.put("mode", "normal");
        paramMap.put("module", null);
        paramMap.put("target_info", new HashMap<>());
        long timestamp = System.currentTimeMillis();
        paramMap.put("ts", timestamp);
        paramMap.putAll(extra_param);
        httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.Activity.LEARNING_ACTIVITY, paramMap);
        System.out.println("发送完毕");
    }

    //该容器不存放任何数据，不存在线程安全
    private static final HashMap<String, Object> extra_param = new HashMap<>(0);

    /**
     * 发送正在学习中(无额外参数)
     *
     * @param activityType
     */
    public void post_learning_activity(String activityType) {
        post_learning_activity(activityType, extra_param);
    }

    /**
     * 解析html获取master_course_id
     */
    @Async
    public void get_master_course_id(String courseId) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        String text = httpConfig.get(ApiEndpoints.BASE_URL + ApiEndpoints.Course.FULL_SCREEN.formatted(courseId));
        Document document = Jsoup.parse(text);
        Element masterCourseId = document.select("#masterCourseId").first();
        assert masterCourseId != null;
        commonInfo.setMaster_course_id(Long.valueOf(masterCourseId.val()));
    }


    /**
     * 获取所有课程的进度
     *
     * @param MyCourses
     * @return
     */
    public Map<String, Double> getAllCoursesSchedule(List<Map<String, Object>> MyCourses) {
        HashMap<String, Double> stringIntegerHashMap = new HashMap<>();
        //检测是否可以强转
        for (Map<String, Object> cour : MyCourses) {
            stringIntegerHashMap.put(String.valueOf(cour.get("name")), Double.valueOf(cour.get("completeness").toString()));
        }
        return stringIntegerHashMap;
    }

}
