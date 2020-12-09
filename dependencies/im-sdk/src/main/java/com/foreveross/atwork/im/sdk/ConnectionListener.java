package com.foreveross.atwork.im.sdk;

/**
 * Created by lingen on 15/4/7.
 * Connection监听，用于监听连接建立，退出，中断及超时等
 */
public interface ConnectionListener {

    /**
     * 连接失败
     *
     * @param errMsg
     */
    void fail(String errMsg);


    /**
     * 连接超时
     */
    void timeout();

    /**
     * 连接成功
     */
    void success();

    /**
     * 连接中断
     */
    void broken();

    /**
     * Socket连接退出
     */
    void exit();


}
