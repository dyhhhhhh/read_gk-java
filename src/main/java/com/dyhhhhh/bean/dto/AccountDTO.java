package com.dyhhhhh.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * 用户的账号密码
 */
public class AccountDTO {
    private String nickname;
    private String username;
    private String password;
    private String cookie;
}
