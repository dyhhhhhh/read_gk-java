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
@Service
public class UserService {
    @Autowired
    private RequestHttpConfig httpConfig;
    @Autowired
    private CommonApisService commonApisService;

    /**
     * 用户初始化
     * @param =
     */
    public void init(PersonalInformation info) {
        Map<String, HashMap<String, String>> userInfo = get_user_info();
        //组装个人参数
        HashMap<String, String> user = userInfo.get("user");
        HashMap<String, String> dept = userInfo.get("dept");
        info.setUser_id(String.valueOf(user.get("id")));
        info.setUser_name(user.get("name"));
        info.setUser_no(user.get("userNo"));
        info.setOrg_id(String.valueOf(user.get("orgId")));
        info.setOrg_name(user.get("orgName"));
        info.setOrg_code(user.get("orgCode"));
        info.setDep_id(dept.get("id"));
        info.setDep_name(dept.get("name"));
        info.setDep_code(dept.get("code"));
        //用户登录后访问首页发送该请求
        commonApisService.send_user_visits(new HashMap<>());
    }

    public String getCookie(AccountDTO accountDTO){
        //处理cookie   2413001200455
        return "HWWAFSESTIME=1742270091161; HWWAFSESID=036b8b8b415983fd87; Hm_lvt_c3f009f814f701e8fad8a17f9682ec79=1740965751,1741332230,1741845078,1742270094; HMACCOUNT=28B2E602A3869955; session=V2-30000000001-a9c2e001-646d-4f3a-aa39-b24acf95ea89.MzAwMDEwMTE2MjY.1742356506398.Rk78-OHVp0CekBRw-6HyERGrvys; Hm_lpvt_c3f009f814f701e8fad8a17f9682ec79=1742270107";
    }

    /**
     * 解析html获取用户信息
     */
    private Map<String, HashMap<String, String>> get_user_info() {
        HashMap<String, HashMap<String, String>> stringStringHashMap;
        String html = httpConfig.get(ApiEndpoints.BASE_URL +ApiEndpoints.User.INDEX);
        Document document = Jsoup.parse(html);
        Element element = document.selectFirst("script:containsData(var globalData)");
        if (element == null) {
            throw new RuntimeException("未找到包含globalData的script标签");
        }
        String scriptContent = element.html();
        int startIndex = scriptContent.indexOf("var globalData = {");
        if (startIndex == -1) {
            throw new RuntimeException("未找到globalData定义");
        }
        startIndex = scriptContent.indexOf('{', startIndex);
        int endIndex = findMatchingBrace(scriptContent, startIndex);
        // 4. 转换JS对象为JSON格式
        String json = scriptContent.substring(startIndex, endIndex + 1)
                .replaceAll("([{,])\\s*([a-zA-Z0-9_]+)\\s*:", "$1\"$2\":")  // 给key加引号
                .replaceAll("'", "\"")  // 处理单引号
                .replaceAll("/\\*.*?\\*/", "")  // 移除注释
                .replaceAll(",\\s*(}|])", "$1"); // 移除尾逗号
        stringStringHashMap = (HashMap<String, HashMap<String, String>>) JSON.parse(json);
        System.out.println(stringStringHashMap);
        return stringStringHashMap;
    }

    // 括号匹配方法
    private static int findMatchingBrace(String str, int start) {
        int braceCount = 1;
        for (int i = start + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '{') braceCount++;
            else if (c == '}') braceCount--;

            if (braceCount == 0) {
                return i;
            }
        }
        throw new RuntimeException("大括号不匹配");
    }
}
