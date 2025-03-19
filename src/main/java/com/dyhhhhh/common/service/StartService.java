package com.dyhhhhh.common.service;

import com.dyhhhhh.bean.activities.Exam;
import com.dyhhhhh.bean.course.Course;
import com.dyhhhhh.bean.dto.AccountDTO;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.common.enums.CourseStatus;
import com.dyhhhhh.common.service.impl.CourseServiceImpl;
import com.dyhhhhh.processor.Processor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 运行逻辑
 */
@Slf4j
@Service
public class StartService {
    @Autowired
    private UserService userService;
    //处理器
    @Autowired
    private Processor processor;
    @Autowired
    //课程服务
    private CourseServiceImpl courseServiceImpl;

    // 统一入口方法（支持多种参数组合）
    public void start(Object identifier) {
        start(identifier, Collections.emptyList());
    }

    public void start(Object identifier, List<String> courseIds) {
        try {
            initUserContext(identifier);
            processCourses(courseIds);
        } finally {
            ThreadLocalHolder.clear(); // 确保资源释放
        }
    }

    // 初始化用户上下文（统一处理 AccountDTO 和 Cookie）
    public void initUserContext(Object identifier) {
        String cookie = resolveCookie(identifier);
        PersonalInformation info = new PersonalInformation(cookie);
        ThreadLocalHolder.setPersonalInformation(info);
        userService.init(info);
        log.info("用户上下文初始化完成: {}", info.getUser_name());
    }

    // 解析 Cookie（支持 AccountDTO 或直接传递 Cookie）
    private String resolveCookie(Object identifier) {
        if (identifier instanceof AccountDTO) {
            return userService.getCookie((AccountDTO) identifier);
        } else if (identifier instanceof String) {
            return (String) identifier;
        }
        throw new IllegalArgumentException("无效的标识类型: " + identifier.getClass());
    }


    // 课程处理逻辑
    private void processCourses(List<String> courseIds) {
        List<Course> courses = courseServiceImpl.getCourses(CourseStatus.CLOSED,true);
        Set<String> targetIds = Optional.ofNullable(courseIds)
                .map(ids -> ids.stream().collect(Collectors.toSet()))
                .orElse(Collections.emptySet());

        courses.stream()
                .filter(course -> targetIds.isEmpty() || targetIds.contains(course.getId()))
                .forEach(processor::processCourse);
    }
    /**
     * 执行考试逻辑
     */
    private void start_exam(List<Exam> examList){
        throw new UnsupportedOperationException("考试功能尚未实现");
    }
}
