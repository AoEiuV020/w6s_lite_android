package com.foreveross.atwork.infrastructure.interfaces;

/**
 * Created by dasunsy on 2017/11/22.
 */

public interface OnVpnReLoginListener {
    void onStartReLogin();

    void onReLoginSuccessful();

    void onReLoginFailed();
}
