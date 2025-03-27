package com.dyhhhhh.bean.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 课程领域对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Course {
    private String id;
    private String course_code;
    private String name;
    private double completeness;
    public static Course fromMap(Map<String, Object> data) {
        Course course = new Course();
        course.id = String.valueOf(data.get("id"));
        course.name = String.valueOf(data.get("name"));
        course.course_code =  String.valueOf(data.get("course_code"));
        try {
            course.completeness = Double.parseDouble(data.get("completeness").toString());
        }catch (NullPointerException e){
            course.completeness = 0;
        }
        return course;
    }

    /**
     * 判断课程进度
     * @return
     */
    public boolean isCompleted() {
        return completeness >= 100.0;
    }

}
