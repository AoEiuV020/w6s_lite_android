package com.foreveross.atwork.infrastructure.model.log.behavior;

/**
 * Created by dasunsy on 2018/3/12.
 */

public enum Type {


    /**
     * 点击类型
     * */
    CLICK,

    /**
     * 访问类型
     * */
    VISIT,


    /**
     * 点击应用
     * */
    APP_CLICK,

    /**
     * 访问应用(从进入 app 到 退出 app)
     * */
    APP_VISIT,


    /**
     * 访问客户端(从可见到不可见)
     *
     */
    CLIENT_VISIT,


    /**
     * 点击客户端(一天一次)
     * */
    CLIENT_CLICK,


    /**
     * 访问 url (摇一摇触发)
     * */
    SHAKE_URL_VISIT,


    /**
     * TAB 点击
     * */
    TAB_CLICK,


    /**
     * TAB 访问时长(从可见到不可见)
     * */
    TAB_VISIT,

    /**
     * email 点击
     * */
    EMAIL_CLICK,


    /**
     * email 访问时长(从可见到不可见)
     * */
    EMAIL_VISIT,


    /**
     * 同事圈点击
     * */
    MOMENTS_CLICK,


    /**
     * 同事圈访问时长(从可见到不可见)
     * */
    MOMENTS_VISIT,


    /**
     * 本地日志, 是否打开过邮箱（未登陆的不算)
     * */
    LOCAL_EMAIL_LOGIN_CLICK,

    /**
     * 本地日志, 是否有使用邮箱发送过邮件
     * */
    LOCAL_EMAIL_WRITE


}
