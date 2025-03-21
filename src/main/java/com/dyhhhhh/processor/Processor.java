package com.dyhhhhh.processor;

import com.dyhhhhh.bean.activities.AllActivities;
import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.bean.ModuleActivitiesBean;
import com.dyhhhhh.bean.course.Course;
import com.dyhhhhh.bean.modules.CourseModules;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.common.service.ActivitiesService;
import com.dyhhhhh.common.service.impl.CourseServiceImpl;
import com.dyhhhhh.common.executor.ActivityExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 整体逻辑处理器
 */
@Component
public class Processor {
    @Autowired
    private CourseServiceImpl courseServiceImpl;
    @Autowired
    private ActivitiesService activitiesService;
    @Autowired
    private ActivityExecutor activityExecutor;

    public void processCourse(Course course) {
        // 设置课程上下文
        setCourseContext(course);
        // 处理模块
        processModules(course.getId());
        //每次处理完一个课程模块，就清空该课程缓存的已完成活动，防止重复存入
        ThreadLocalHolder.currentCommonInfo().clearMaps();
    }

    /**
     * 设置课程上下文
     * @param course
     */
    private void setCourseContext(Course course) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        commonInfo.setCourse_code(course.getCourse_code());
        commonInfo.setCourse_id(course.getId());
        commonInfo.setCourse_name(course.getName());
    }


    /**
     * 模块控制器
     * @param courseId
     */
    private void processModules(String courseId) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        List<CourseModules> modules = courseServiceImpl.getModulesForCourses(courseId);
        //获取本课程全部已完结活动
        courseServiceImpl.my_completeness(courseId);

        //获取模块参数
        ModuleActivitiesBean activityParams = new ModuleActivitiesBean();

        modules.forEach(module -> {
            //获取当前课程id，去获取模块
            activityParams.getModule_ids().add(module.getId());
            commonInfo.setModule_id(Long.valueOf(module.getId()));
            commonInfo.setModule_name(module.getName());
            //执行模块处理器
            processModuleActivities(courseId, activityParams, module);
            //当前模块运行完毕后，清空列表防止重复运行
            activityParams.clearModule_ids();
        });

    }

    /**
     * 活动处理器
     * @param courseId
     * @param params
     * @param module
     */
    private void processModuleActivities(String courseId, ModuleActivitiesBean params, CourseModules module) {

        //获取模块下面所有的活动
        AllActivities activities = activitiesService.getModuleActivities(courseId, params);
        // 处理学习活动
        activityExecutor.executeLearningActivities(activities.getLearningActivitiesList());
        // 处理考试（待实现）
    }
}
