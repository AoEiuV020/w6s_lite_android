package com.foreveross.atwork.infrastructure.newmessage.post.chat;

/**
 * Created by lingen on 15/4/29.
 * Description:
 * 文件发送状态
 */
public enum FileStatus {

    //已取消
    SEND_CANCEL,

    DOWNLOAD_CANCEL,

    //未下载
    NOT_DOWNLOAD,

    //下载中
    DOWNLOADING,

    //下载失败
    DOWNLOAD_FAIL,

    //已下载
    DOWNLOADED,

    //未发送
    NOT_SENT,

    //发送中
    SENDING,

    //已发送成功
    SENDED,

    //发送失败
    SEND_FAIL,

    //暂停
    PAUSE,

    //失效
    OVERDUE;

    public static FileStatus fromString(String type) {
        if ("SEND_CANCEL".equalsIgnoreCase(type)) {
            return SEND_CANCEL;
        }
        if ("DOWNLOAD_CANCEL".equalsIgnoreCase(type)) {
            return DOWNLOAD_CANCEL;
        }
        if ("NOT_DOWNLOAD".equalsIgnoreCase(type)) {
            return NOT_DOWNLOAD;
        }
        if ("DOWNLOADING".equalsIgnoreCase(type)) {
            return DOWNLOADING;
        }
        if ("DOWNLOAD_FAIL".equalsIgnoreCase(type)) {
            return DOWNLOAD_FAIL;
        }
        if ("DOWNLOADED".equalsIgnoreCase(type)) {
            return DOWNLOADED;
        }
        if ("NOT_SENT".equalsIgnoreCase(type)) {
            return NOT_SENT;
        }
        if ("SENDED".equalsIgnoreCase(type)) {
            return SENDED;
        }
        if ("PAUSE".equalsIgnoreCase(type)) {
            return PAUSE;
        }
        return NOT_DOWNLOAD;
    }

}
