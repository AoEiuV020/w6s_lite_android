package com.foreveross.atwork.infrastructure.model.organizationSetting;

/**
 * Created by dasunsy on 2017/11/20.
 */

public enum VpnCredentialType {
    /**
     * 固定帐号密码
     * */
    GLOBAL,

    /**
     * 用户单独帐号密码（由用户在移动端填写）
     * */
    OTHER,

    /**
     * 使用用户登录的帐号密码
     * */
    USER
}
