package com.foreveross.atwork.api.sdk.net;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.net.HttpURLConnection;

public class HttpResult {

    /**
     * 返回结果正常
     */
    public static final int RESULT_OK = 0;

    /**
     * 返回结果，非200
     */
    public static final int RESULT_NOT_200 = -2;

    /**
     * 异常
     */
    public static final int RESULT_EXCEPTION = -1;

    public static final String EXCEPTION_TCP_BROKEN = "sendto failed: EPIPE (Broken pipe)";

    public static final String EXCEPTION_EOF = "EXCEPTION_EOF";

    public static final String EXCEPTION_TIME_OUT= "SocketTimeoutException";


    public static final String EXCEPTION_NETWORK_IS_UNREACHABLE = "Network is unreachable";

    public int status;

    public int statusCode;

    public String result;

    public BasicResponseJSON resultResponse;

    public String error = "";

    public byte[] byteResult;


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpResult{");
        sb.append("status=").append(status);

        sb.append(", statusCode=").append(statusCode);
        if (!StringUtils.isEmpty(result)) {
            sb.append(", result='").append(result).append('\'');
        }
        if (!StringUtils.isEmpty(error)) {
            sb.append(", error='").append(error).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }

    public static HttpResult getInstance() {
        return new HttpResult();
    }

    public HttpResult netStatsOK() {
        this.status = RESULT_OK;
        this.statusCode = HttpURLConnection.HTTP_OK;
        return this;
    }

    public HttpResult netStatusNot200(int status) {
        this.status = RESULT_NOT_200;
        this.statusCode = status;
        return this;
    }

    public HttpResult netException(String error) {
        this.status = RESULT_EXCEPTION;
        this.error = error;
        return this;
    }


    public HttpResult result(String result) {
        this.result = result;
        return this;
    }

    public HttpResult result(BasicResponseJSON basicResponseJSON) {
        this.resultResponse = basicResponseJSON;
        return this;
    }

    public HttpResult byteResult(byte[] bytes){
        this.byteResult = bytes;
        return this;
    }

    public HttpResult result(BasicResponseJSON basicResponseJSON, int statusCode) {
        this.resultResponse = basicResponseJSON;
        if (this.resultResponse != null) {
            this.status = resultResponse.status;
        }
        this.statusCode = statusCode;
        this.status = statusCode;
        return this;
    }

    /**
     * 网络请求过程中出现异常
     * */
    public boolean isNetError() {
        return RESULT_EXCEPTION == status;
    }

    /**
     * 网络请求非200相应
     * */
    public boolean isNetFail() {
        return RESULT_NOT_200 == status;
    }

    /**
     * 网络请求成功, 返回200相应
     * */
    public boolean isNetSuccess() {
        return RESULT_OK == status;
    }

    public boolean isIOError(){
        return EXCEPTION_TCP_BROKEN.equals(error) || EXCEPTION_EOF.equals(error);
    }

    /**
     * 超时出错
     * */
    public boolean isTimeOutError(){
        return EXCEPTION_TIME_OUT.equals(error);
    }


    /**
     * 网络请求成功, 并且服务器处理成功
     * */
    public boolean isRequestSuccess() {
        return null != resultResponse && null != resultResponse.status && 0 == resultResponse.status;
    }
}
