package com.dyhhhhh.common.apis;

import com.alibaba.fastjson2.JSON;
import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.bean.PublishAndReplyBean;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 发表讨论及回复的具体执行
 */
@Component
public class ForumStrategy implements Strategy {

    @Autowired
    private CommonApisService commonApisService;

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random random = new Random();

    @Override
    public void execute(String activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig) {
        //检查是否完成
        if (commonApisService.is_full(activityId,new HashMap<String, Object>())) {
            return;
        }
        forum_user_visits();
        //forums请求
        send_forums(activityId,httpConfig);

        commonApisService.post_learning_activity(activityDetails.get("type").toString());
        //获取帖子id
        String topic_category_id = String.valueOf(activityDetails.get("topic_category_id"));
        //获取评论
        HashMap<String, Object> topic = getPublished(topic_category_id, 1, httpConfig);
        if (topic != null) {
            //标题
            String title = String.valueOf(topic.get("title"));
            //评论id
            String post_id = String.valueOf(topic.get("id"));
            //内容
            String content = String.valueOf(topic.get("content"));
            if (!content.isEmpty()) {
                //去发表和回复帖子
                if (publish_post(topic_category_id, title, content, httpConfig) && replies_post(post_id, content, httpConfig)) {
                    System.out.println("全部成功");
                } else {
                    System.out.println("失败");
                }
            }
        }
    }

    /**
     * 发表帖子
     *
     * @param topic_category_id:帖子id
     * @param title:标题
     * @param content:内容
     * @return
     */
    public boolean publish_post(String topic_category_id, String title, String content, RequestHttpConfig httpConfig) {
        System.out.println("开始发表帖子..");
        PublishAndReplyBean publishAndReplyBean = new PublishAndReplyBean();
        try {
            publishAndReplyBean.setTitle(title);
            publishAndReplyBean.setContent(content);
            publishAndReplyBean.setCategory_id(topic_category_id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //去发表
        String response = httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.Forum.TOPICS, "", publishAndReplyBean);
        if (!response.isEmpty()) {
            System.out.println("发表成功");
            return true;
        } else {
            System.out.println("发表失败");
            return false;
        }
    }

    /**
     * 回复帖子
     *
     * @param post_id:要回复的帖子id
     * @param content
     * @return
     */
    public boolean replies_post(String post_id, String content, RequestHttpConfig httpConfig) {
        System.out.println("开始回复帖子..");
        PublishAndReplyBean publishAndReplyBean = new PublishAndReplyBean();
        //防止帖子回复内容重复，生产随机字符串
        publishAndReplyBean.setContent(content + generate(5));
        String response = httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.Forum.REPLIES.formatted(post_id), "", publishAndReplyBean);
        if (!response.isEmpty()) {
            System.out.println("回复成功");
            return true;
        } else {
            System.out.println("回复失败");
            return false;
        }
    }

    public static String generate(int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length()));
        }
        return new String(text);
    }

    /**
     * 获取评论
     *
     * @param activityId:帖子id
     * @param page:页数
     * @return
     * @throws IOException
     */
    private HashMap<String, Object> getPublished(String activityId, int page, RequestHttpConfig httpConfig) {
        String text = httpConfig.get(ApiEndpoints.BASE_URL + ApiEndpoints.Forum.CATEGORIES.formatted(activityId, page), "");
        HashMap<String, Object> response = (HashMap<String, Object>) JSON.parse(text);
        //获取评论结果
        HashMap<String, Object> result = (HashMap<String, Object>) response.get("result");
        List<HashMap<String, Object>> topics = (List<HashMap<String, Object>>) result.get("topics");
        for (HashMap<String, Object> topic : topics) {
            String content = String.valueOf(topic.get("content"));
            if (verify_personal_information(content)) {
                return topic;
            }
        }
        //翻页递归是找合适的
        if (page > 5) {
            return null;
        }
        page = page + 1;
        return getPublished(activityId, page, httpConfig);
    }

    public boolean verify_personal_information(String content) {
        return !content.contains("姓名") || !content.contains("学号");
    }

    /**
     * 发送 forum 请求
     */
    private void send_forums(String activityId, RequestHttpConfig httpConfig) {
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
        param.put("action_type", "open");
        param.put("is_teacher", personalInformation.is_teacher());
        param.put("is_student", personalInformation.is_student());
        param.put("ts", System.currentTimeMillis());
        param.put("user_agent", personalInformation.getBrowser_user_agent()[1]);
        param.put("meeting_type", "forum");
        param.put("master_course_id", commonApisService.get_master_course_id(commonInfo.getCourse_id()));
        param.put("org_name", personalInformation.getOrg_name());
        param.put("org_code", personalInformation.getOrg_code());
        param.put("user_no", personalInformation.getUser_no());
        param.put("user_name", personalInformation.getUser_name());
        param.put("course_code", commonInfo.getCourse_code());
        param.put("course_name", commonInfo.getCourse_name());
        param.put("dep_id", personalInformation.getDep_id());
        param.put("dep_name", personalInformation.getDep_name());
        param.put("dep_code", personalInformation.getDep_code());

        // 调用 startPost 方法发送请求
        System.out.println(httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.Forum.FORUMS, "" ,param));
        System.out.println("已发送 forums");
    }

    //线程安全
    private static final ThreadLocal<HashMap<String, Object>> FORUM_PARAMS = ThreadLocal.withInitial(HashMap::new);

    /**
     * 发送forum_user_visits
     */
    private void forum_user_visits() {
        HashMap<String, Object> forum_extra_param = FORUM_PARAMS.get();
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();

        forum_extra_param.put("course_code",commonInfo.getCourse_code());
        forum_extra_param.put("course_id",commonInfo.getCourse_id());
        forum_extra_param.put("course_name",commonInfo.getCourse_name());
        commonApisService.send_user_visits(forum_extra_param);
    }
}

