package com.dyhhhhh.bean.modules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseModules {
    private String id;
    private String name;

    private List<ModulesSyllabuses> modulesSyllabuses;

    /**
     * 转换map
     * @param data
     * @return
     */
    public static CourseModules fromMap(Map<String, Object> data) {
        ArrayList<ModulesSyllabuses> list = new ArrayList<>();
        CourseModules courseModules = new CourseModules();
        courseModules.id = String.valueOf(data.get("id"));
        courseModules.name = String.valueOf(data.get("name"));
        //解析字模块
        List<HashMap<String,Object>> syllabuses = (List<HashMap<String,Object>>) data.get("syllabuses");
        for (HashMap<String, Object> syllabus : syllabuses) {
            list.add(ModulesSyllabuses.fromMap(syllabus));
        }
        courseModules.modulesSyllabuses = list;
        return courseModules;
    }

}
