package com.dyhhhhh.common.service;

import com.dyhhhhh.bean.course.Course;
import com.dyhhhhh.bean.modules.CourseModules;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.enums.CourseStatus;

import java.util.List;

/**
 * 课程相关方法
 */
public interface CourseService {
    /**
     * 获取所有课程
     * @param status
     * @return
     */
    List<Course> getCourses(CourseStatus status,boolean enableDelay);

    /**
     * 获取所有课程
     * @param status
     * @return
     */
    List<Course> getCourses(CourseStatus status,String cookie);
    /**
     * 获取课程下面的所有模块
     * @param coursesId
     * @return
     */
    List<CourseModules> getModulesForCourses(String coursesId);

    /**
     * 获取当前课程所有已完成的魔抗
     * @param coursesId
     */
    void my_completeness(String coursesId);

}
