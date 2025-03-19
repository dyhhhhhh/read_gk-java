package com.dyhhhhh.bean.modules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 模块下的子模块
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModulesSyllabuses {
    private String id;
    private String module_id;
    private String summary;
    private String syllabus_id;
    public static ModulesSyllabuses fromMap(Map<String, Object> data) {
        ModulesSyllabuses modulesSyllabuses = new ModulesSyllabuses();
        modulesSyllabuses.id = String.valueOf(data.get("id"));
        modulesSyllabuses.module_id = String.valueOf(data.get("module_id"));
        modulesSyllabuses.summary = String.valueOf(data.get("summary"));
        modulesSyllabuses.syllabus_id = String.valueOf(data.get("syllabus_id"));
        return modulesSyllabuses;
    }
}

