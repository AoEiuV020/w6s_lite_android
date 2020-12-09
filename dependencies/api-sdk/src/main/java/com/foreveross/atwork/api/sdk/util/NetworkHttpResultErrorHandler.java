package com.foreveross.atwork.api.sdk.util;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpResultException;
import com.foreveross.atwork.infrastructure.utils.StringUtils;


public class NetworkHttpResultErrorHandler {



    public static HttpResultException toException(HttpResult httpResult) {

        int errorCode;
        String errorMsg;
        if (httpResult.isNetError()) {
            errorCode = httpResult.status;
            errorMsg = httpResult.error;

        } else if (httpResult.isNetFail()) {
            errorCode = httpResult.statusCode;
            errorMsg = StringUtils.EMPTY;

        } else {
            BasicResponseJSON responseJSON = httpResult.resultResponse;
            if(null != responseJSON) {

                errorCode = httpResult.status;
                errorMsg = httpResult. error;

            } else {
                //解析错误
                errorCode = -3;
                errorMsg = StringUtils.EMPTY;
            }

        }


        return new HttpResultException(errorCode, errorMsg, httpResult);
    }


    /**
     * 处理错误 的HTTP ERROR
     *
     * @param httpResult
     * @param netWorkFailListener
     */
    public static void handleHttpError(HttpResult httpResult, NetWorkFailListener netWorkFailListener) {
        if(null != httpResult && null != netWorkFailListener) {
            if (httpResult.isNetError()) {
                netWorkFailListener.networkFail(httpResult.status, httpResult.error);

            } else if (httpResult.isNetFail()) {
                netWorkFailListener.networkFail(httpResult.statusCode, StringUtils.EMPTY);

            } else {
                BasicResponseJSON responseJSON = httpResult.resultResponse;
                if(null != responseJSON) {
                    netWorkFailListener.networkFail(responseJSON.status, responseJSON.message);

                } else {
                    //解析错误
                    netWorkFailListener.networkFail(-3, StringUtils.EMPTY);
                }

            }

        }
    }


}
