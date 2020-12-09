package com.foreveross.atwork.api.sdk;

/**
 * Created by lingen on 15/4/30.
 * Description:
 */
public interface NetWorkFailListener {

    /**
     * 操作失败
     *
     * @param errorCode
     * @param errorMsg
     */
    void networkFail(int errorCode, String errorMsg);
}
