package com.dyhhhhh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlineVideoBean extends CommonPayloadBean {
    private Long module_id;
    private Long syllabus_id;
    private Long activity_id;
    private String reply_id = null;
    private String comment_id = null;
    private String forum_type = "";
    private String action_type;
    private Long ts = System.currentTimeMillis();
    private String meeting_type = "online_video";
    private int start_at;
    private int end_at;
    private int duration;
    private String course_code;
    private String course_id;
    private String course_name;
    private String is_final;
    private String is_formative;
    private Long master_course_id;
    private Long upload_id;
    public OnlineVideoBean() {
        super();
    }
}
