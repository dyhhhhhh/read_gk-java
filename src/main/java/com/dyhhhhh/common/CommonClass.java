package com.dyhhhhh.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 公共同一对象模型
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommonClass<T extends CommonClass<T>>{
    private String id;
    private String title;
    private String type;
    public abstract T fromMap(Map<String, Object> data);
}
