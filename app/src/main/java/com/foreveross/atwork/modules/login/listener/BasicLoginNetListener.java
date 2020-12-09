package com.foreveross.atwork.modules.login.listener;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;

public interface BasicLoginNetListener extends NetWorkFailListener {
    void loginSuccess(String clientId, boolean needInitPwd);
}
