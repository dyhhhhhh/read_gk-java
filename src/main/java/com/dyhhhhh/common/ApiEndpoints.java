package com.dyhhhhh.common;

/**
 * api集中管理
 */
public final class ApiEndpoints {
    public static final String BASE_URL = "https://lms.ouchn.cn";
    public static class User {
        public static final String INDEX = "/user/index";
        public static final String VISITS = "/statistics/api/user-visits";
        public static final String pages = "/statistics/api/pages";
    }

    public static class Course {
        public static final String MY_COURSES = "/api/my-courses";
        public static final String MODULES = "/api/courses/%s/modules";
        public static final String ACTIVITIES = "/api/course/%s/all-activities";
        public static final String ACTIVITIES_READ = "/api/course/activities-read/%s";
        public static final String FULL_SCREEN = "/course/%s/learning-activity/full-screen";
        public static final String MY_COMPLETENESS = "/api/course/%s/my-completeness";

    }
    public static class Activity{
        public static final String ACTIVITIES = "/api/activities/%s";
       public static final String LEARNING_ACTIVITY = "/statistics/api/learning-activity";
    }
    public static class Video{
        public static final String ONLINE_VIDEOS = "/statistics/api/online-videos";
    }
    public static class Forum{
        public static final String TOPICS = "/api/topics";
        public static final String REPLIES = "/api/topics/%S/replies";
        public static final String CATEGORIES = "/api/forum/categories/%s?conditions=%%7B%%7D&fields=id,title,created_by(id,name,nickname,comment,avatar_big_url,user_no),group_id,created_at,updated_at,content,read_replies(reply_id),reply_count,unread_reply_count,like_count,current_user_read,current_user_liked,in_common_category,user_role,has_matched_replies,uploads,user_role&page=%d";
        public static final String FORUMS = "/statistics/api/forums";
    }
    public static class Materials{
        public static final String MATERIALS = "/statistics/api/materials";
    }
}
