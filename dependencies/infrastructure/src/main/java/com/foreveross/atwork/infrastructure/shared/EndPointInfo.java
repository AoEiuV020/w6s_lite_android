package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.user.EndPoint;
import com.foreveross.atwork.infrastructure.utils.EncryptSharedPrefsUtils;

import java.util.concurrent.CopyOnWriteArrayList;

public class EndPointInfo {

    public static final String END_POINT_SP = "END_POINT_SP";

    public static final String KEY_END_POINT_ADDRESS = "KEY_END_POINT_ADDRESS";

    public static final String KEY_END_POINT_PORT = "KEY_END_POINT_PORT";

    public static final String KEY_END_POINT_SECRET = "KEY_END_POINT_SECRET";

    public static final String KEY_END_POINT_SSL_ENABLE = "KEY_END_POINT_SSL_ENABLE";

    public static final String KEY_END_POINT_SSL_VERIFY = "KEY_END_POINT_SSL_VERIFY";

    public static final String KEY_LAST_RETRY_REMOTE_TIME = "KEY_LAST_RETRY_REMOTE_TIME";


    private static volatile EndPointInfo sInstance = new EndPointInfo();

    public static EndPointInfo getInstance() {
        return sInstance;
    }

    private EndPoint mCurrentEndpoint;

    public EndPoint getCurrentEndpointInfo(Context context) {
        if (mCurrentEndpoint == null) {
            mCurrentEndpoint = new EndPoint();
            mCurrentEndpoint.mSessionHost = EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).getString(KEY_END_POINT_ADDRESS, "");
            mCurrentEndpoint.mSessionPort = EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).getString(KEY_END_POINT_PORT, "8020");
            mCurrentEndpoint.mSecret = EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).getString(KEY_END_POINT_SECRET, "");
            mCurrentEndpoint.mSslEnabled = EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).getBoolean(KEY_END_POINT_SSL_ENABLE, false);
            mCurrentEndpoint.mSslVerify = EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).getBoolean(KEY_END_POINT_SSL_VERIFY, false);
        }
        return mCurrentEndpoint;
    }

    public void setEndpointInfo(Context context, EndPoint endpoint) {
        mCurrentEndpoint = endpoint;
        EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).putString(KEY_END_POINT_ADDRESS, endpoint.mSessionHost);
        EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).putString(KEY_END_POINT_PORT, endpoint.mSessionPort);
        EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).putString(KEY_END_POINT_SECRET, endpoint.mSecret);
        EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).putBoolean(KEY_END_POINT_SSL_ENABLE, endpoint.mSslEnabled);
        EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).putBoolean(KEY_END_POINT_SSL_VERIFY, endpoint.mSslVerify);
        setLastRetryRemoteTime(context, System.currentTimeMillis());
    }

    public void clearLastRetryRemoteTime(Context context){
        setLastRetryRemoteTime(context, -1);
    }

    public void setLastRetryRemoteTime(Context context, long time) {
        EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).putLong(KEY_LAST_RETRY_REMOTE_TIME, time);
    }

    public long getLastRetryRemoteTime(Context context) {
        return EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).getLong(KEY_LAST_RETRY_REMOTE_TIME, -1);
    }

    public void clear(Context context) {
        mCurrentEndpoint = null;
        EncryptSharedPrefsUtils.get(context, appendedShareFileName(context)).clear();
    }

    private String appendedShareFileName(Context context) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        return userId + "_" + END_POINT_SP;
    }


}
