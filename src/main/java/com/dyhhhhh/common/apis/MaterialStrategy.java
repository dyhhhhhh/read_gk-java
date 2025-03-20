package com.dyhhhhh.common.apis;

import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 打开文件的具体执行
 */
@Component
public class MaterialStrategy implements Strategy{

    @Autowired
    private CommonApisService commonApisService;

    //线程安全，用来组装额外参数
    private static final ThreadLocal<HashMap<String, Object>> learning_activity_map = ThreadLocal.withInitial(HashMap::new);

    @Override
    public void execute(String activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig){
        //找到里面的文件
        List<HashMap<String,Object>> uploads = (List<HashMap<String, Object>>) activityDetails.get("uploads");
        //循环阅读文件
        for (HashMap<String, Object> upload : uploads) {
            try {
                //阅读每个文件需要延迟
                Thread.sleep(5 + new Random().nextInt(5));
            }catch (InterruptedException e){
                System.out.println(e.getMessage());
            }

            //获取每个文件后缀
            String suffix = String.valueOf(upload.get("name")).split("\\.")[1];
            //文件id
            String sub_id = String.valueOf(upload.get("id"));
            HashMap<String, Object> extra_param = learning_activity_map.get();
            extra_param.put("sub_id",sub_id);
            extra_param.put("sub_type",suffix);
            //发送学习中
            commonApisService.post_learning_activity(activityDetails.get("type").toString(), extra_param);
            System.out.println("阅读资料:"+upload.get("name"));
            //判断是否完成
            HashMap<String, String> getCompleteness = new HashMap<>();
            getCompleteness.put("upload_id", sub_id);
            HashMap<String, Object> result = commonApisService.activities_state(activityId, getCompleteness);
            if (result.get("completeness").equals("full")) {
                System.out.println("阅读完毕");
                break;
            }
        }
    }
}
