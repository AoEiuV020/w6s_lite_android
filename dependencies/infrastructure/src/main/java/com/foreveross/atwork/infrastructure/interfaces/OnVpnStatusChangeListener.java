package com.foreveross.atwork.infrastructure.interfaces;

/**
 * Created by dasunsy on 2017/11/21.
 */

public interface OnVpnStatusChangeListener {

    void onInitFail(String errMsg);

    void onInitSuccess();

    void onAuthFail(String errMsg);

    void onAuthSuccess();

    void onNoNetwork();

    void onLogout();
}
