package com.dyhhhhh.common.service;

import com.dyhhhhh.bean.ModuleActivitiesBean;
import com.dyhhhhh.bean.activities.AllActivities;


public interface ActivitiesService {

    /**
     * 获取模块下面所有的活动
     */
    AllActivities getModuleActivities(String courses_id, ModuleActivitiesBean moduleActivitiesBean);

}
