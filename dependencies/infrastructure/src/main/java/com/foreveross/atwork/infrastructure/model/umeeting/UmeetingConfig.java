package com.foreveross.atwork.infrastructure.model.umeeting;

/**
 * Created by dasunsy on 2017/11/10.
 */

public class UmeetingConfig {
    public String mAppKey;

    public String mAppSecret;

    public String mWebDomain;



    public String mUrl;

    public String mInviteUrl;


    public String mInvokeUrl;

    public static UmeetingConfig newInstance () {
        return new UmeetingConfig();
    }

    public UmeetingConfig setAppKey(String appKey) {
        mAppKey = appKey;
        return this;
    }

    public UmeetingConfig setAppSecret(String appSecret) {
        mAppSecret = appSecret;
        return this;
    }

    public UmeetingConfig setWebDomain(String webDomain) {
        mWebDomain = webDomain;
        return this;
    }

    public UmeetingConfig setUrl(String url) {
        mUrl = url;
        return this;
    }

    public UmeetingConfig setInviteUrl(String inviteUrl) {
        mInviteUrl = inviteUrl;
        return this;
    }

    public UmeetingConfig setInvokeUrl(String invokeUrl) {
        mInvokeUrl = invokeUrl;
        return this;
    }

}
