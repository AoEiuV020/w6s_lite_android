package com.foreveross.atwork.infrastructure.model.user;

import android.content.Context;

import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.AES128ECBUtil_V0;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/5/3.
 */
public class LoginUserBasic extends UserHandleBasic {

    /**
     * 请求登录时使用的 username, cimc 多域验证时,该字段作为登录时使用的请求参数
     * */
    @SerializedName("login_username")
    public String mLoginRealUsername;

    @SerializedName("username")
    public String mUsername = StringUtils.EMPTY;

    @SerializedName("secret")
    public transient String mSecret = StringUtils.EMPTY;

    @SerializedName("name")
    public String mName = StringUtils.EMPTY;

    @SerializedName("avatar")
    public String mAvatar = StringUtils.EMPTY;

    /**
     * 个性签名
     * */
    @SerializedName("status")
    public String mStatus = StringUtils.EMPTY;


    private transient String mKey = "1234567812345678";


    public UserHandleInfo toUserHandleInfo() {
        UserHandleInfo userHandleInfo = new UserHandleInfo();

        userHandleInfo.mUserId = this.mUserId;
        userHandleInfo.mDomainId = this.mDomainId;
        userHandleInfo.mShowName = this.mName;
        userHandleInfo.mAvatar = this.mAvatar;
        userHandleInfo.mStatus = User.STATUS_INITIALIZED; //已登录用户一定是激活状态

        return userHandleInfo;
    }

    public void setPassword(Context context, String pw) {
        this.mSecret = AES128ECBUtil_V0.encrypt(pw, mKey);

        PreferencesUtils.putString(context, LoginUserInfo.SP_USER_LOGIN_BASIC, LoginUserInfo.LOGIN_SECRET, mSecret);

    }

    public String getPassword() {
        return AES128ECBUtil_V0.decrypt(this.mSecret, mKey);
    }


    public String getShowName() {
        if(!StringUtils.isEmpty(mName)) {
            return mName;
        }

        if(!StringUtils.isEmpty(mUsername)) {
            return mUsername;
        }

        return "";

    }

}
