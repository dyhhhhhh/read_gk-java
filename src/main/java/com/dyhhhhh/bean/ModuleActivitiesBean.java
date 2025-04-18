package com.dyhhhhh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取模块下面的活动
 */
@Data
@AllArgsConstructor
public class ModuleActivitiesBean {
    //要获取模块的id
    private List<String> module_ids;
    //字段
    private String activity_types = "learning_activities,exams,classrooms,live_records,rollcalls";
    private boolean no_loading_animation = true;

    public ModuleActivitiesBean() {
        this.module_ids = new ArrayList<>();
    }

    public void clearModule_ids(){
        module_ids.clear();
    }

    @Override
    public String toString() {
        return "?module_ids=" + module_ids
                + "&activity_types=" + activity_types
                + "&no-loading-animation=" + no_loading_animation;
    }
}
