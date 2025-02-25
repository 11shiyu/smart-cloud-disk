package com.yupi.yupicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

//请求封装类 用户注册请求对象
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 9129174387846253664L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
