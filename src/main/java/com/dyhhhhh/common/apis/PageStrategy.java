package com.dyhhhhh.common.apis;

import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 浏览页面的具体执行
 */
@Component
public class PageStrategy implements Strategy{

    @Autowired
    private CommonApisService commonApisService;

    @Override
    public void execute(Long activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig) {
        //请求
        page_user_visits(activityDetails);
        //模拟点进页面发送这个请求
        send_pages(activityId,httpConfig);

        commonApisService.post_learning_activity(activityDetails.get("type").toString());
        //获取详情信息
        String title = activityDetails.get("title").toString();
        //检查是否观看完毕
        System.out.println("开始阅读---" + title);
        HashMap<String, Object> stringObjectHashMap = commonApisService.activities_state(activityId, new HashMap<String, Object>());

        if (!String.valueOf(stringObjectHashMap.get("completeness")).equals("full")){
            System.out.println("阅读失败--->" + title);
        }else {
            System.out.println("阅读成功");
        }
    }
    /**
     * 发送page请求
     */
    private void send_pages(Long activityId,RequestHttpConfig httpConfig){
        PersonalInformation personalInformation = ThreadLocalHolder.getPersonalInformation();
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        // 组装请求参数
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", Long.valueOf(personalInformation.getUser_id()));
        param.put("org_id", personalInformation.getOrg_id());
        param.put("course_id", commonInfo.getCourse_id());
        param.put("module_id", commonInfo.getModule_id());
        param.put("is_final", null);
        param.put("is_formative", null);
        param.put("activity_id", activityId);
        param.put("reply_id", null);
        param.put("comment_id", null);
        param.put("forum_type", "");
        param.put("action_type", "open");
        param.put("is_teacher", personalInformation.is_teacher());
        param.put("is_student", personalInformation.is_student());
        param.put("ts", System.currentTimeMillis());
        param.put("user_agent", personalInformation.getBrowser_user_agent()[1]);
        param.put("meeting_type", "page");
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
        System.out.println(httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.User.pages, "", param));
        System.out.println("已发送pages");
    }
    //线程安全
    private static final ThreadLocal<HashMap<String, Object>> PAGE_PARAMS = ThreadLocal.withInitial(HashMap::new);
    /**
     * 点击之前 user_visits
     */
    private void page_user_visits(HashMap<String, Object> activityDetails) {
        HashMap<String, Object> page_extra_param = PAGE_PARAMS.get();
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();

        page_extra_param.put("course_code",commonInfo.getCourse_code());
        page_extra_param.put("course_id",commonInfo.getCourse_id());
        page_extra_param.put("course_name",commonInfo.getCourse_name());
        page_extra_param.put("activity_id",activityDetails.get("id"));
        page_extra_param.put("activity_type",activityDetails.get("type"));
        commonApisService.send_user_visits(page_extra_param);
    }

}
