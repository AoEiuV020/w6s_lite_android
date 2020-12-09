package com.foreveross.atwork.modules.login.listener;

import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult;

/**
 * Created by dasunsy on 2017/7/22.
 */

public interface LoginNetListener extends BasicLoginNetListener {

    void loginDeviceNeedAuth(LoginDeviceNeedAuthResult result);
}

