package com.foreveross.atwork.manager;

import com.foreveross.atwork.infrastructure.utils.Logger;
import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

public class BaseApiListener implements IRequestListener {

    private static final String TAG = BaseApiListener.class.getSimpleName();


    @Override
    public void onComplete(JSONObject jsonObject) {
        Logger.e(TAG, "onComplete");
    }

    @Override
    public void onIOException(IOException e) {
        Logger.e(TAG, "onIOException");
    }

    @Override
    public void onMalformedURLException(MalformedURLException e) {
        Logger.e(TAG, "onMalformedURLException");
    }

    @Override
    public void onJSONException(JSONException e) {
        Logger.e(TAG, "onJSONException");
    }

    @Override
    public void onConnectTimeoutException(ConnectTimeoutException e) {
        Logger.e(TAG, "onConnectTimeoutException");
    }

    @Override
    public void onSocketTimeoutException(SocketTimeoutException e) {
        Logger.e(TAG, "onSocketTimeoutException");
    }

    @Override
    public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {
        Logger.e(TAG, "onNetworkUnavailableException");
    }

    @Override
    public void onHttpStatusException(HttpUtils.HttpStatusException e) {
        Logger.e(TAG, "onHttpStatusException");
    }

    @Override
    public void onUnknowException(Exception e) {
        Logger.e(TAG, "onUnknowException");
    }
}