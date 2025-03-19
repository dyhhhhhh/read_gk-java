package com.dyhhhhh.common.service;

import com.alibaba.fastjson2.JSON;
import com.dyhhhhh.bean.dto.AccountDTO;
import com.dyhhhhh.common.ApiEndpoints;
import com.dyhhhhh.common.PersonalInformation;
import com.dyhhhhh.common.service.impl.CommonApisService;
import com.dyhhhhh.config.RequestHttpConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类
 */

public interface UserService {

    /**
     * 用户初始化
     * @param =
     */
    void init(PersonalInformation info);

    String getCookie(AccountDTO accountDTO);

}
