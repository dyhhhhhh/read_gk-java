package com.dyhhhhh.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor

/**
 * 查询所有课程的对象
 */
public class MyCoursesBean {
    //查询课程的参数
    //已结束
    private static final String closed = "%7B%22status%22:%5B%22closed%22%5D,%22keyword%22:%22%22%7D";
    //进心中
    private static final String ongoing = "%7B%22status%22:%5B%22ongoing%22%5D,%22keyword%22:%22%22%7D";
    //要查询的字段
    private static final String fieIds = "id,name,course_code,department";
    //查询页码
    private int page = 1;
    //每页大小
    private int size = 100;

    //查询已结束
    public String closed_toString() {
        return "?conditions="+closed+"&"
                +"fields="+fieIds+"&"
                +"page="+page+"&"
                +"page_size="+size;
    }
    //查询进行中
    public String ongoing_toString() {
        return "?conditions="+ongoing+"&"
                +"fields="+fieIds+"&"
                +"page="+page+"&"
                +"page_size="+size;
    }

}
