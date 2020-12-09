package com.foreveross.atwork.api.sdk.users.responseJson;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.user.LoginToken;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/4/10.
 */

public class ModifyPasswordResponse extends BasicResponseJSON {
    @SerializedName("result")
    public LoginToken mLoginToken;

    public void saveToShared(Context context) {
        if (!TextUtils.isEmpty(mLoginToken.mAccessToken) && !TextUtils.isEmpty(mLoginToken.mClientId)) {
            LoginUserInfo.getInstance().setLoginToken(context, mLoginToken);
        }

    }
}
