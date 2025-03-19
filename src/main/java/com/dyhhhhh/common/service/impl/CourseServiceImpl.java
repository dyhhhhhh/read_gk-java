package com.dyhhhhh.common.service.impl;

import com.alibaba.fastjson2.JSON;
import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.bean.course.Course;
import com.dyhhhhh.bean.MyCoursesBean;
import com.dyhhhhh.bean.modules.CourseModules;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.common.enums.CourseStatus;
import com.dyhhhhh.common.service.CourseService;
import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程服务类
 */
@Service
public class CourseServiceImpl implements CourseService {
    private final RequestHttpConfig httpConfig;

    @Autowired
    private CommonApisService commonApisService;


    public CourseServiceImpl(RequestHttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    /**获取所有课程
     *
     * @param status
     * @return
     */
    @Override
    public List<Course> getCourses(CourseStatus status,boolean enableDelay) {
        String params = switch (status) {
            case CLOSED -> new MyCoursesBean().closed_toString();
            case ONGOING -> new MyCoursesBean().ongoing_toString();
        };
        //获取课程前发送
        commonApisService.send_user_visits(new HashMap<>());
        String response = httpConfig.get(ApiEndpoints.BASE_URL + ApiEndpoints.Course.MY_COURSES, params,enableDelay);

        return parseCourses(response);
    }

    @Override
    public List<Course> getCourses(CourseStatus status, String cookie) {
        PersonalInformation personalInformation = new PersonalInformation(cookie);
        ThreadLocalHolder.setPersonalInformation(personalInformation);
        return getCourses(status,false);
    }


    private List<Course> parseCourses(String response) {
        ArrayList<Course> courseArrayList = new ArrayList<>();
        // 解析响应生成Course对象列表...
        HashMap<String, Object> parse = (HashMap<String, Object>) JSON.parse(response);
        List<Map<String, Object>> courses = (List<Map<String, Object>>) parse.get("courses");
        for (Map<String, Object> data : courses) {
            Course course = Course.fromMap(data);
            courseArrayList.add(course);
        }
        return courseArrayList;
    }
    /**获取课程下面的所有模块
     *
     * @param coursesId
     * @return
     */
    @Override
    public List<CourseModules> getModulesForCourses(String coursesId) {
        commonApisService.send_user_visits(new HashMap<>());
        String response = httpConfig.get(ApiEndpoints.BASE_URL + ApiEndpoints.Course.MODULES.formatted(coursesId), "");
        return parseModules(response);
    }
    private List<CourseModules> parseModules(String response){
        List<CourseModules> courseModulesList = new ArrayList<>();
        HashMap<String, List<Map<String, Object>>> parse = (HashMap<String, List<Map<String, Object>>>) JSON.parse(response);
        List<Map<String, Object>> modules = parse.get("modules");
        for (Map<String, Object> module : modules) {
            CourseModules courseModules = CourseModules.fromMap(module);
            courseModulesList.add(courseModules);
        }
        return courseModulesList;
    }

    /**
     * 获取当前课程全部已学完的活动,初始化当前全局变量中
     */
    @Override
    public void my_completeness(String coursesId) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();

        String response = httpConfig.get(ApiEndpoints.BASE_URL + ApiEndpoints.Course.MY_COMPLETENESS.formatted(coursesId));
        HashMap<String, Object> hashMap = (HashMap<String, Object>) JSON.parse(response);
        //当前课程进度
        double studyCompleteness = Double.parseDouble(hashMap.get("study_completeness").toString());

        HashMap<String, Object> completed_result = (HashMap<String, Object>) hashMap.get("completed_result");
        HashMap<String, Object> completed = (HashMap<String, Object>) completed_result.get("completed");
        //所有已完成的考试
        List<String> exam_activity =  (List<String>) completed.get("exam_activity");
        //所有已完成的活动
        List<String> learning_activity =  (List<String>) completed.get("learning_activity");
        //将两个list转换为map
        convertListsToMap(exam_activity,learning_activity);
        commonInfo.setStudy_completeness(studyCompleteness);
    }

    private static void convertListsToMap(List<String> examActivity, List<String> learningActivity) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        HashMap<String, Boolean> examActivity1 = commonInfo.getExam_activity();
        HashMap<String, Boolean> activity1 = commonInfo.getLearning_activity();
        // 遍历 exam_activity 列表并添加到 Map 中
        for (Object exam : examActivity) {
            String examStr = String.valueOf(exam);
            examActivity1.put(examStr, true);
        }
        // 遍历 learning_activity 列表并添加到 Map 中
        for (Object activity : learningActivity) {
            String activityStr = String.valueOf(activity);
            activity1.put(activityStr, true);
        }
    }
}
