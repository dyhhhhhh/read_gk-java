package com.dyhhhhh.common.executor;

import com.dyhhhhh.common.apis.Context;
import com.dyhhhhh.bean.activities.LearningActivities;
import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.common.ThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * 活动执行器
 */
@Component
public class ActivityExecutor {
    //具体执行课程的上下文
    @Autowired
    private Context context;

    public void executeLearningActivities(List<LearningActivities> activities) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        activities.forEach(activity -> {
            System.out.println("当前执行活动 -> " + commonInfo.getCourse_name() +
                    "->" + commonInfo.getModule_name() +
                    "->" + activity.getTitle());
            commonInfo.setActivity_title(activity.getTitle());
            HashMap<String, Boolean> map = commonInfo.getLearning_activity();
            //查找当前活动是否已经在完结容器中
            if (map.get(activity.getId()) != null){
                System.out.println("已跳过");
                return;
            }
            context.executeStrategy(activity);
        });
    }
}
