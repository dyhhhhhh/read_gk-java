package com.dyhhhhh.controller;

import com.dyhhhhh.bean.course.Course;
import com.dyhhhhh.common.enums.CourseStatus;
import com.dyhhhhh.common.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/getCourses")
    public ResponseEntity<?> submitTasks(@RequestParam("cookie") String cookie) {
        /**
         * 只请求课程不用代理和
         */
        List<Course> courses = courseService.getCourses(CourseStatus.ONGOING, cookie);
        return ResponseEntity.ok(courses);
    }

}
