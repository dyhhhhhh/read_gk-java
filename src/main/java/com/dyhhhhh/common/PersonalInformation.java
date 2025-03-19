package com.dyhhhhh.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

//用户信息
@Data
public class PersonalInformation {
    private String user_id;
    private String org_id;
    private final boolean is_teacher = false;
    private final boolean is_student = true;
    private String org_name;
    private String org_code;
    private String user_name;
    private String user_no;
    private String dep_code;
    private String dep_id;
    private String dep_name;
    // 只提供获取方法，不提供修改方法，确保线程安全
    //生产的cookie
    private final String cookie;
    //浏览器标识
    private final String[] browser_user_agent;

    public PersonalInformation(String cookie) {
        this.cookie = cookie;
        this.browser_user_agent = BrowserInfo.getRandomBrowserAndUserAgent();
    }
}
