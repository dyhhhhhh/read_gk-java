package com.dyhhhhh.common.apis;

import com.dyhhhhh.bean.CommonInfo;
import com.dyhhhhh.bean.OnlineVideoBean;
import com.dyhhhhh.bean.ReadVideoBean;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.common.ThreadLocalHolder;
import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 在线观看视频的具体执行
 */
@Component
public class OnlineVideoStrategy implements Strategy{

    @Autowired
    private CommonApisService commonApisService;

    //线程安全
    private static final ThreadLocal<HashMap<String, Object>> mapThreadLocal = ThreadLocal.withInitial(HashMap::new);

    @Override
    public void execute(Long activityId, HashMap<String, Object> activityDetails,RequestHttpConfig httpConfig) {
        ReadVideoBean readVideoBean = new ReadVideoBean();

        //检查是否已经完成观看
        HashMap<String, Object> response = commonApisService.activities_state(activityId, new HashMap<String, Object>());

        readVideoBean.setStart(0);
        readVideoBean.setEnd(61);
        String completeness = response.get("completeness").toString();
        if ("full".equals(completeness)){
            System.out.println("跳过");
            return;
        }

        video_user_visits();
        //获取视频时长
        List<HashMap<String,Object>> uploads = (List<HashMap<String, Object>>) activityDetails.get("uploads");
        HashMap<String, Object> extra_param = mapThreadLocal.get();

        //发送learning-activity，每个视频只发送一次
        commonApisService.post_learning_activity(activityDetails.get("type").toString(),extra_param);
        //遍历所有文件
        for (HashMap<String, Object> upload : uploads) {
            Long upload_id = Long.valueOf(upload.get("id").toString());
            //获取每个文件后缀
            String suffix = String.valueOf(upload.get("name")).split("\\.")[1];
            extra_param.put("sub_id", upload_id);
            extra_param.put("sub_type",suffix);
            List<HashMap<String,Object>> videos = (List<HashMap<String, Object>>) upload.get("videos");
            System.out.println("开始观看视频->" + upload.get("name"));

            if (!videos.isEmpty()){
                HashMap<String, Object> video = videos.get(0);
                int max_duration = (int) Double.parseDouble(video.get("duration").toString());

                //获取module_id
                Long module_id = Long.valueOf(String.valueOf(activityDetails.get("module_id")));
                Long syllabus_id = Long.valueOf(String.valueOf(activityDetails.get("syllabus_id")));
                //模拟点进去观看，这一次只记录观看次数+1
                do_online_video(activityId,module_id,syllabus_id,0,0,"view",httpConfig, upload_id);

                //继续观看，从上次一次观看的最大时长开始往后观看
                HashMap<String,Object> data = (HashMap<String, Object>) response.get("data");

                try {
                    List<List<Integer>>  ranges = (List<List<Integer>>) data.get("ranges");
                    for (List<Integer> range : ranges) {
                        for (Integer r : range) {
                            readVideoBean.setEnd(Math.max(readVideoBean.getEnd(),r));
                        }
                    }
                }catch (NullPointerException e){
                    readVideoBean.setEnd(readVideoBean.getEnd());
                }
                //递归观看完视频
                recursion_watch_video(max_duration,activityId,readVideoBean.getEnd(),httpConfig,activityDetails,readVideoBean, upload_id);
            }else {
                System.out.println("未找到视频数据");
            }
        }
    }
    /**
     * 递归观看直到80%
     * @param duration
     * @param video_activity_id
     * @param end
     */
    public void recursion_watch_video(int duration,
                                      Long video_activity_id,
                                      int end,
                                      RequestHttpConfig httpConfig,
                                      HashMap<String, Object> activityDetails,
                                      ReadVideoBean readVideoBean,
                                      Long upload_id) {
        try {
            //判断是否观看到80%
            if(end < duration * 0.8){
                System.out.println("继续观看");
                int rand = 1 + new Random().nextInt(5);
                int start = end;
                if(duration - end + 60 + rand > 60){
                    end = end + 60 + rand;
                }else {
                    end = end + duration - end;
                }
                readVideoBean.setStart(start);
                readVideoBean.setEnd(end);
                //记录
                commonApisService.activities_state(video_activity_id,readVideoBean);

                //发送正在观看视频进度
                do_online_video(video_activity_id,
                        Long.valueOf(String.valueOf(activityDetails.get("module_id"))),
                        Long.valueOf(activityDetails.get("syllabus_id").toString()),
                        start,
                        end,
                        "play",
                        httpConfig,upload_id);

                //发送观看时长
                ing_param(activityDetails);
                video_user_visits();
                //递归观看
                recursion_watch_video(duration,video_activity_id,end,httpConfig,activityDetails,readVideoBean,upload_id);
            }else {
                System.out.println("观看完毕");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    /**
     * 发送观看次数及记录
     * @param activity_id
     * @param module_id
     * @param syllabus_id
     * @param start
     * @param end
     * @param action_type
     * @throws Exception
     */
    private static void do_online_video(Long activity_id,
                                        Long module_id,
                                        Long syllabus_id,
                                        int start,
                                        int end,
                                        String action_type,
                                        RequestHttpConfig httpConfig,
                                        Long upload_id) {
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        try {
            OnlineVideoBean onlineVideoBean = new OnlineVideoBean();
            onlineVideoBean.setModule_id(module_id);
            onlineVideoBean.setSyllabus_id(syllabus_id);
            onlineVideoBean.setActivity_id(activity_id);
            onlineVideoBean.setAction_type(action_type);
            onlineVideoBean.setCourse_code(commonInfo.getCourse_code());
            onlineVideoBean.setCourse_id(commonInfo.getCourse_id());
            onlineVideoBean.setCourse_name(commonInfo.getCourse_name());
            onlineVideoBean.setIs_final(null);
            onlineVideoBean.setIs_formative(null);
            onlineVideoBean.setMaster_course_id(commonInfo.getMaster_course_id());
            onlineVideoBean.setUpload_id(upload_id);
            if (start != 0 && end != 0){
                onlineVideoBean.setStart_at(start);
                onlineVideoBean.setEnd_at(end);
                onlineVideoBean.setDuration(end - start);
            }

            //发送请求
            httpConfig.post(ApiEndpoints.BASE_URL + ApiEndpoints.Video.ONLINE_VIDEOS,onlineVideoBean);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    //线程安全
    private static final ThreadLocal<HashMap<String, Object>> VIDEO_PARAMS = ThreadLocal.withInitial(HashMap::new);
    /**
     * 点击视频之前 user_visits
     */
    private void video_user_visits() {
        HashMap<String, Object> video_extra_param = VIDEO_PARAMS.get();
        CommonInfo commonInfo = ThreadLocalHolder.currentCommonInfo();
        video_extra_param.put("course_code",commonInfo.getCourse_code());
        video_extra_param.put("course_id",commonInfo.getCourse_id());
        video_extra_param.put("course_name",commonInfo.getCourse_name());
        commonApisService.send_user_visits(video_extra_param);
    }
    /**
     * 观看视频中参数
     */
    public static void ing_param(HashMap<String, Object> activityDetails) {
        HashMap<String, Object> video_extra_param = VIDEO_PARAMS.get();
        video_extra_param.put("activity_id",activityDetails.get("id"));
        video_extra_param.put("activity_type",activityDetails.get("type"));
        video_extra_param.put("auto_interval",true);
    }
}
