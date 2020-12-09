package com.foreveross.atwork.api.sdk.auth.inter;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;

public  interface OnEncryptInitListener extends NetWorkFailListener {

    void onInitResultCallback(String secret);
}
