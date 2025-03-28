package com.dyhhhhh.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * 全局公共对象
 */
@Getter
@Setter
public class CommonInfo {
    private Long activity_id;
    private String activity_title;
    private String course_id;
    private String course_code;
    private String course_name;
    private Long module_id;
    private String module_name;
    private Long master_course_id;
    private HashMap<String, Boolean> learning_activity;
    private HashMap<String, Boolean> exam_activity;
    private Double study_completeness;

    public CommonInfo() {
        this.learning_activity = new HashMap<>();
        this.exam_activity = new HashMap<>();
    }

    public void clearMaps() {
        learning_activity.clear();
        exam_activity.clear();
    }
}
