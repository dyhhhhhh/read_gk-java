package com.dyhhhhh.common.apis;

import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void execute(Long activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig){
        //找到里面的文件
        List<HashMap<String,Object>> uploads = (List<HashMap<String, Object>>) activityDetails.get("uploads");

        send_materials(activityId,httpConfig,null);
        commonApisService.activities_state(activityId, new HashMap<String, Object>());
        String syllabus_id = String.valueOf(activityDetails.get("syllabus_id"));
        //循环阅读文件
        for (HashMap<String, Object> upload : uploads) {
            //获取每个文件后缀
            String[] names = String.valueOf(upload.get("name")).split("\\.");
            String suffix = names[names.length - 1];
            //文件id
            Long sub_id = Long.valueOf(String.valueOf(upload.get("id")));
            HashMap<String, Object> extra_param = learning_activity_map.get();
            extra_param.put("sub_id",sub_id);
            extra_param.put("sub_type",suffix);
            //发送学习中
            commonApisService.post_learning_activity(activityDetails.get("type").toString(), extra_param);
            System.out.println("阅读资料:"+upload.get("name"));
            //判断是否完成
            HashMap<String, Object> getCompleteness = new HashMap<>();
            getCompleteness.put("upload_id", sub_id);
            HashMap<String, Object> result = commonApisService.activities_state(activityId, getCompleteness);
            //发送该文件的 send_materials,每个文件都要发送
            HashMap<String, Object> send_materials_extra_param = new HashMap<>();
            send_materials_extra_param.put("upload_id",sub_id);
            send_materials_extra_param.put("syllabus_id",syllabus_id);
            send_materials(activityId,httpConfig,send_materials_extra_param);

            if (result.get("completeness").equals("full")) {
                System.out.println("阅读完毕");
                break;
            }
        }
    }

    private void send_materials(Long activityId,RequestHttpConfig httpConfig,HashMap<String,Object> extra_param) {

        PersonalInformation personalInformation = ThreadLocalHolder.getPersonalInformation();
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        // 组装请求参数
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", personalInformation.getUser_id());
        param.put("org_id", personalInformation.getOrg_id());
        param.put("course_id", commonInfo.getCourse_id());
        param.put("module_id", commonInfo.getModule_id());
        param.put("is_final", null);
        param.put("is_formative", null);
        param.put("activity_id", activityId);
        param.put("reply_id", null);
        param.put("comment_id", null);
        param.put("forum_type", "");
        param.put("action_type", "view");
        param.put("is_teacher", personalInformation.is_teacher());
        param.put("is_student", personalInformation.is_student());
        param.put("ts", System.currentTimeMillis());
        param.put("user_agent", personalInformation.getBrowser_user_agent()[1]);
        param.put("meeting_type", "material");
        param.put("master_course_id", commonInfo.getMaster_course_id());
        param.put("org_name", personalInformation.getOrg_name());
        param.put("org_code", personalInformation.getOrg_code());
        param.put("user_no", personalInformation.getUser_no());
        param.put("user_name", personalInformation.getUser_name());
        param.put("course_code", commonInfo.getCourse_code());
        param.put("course_name", commonInfo.getCourse_name());
        param.put("dep_id", personalInformation.getDep_id());
        param.put("dep_name", personalInformation.getDep_name());
        param.put("dep_code", personalInformation.getDep_code());
        if (extra_param != null) {
            param.putAll(extra_param);
        }
        System.out.println(httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.Materials.MATERIALS, "", param));
        System.out.println("已发送materials");
    }
}
