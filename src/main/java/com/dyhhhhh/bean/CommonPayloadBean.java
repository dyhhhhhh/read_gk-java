package com.dyhhhhh.bean;

import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.ThreadLocalHolder;
import lombok.Data;

/**
 * 公共参数
 */
@Data
public class CommonPayloadBean {
    //个人信息
    public CommonPayloadBean() {
       PersonalInformation personalInformation = ThreadLocalHolder.getPersonalInformation();
        this.user_id = personalInformation.getUser_id();
        this.org_id = personalInformation.getOrg_id();
        this.user_agent = personalInformation.getBrowser_user_agent()[1];
        this.is_teacher = personalInformation.is_teacher();
        this.is_student = personalInformation.is_student();
        this.org_name = personalInformation.getOrg_name();
        this.org_code = personalInformation.getOrg_code();
        this.user_name = personalInformation.getUser_name();
        this.user_no = personalInformation.getUser_no();
        this.dep_code = personalInformation.getDep_code();
        this.dep_id = personalInformation.getDep_id();
        this.dep_name = personalInformation.getDep_name();
    }

    public String user_id;
    public Long org_id;
    public String user_agent;
    public boolean is_teacher;
    public boolean is_student;
    public String org_name;
    public String org_code;
    public String user_name;
    public String user_no;
    public String dep_code;
    public String dep_id;
    public String dep_name;

}
