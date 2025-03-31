package com.dyhhhhh.common.apis;

import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.bean.activities.LearningActivities;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.common.enums.ActivityType;
import com.dyhhhhh.config.RequestHttpConfig;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

//上下文类，用于执行活动类型的方法
@Setter
@Component
public class Context {
    @Autowired
    private RequestHttpConfig httpConfig;
    @Autowired
    private CommonApisService commonApisService;
    @Autowired
    private StrategyFactory strategyFactory;

    public void executeStrategy(LearningActivities learningActivities) {
        //可能存在需要考完试才继续的课程，这里做无限暂停直到做完考试才继续
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();

        while (true){
            HashMap<String, Object> details = commonApisService.getActivityDetails(learningActivities.getId());
            try {
                if (!details.containsKey("type")) {

                    String message = String.valueOf(details.get("message"));
                    if ("该学习活动尚未解锁".equals(message)) {
                        Thread.sleep(30);
                        System.err.println(message+",请手动完成答题以继续->" + commonInfo.getCourse_name() + "->" +
                                commonInfo.getModule_name() + "->" + commonInfo.getActivity_title());
                    }else if ("您没有权限完成此操作".equals(message)){
                        System.err.println(message + "->" + commonInfo.getCourse_name() + "->" +
                                commonInfo.getModule_name() + "->" + commonInfo.getActivity_title() + "->>跳过");
                        return;
                    }
                }else {
                    // 正常执行策略
                    ActivityType type = ActivityType.fromTypeName(learningActivities.getType());
                    Strategy strategy = strategyFactory.getStrategy(type);
                    strategy.execute(Long.valueOf(learningActivities.getId()), details, httpConfig);
                    // 成功执行则退出循环
                    break;
                }
            }catch (Exception e) {
                System.err.println(e);
            }

        }
    }
}
