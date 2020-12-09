package com.foreveross.atwork.infrastructure.shared;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.user.LoginToken;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录用户信息 sharedPreference
 * Created by reyzhang22 on 16/3/24.
 */
public class LoginUserInfo implements Serializable {

    private static final String TAG = LoginUserInfo.class.getName();


    /**
     * 区别与{@link #SP_USER_LOGIN_BASIC}, 该存储登出后会清除
     * */
    private static final String SP_USER_LOGIN_FILE = "USER_LOGIN_FILE" + AtworkConfig.SP_SUFFIX_USER_LOGIN_FILE;

    /**
     * 用以存储登录用户的基本请求参数, userId, domainId, username 等,
     * 这些参数登出不清除
     */
    public static final String SP_USER_LOGIN_BASIC = "SP_USER_LOGIN_BASIC";

    //----------------------login access_token ----------------------------
    public static final String LOGIN_ACCESS_TOKEN = "LOGIN_ACCESS_TOKEN";
    public static final String LOGIN_REFRESH_TOKEN = "LOGIN_REFRESH_TOKEN";
    public static final String LOGIN_EXPIRE_TIME = "LOGIN_EXPIRE_TIME";
    public static final String LOGIN_CLIENT_ID = "LOGIN_CLIENT_ID";
    public static final String LOGIN_ISSUED_TIME = "LOGIN_ISSUED_TIME";

    @SerializedName("login_token")
    private LoginToken mLoginToken;

    //----------------------login user info ----------------------------
    public static final String LOGIN_USER_REAL_USERNAME = "LOGIN_REAL_USERNAME";
    public static final String LOGIN_USER_USERNAME = "LOGIN_USERNAME";
    public static final String LOGIN_USER_DOMAIN_ID = "LOGIN_DOMAIN_ID";
    public static final String LOGIN_USER_NAME = "LOGIN_USER_NAME";
    public static final String LOGIN_USER_AVATAR = "LOGIN_USER_AVATAR";
    public static final String LOGIN_USER_SIGNATURE = "LOGIN_USER_SIGNATURE";
    public static final String LOGIN_SECRET = "LOGIN_P";

    @SerializedName("login_user")
    private LoginUserBasic mLoginUserBasic;


    //-----------------------login session info-------------------------
    public static final String LOGIN_SESSION_HOST = "LOGIN_SESSION_HOST";
    public static final String LOGIN_SESSION_PORT = "LOGIN_SESSION_PORT";
    public static final String LOGIN_SESSION_SECRET = "LOGIN_SESSION_SECRET";
    public static final String LOGIN_SESSION_SSL_ENABLE = "LOGIN_SESSION_SSL_ENABLE";
    public static final String LOGIN_SESSION_SSL_VERIFY = "LOGIN_SESSION_SSL_VERIFY";

    //-----------------------login other info-------------------------
    public static final String LOGIN_TIME = "LOGIN_TIME";

    //-----------------------Vpn login info---------------------------
    public static final String VPN_USERNAME = "VPN_USERNAME";
    public static final String VPN_PASSWORD = "VPN_PASSWORD";
    public static final String VPN_ADDRESS = "VPN_ADDRESS";
    public static final String VPN_PORT = "VPN_PORT";
    public static final String VPN_SHOULD_OPEN = "VPN_SHOULD_OPEN";

    public static final String SYNC_WE_CHAT = "SYNC_WE_CHAT_";

    public static final String SYNC_LOGIN_DISCUSSION_STATUS = "SYNC_LOGIN_DISCUSSION_STATUS";
    public static final String SYNC_LOGIN_STAR_CONTACTS_STATUS = "SYNC_LOGIN_STAR_CONTACTS_STATUS";
    public static final String SYNC_LOGIN_ORGANIZATION_STATUS = "SYNC_LOGIN_ORGANIZATION_STATUS";
    public static final String SYNC_LOGIN_CONVERSATION_SETTING_STATUS = "SYNC_LOGIN_CONVERSATION_SETTING_STATUS";
    public static final String SYNC_LOGIN_USER_SETTING_STATUS = "SYNC_LOGIN_USER_SETTING_STATUS";
    public static final String SYNC_LOGIN_APP_STATUS = "SYNC_LOGIN_APP_STATUS";


    public static final String LOGIN_NEED_CLEAR_WEBVIEW = "LOGIN_NEED_CLEAR_WEBVIEW";

    public static final String DOC_SEARCH_HISTORY = "DOC_SEARCH_HISTORY";

    /**
     * 登录信息同步情况, 如群组, 星标联系人等
     * */
    public LoginSyncStatus mLoginSyncStatus;

    public String vpnUsername;

    public String vpnPassword;

    public String vpnAddress;

    public boolean vpnShouldOpen = false;

    public int vpnPort;

    private long mLoginTime = -1;

    public long mLastCodeLockTime = -1;

    private boolean mOfflineIsPulling = false;

    /**
     * 拉离线时的状态, 因为可能重试3次服务器仍然出错, 这时为了标记离线拉取未结束,
     * 以及新来消息不影响下次拉取离线的时间, 需要该状态值
     */
    private boolean mOfflinePullingError = false;

    /**
     * 是否初次打开手势密码
     */
    public boolean mIsInitOpenCodeLock = false;

    public long mLastReceiveEmailTime = -1;


    public static LoginUserInfo sInstance = new LoginUserInfo();

    public static LoginUserInfo getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new LoginUserInfo();
            }
            return sInstance;
        }
    }


    /**
     * 保存登录token信息
     *
     * @param context
     * @param loginToken
     */
    public void setLoginToken(Context context, LoginToken loginToken) {
        if (loginToken == null) {
            return;
        }
        mLoginToken = loginToken;
        PreferencesUtils.putString(context, SP_USER_LOGIN_FILE, LOGIN_ACCESS_TOKEN, loginToken.mAccessToken);
        PreferencesUtils.putString(context, SP_USER_LOGIN_FILE, LOGIN_REFRESH_TOKEN, loginToken.mRefreshToken);
        PreferencesUtils.putString(context, SP_USER_LOGIN_FILE, LOGIN_ISSUED_TIME, loginToken.mIssuedTime);
        PreferencesUtils.putLong(context, SP_USER_LOGIN_FILE, LOGIN_EXPIRE_TIME, loginToken.mExpireTime);
    }

    public void setWeChatConversationSettings(Context context, String conversationId, boolean syncWeChat) {
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_FILE, SYNC_WE_CHAT + conversationId, syncWeChat);
    }

    public boolean getWeChatConversationSettings(Context context, String conversationId) {
        return PreferencesUtils.getBoolean(context, SP_USER_LOGIN_FILE, SYNC_WE_CHAT + conversationId, false);
    }



    /**
     * 获取登录token
     *
     * @param context
     * @return
     */
    public LoginToken getLoginToken(Context context) {
        if (mLoginToken == null) {
            readLoginTokenFromShared(context);
        }
        if (TextUtils.isEmpty(mLoginToken.mClientId) || TextUtils.isEmpty(mLoginToken.mAccessToken)) {
            readLoginTokenFromShared(context);
        }
        return mLoginToken;
    }

    public String getLoginUserAccessToken(Context context) {

        return getLoginToken(context).mAccessToken;
    }

    private static final String LOGIN_USER_EWS_URI = "LOGIN_USER_EWS_URI";
    public String getLoginUserEmailEwsUri(Context context) {
        return PreferencesUtils.getString(context, SP_USER_LOGIN_FILE, LOGIN_USER_EWS_URI, "");
    }

    public void setLoginUserEmailEwsUri(Context context, String uri) {
        PreferencesUtils.putString(context, SP_USER_LOGIN_FILE, LOGIN_USER_EWS_URI, uri);
    }

    public boolean isLogin(Context context) {
        return getLoginUserAccessToken(context) != null && !TextUtils.isEmpty(this.mLoginToken.mAccessToken);
    }

    public boolean hasLoginBefore(Context context) {
        return !StringUtils.isEmpty(getLoginUserUserName(context));
    }

    public void setLoginUserBasic(Context context, String userId, String domainId, String realLoginUsername, String username, String showName, String avatar, String signature) {
        if (StringUtils.isEmpty(domainId)) {
            domainId = AtworkConfig.DOMAIN_ID;
        }
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, LOGIN_CLIENT_ID, userId);
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_DOMAIN_ID, domainId);
        if(null != realLoginUsername) {
            PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_REAL_USERNAME, realLoginUsername);

        }
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_USERNAME, username);
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_NAME, showName);
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_SIGNATURE, signature);

        if (null != avatar) {
            PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_AVATAR, avatar);
        }

        if (null == mLoginUserBasic) {
            mLoginUserBasic = new LoginUserBasic();
        }

        mLoginUserBasic.mUserId = userId;
        mLoginUserBasic.mDomainId = domainId;
        if(null != realLoginUsername) {
            mLoginUserBasic.mLoginRealUsername = realLoginUsername;
        }
        mLoginUserBasic.mUsername = username;
        mLoginUserBasic.mName = showName;
        if (null != avatar) {
            mLoginUserBasic.mAvatar = avatar;
        }
        mLoginUserBasic.mStatus = signature;

    }

    public void setLoginUserPw(Context context, String pw) {
        if (null == mLoginUserBasic) {
            mLoginUserBasic = new LoginUserBasic();
        }

        mLoginUserBasic.setPassword(context, pw);
    }

    public LoginUserBasic getLoginUserBasic(Context context) {
        if (mLoginUserBasic == null) {
            readLoginBasicFromShared(context);
        }

        return mLoginUserBasic;
    }

    @NonNull
    public LoginSyncStatus getLoginSyncStatus(Context context) {
        if(mLoginSyncStatus == null) {
            readLoginSyncStatusFromShared(context);
        }

        return mLoginSyncStatus;
    }

    /**
     * 获取登录用户的 userId
     *
     * @param context
     * @return userId
     */
    public String getLoginUserId(Context context) {
        return getLoginUserBasic(context).mUserId;
    }

    /**
     * 获取登录用户的 domainId
     *
     * @param context
     * @return userId
     */
    public String getLoginUserDomainId(Context context) {
        return getLoginUserBasic(context).mDomainId;
    }

    /**
     * 获取登录用户的 username
     *
     * @param context return username
     */
    public String getLoginUserUserName(Context context) {
        return getLoginUserBasic(context).mUsername;
    }


    /**
     * 请求登录时使用的 username, cimc 多域验证时,该字段作为登录时使用的请求参数
     * */
    public String getLoginUserRealUserName(Context context) {
        String loginUsername = getLoginUserBasic(context).mLoginRealUsername;
        if(StringUtils.isEmpty(loginUsername)) {
            loginUsername = getLoginUserUserName(context);
        }

        return loginUsername;
    }

    /**
     * 获取登录用户的 avatar
     * */
    public String getLoginUserAvatar(Context context) {
        return getLoginUserBasic(context).mAvatar;
    }

    /**
     * 获取登录用户的 name
     * */
    public String getLoginUserName(Context context) {
        String name = getLoginUserBasic(context).mName;
        if(StringUtils.isEmpty(name)) {
            name = getLoginUserUserName(context);
        }

        return name;
    }



    public String getLoginSecret(Context context) {
        return getLoginUserBasic(context).getPassword();
    }



    public void setLoginTime(Context context, long loginTime) {
        this.mLoginTime = loginTime;
        PreferencesUtils.putLong(context, SP_USER_LOGIN_FILE, LOGIN_TIME, loginTime);
    }

    public void setDocSearchHistory(Context context, String data) {
        PreferencesUtils.putString(context, SP_USER_LOGIN_FILE, DOC_SEARCH_HISTORY, data);
    }

    public String getDocSearchHistory(Context context) {
        return PreferencesUtils.getString(context, SP_USER_LOGIN_FILE, DOC_SEARCH_HISTORY, "");
    }

    public long getLoginTime(Context context) {
        if (-1 == mLoginTime) {
            readLoginOtherFromShared(context);
        }
        return mLoginTime;
    }

    public void clearBasic(Context context) {
        PreferencesUtils.clear(context, SP_USER_LOGIN_BASIC);
        mLoginUserBasic = null;
    }

    /**
     * 清除登录信息接口
     *
     * @param context
     */
    public void clear(Context context) {
        PreferencesUtils.clear(context, SP_USER_LOGIN_FILE);

        this.mLoginToken = null;
        this.mLoginSyncStatus = null;
        this.mLastCodeLockTime = -1;
        this.mOfflineIsPulling = false;
        this.mOfflinePullingError = false;
        this.mIsInitOpenCodeLock = false;
        this.mLastReceiveEmailTime = -1;
        this.vpnUsername = "";
        this.vpnPassword = "";

    }


    private void readLoginTokenFromShared(Context context) {
        if (mLoginToken == null) {
            mLoginToken = new LoginToken();
        }
        mLoginToken.mAccessToken = PreferencesUtils.getString(context, SP_USER_LOGIN_FILE, LOGIN_ACCESS_TOKEN);
        mLoginToken.mExpireTime = PreferencesUtils.getLong(context, SP_USER_LOGIN_FILE, LOGIN_EXPIRE_TIME);
        mLoginToken.mIssuedTime = PreferencesUtils.getString(context, SP_USER_LOGIN_FILE, LOGIN_ISSUED_TIME);
        mLoginToken.mRefreshToken = PreferencesUtils.getString(context, SP_USER_LOGIN_FILE, LOGIN_REFRESH_TOKEN);

        //userId 不用清除
        mLoginToken.mClientId = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_CLIENT_ID);
    }

    private void readLoginOtherFromShared(Context context) {
        mLoginTime = PreferencesUtils.getLong(context, SP_USER_LOGIN_FILE, LOGIN_TIME, -1);
    }

    private void readLoginBasicFromShared(Context context) {
        if (mLoginUserBasic == null) {
            mLoginUserBasic = new LoginUserBasic();
        }
        mLoginUserBasic.mLoginRealUsername = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_REAL_USERNAME);
        mLoginUserBasic.mSecret = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_SECRET);
        mLoginUserBasic.mUsername = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_USERNAME);
        mLoginUserBasic.mUserId = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_CLIENT_ID);
        mLoginUserBasic.mDomainId = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_DOMAIN_ID);
        mLoginUserBasic.mName = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_NAME);
        mLoginUserBasic.mAvatar = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, LOGIN_USER_AVATAR);
    }

    private void readLoginSyncStatusFromShared(Context context) {
        if(mLoginSyncStatus == null) {
            mLoginSyncStatus = new LoginSyncStatus();
        }

        mLoginSyncStatus.mDiscussionResult = PreferencesUtils.getBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_DISCUSSION_STATUS, false);
        mLoginSyncStatus.mStarContactResult = PreferencesUtils.getBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_STAR_CONTACTS_STATUS, false);
        mLoginSyncStatus.mConversationSettingResult = PreferencesUtils.getBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_CONVERSATION_SETTING_STATUS, false);
        mLoginSyncStatus.mUserSettingResult = PreferencesUtils.getBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_USER_SETTING_STATUS, false);

        HashSet<String> defValues = new HashSet<>();
//        defValues.add("defaultAny");
        mLoginSyncStatus.mAppOrgCodesResult = PreferencesUtils.getStringSet(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_APP_STATUS, defValues);
    }

    public void setOfflineIsPulling(Context context, boolean offlineIsPulling) {
        this.mOfflineIsPulling = offlineIsPulling;
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MESSAGE_REFRESH"));
    }

    public boolean isOfflinePulling() {
        Logger.e(TAG, "is Offline pulling.. = " + mOfflineIsPulling);
        return this.mOfflineIsPulling;
    }

    public boolean isOfflinePullingError() {
        return mOfflinePullingError;
    }

    public void setOfflinePullingError(boolean offlinePullingError) {
        this.mOfflinePullingError = offlinePullingError;
    }

    public void setVpnUsername(Context context, String vpnUsername) {
        this.vpnUsername = vpnUsername;
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, VPN_USERNAME, vpnUsername);
    }

    public String getVpnUsername(Context context) {

        return vpnUsername = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, VPN_USERNAME);
    }

    public void setVpnPassword(Context context, String vpnPassword) {
        this.vpnPassword = vpnPassword;
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, VPN_PASSWORD, vpnPassword);
    }

    public String getVpnPassword(Context context) {

        return vpnPassword = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, VPN_PASSWORD);
    }

    public void setVpnAddress(Context context, String vpnAddress) {
        this.vpnAddress = vpnAddress;
        PreferencesUtils.putString(context, SP_USER_LOGIN_BASIC, VPN_ADDRESS, vpnAddress);
    }

    public String getVpnAddress(Context context) {
        return vpnAddress = PreferencesUtils.getString(context, SP_USER_LOGIN_BASIC, VPN_ADDRESS);
    }


    public void setVpnShouldOpen(Context context, boolean vpnShouldOpen) {
        this.vpnShouldOpen = vpnShouldOpen;
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_BASIC, VPN_SHOULD_OPEN, vpnShouldOpen);
    }

    public boolean getVpnShouldOpen(Context context) {
        return PreferencesUtils.getBoolean(context, SP_USER_LOGIN_BASIC, VPN_SHOULD_OPEN);
    }

    public void setVpnPort(Context context, int vpnPort) {
        this.vpnPort = vpnPort;
        PreferencesUtils.putInt(context, SP_USER_LOGIN_BASIC, VPN_PORT, vpnPort);
    }

    public int getVpnPort(Context context) {
        return PreferencesUtils.getInt(context, SP_USER_LOGIN_BASIC, VPN_PORT);
    }

    public void setDiscussionSyncStatus(Context context, boolean result) {
        LoginSyncStatus loginSyncStatus = getLoginSyncStatus(context);

        loginSyncStatus.mDiscussionResult = result;
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_DISCUSSION_STATUS, result);
    }

    public boolean isDiscussionSyncSuccess(Context context) {
        return getLoginSyncStatus(context).mDiscussionResult;
    }

    public void setStarContactSyncStatus(Context context, boolean result) {
        LoginSyncStatus loginSyncStatus = getLoginSyncStatus(context);

        loginSyncStatus.mStarContactResult = result;
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_STAR_CONTACTS_STATUS, result);
    }

    public boolean isStarContactSyncSuccess(Context context) {
        return getLoginSyncStatus(context).mStarContactResult;
    }


    public void setAppSyncStatus(Context context, Set<String> orgCodes) {
        LoginSyncStatus loginSyncStatus = getLoginSyncStatus(context);
        loginSyncStatus.mAppOrgCodesResult = orgCodes;
        PreferencesUtils.putStringSet(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_APP_STATUS, orgCodes);
    }

    public Set<String> getAppOrgCodesSyncStatus(Context context) {
        return getLoginSyncStatus(context).mAppOrgCodesResult;
    }

    public void setOrganizationSyncStatus(Context context, boolean result) {
        LoginSyncStatus loginSyncStatus = getLoginSyncStatus(context);

        loginSyncStatus.mOrganizationResult = result;
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_ORGANIZATION_STATUS, result);
    }

    public boolean isOrganizationSyncStatus(Context context) {
        return getLoginSyncStatus(context).mOrganizationResult;
    }


    public void setConversionSettingSyncStatus(Context context, boolean result) {
        LoginSyncStatus loginSyncStatus = getLoginSyncStatus(context);

        loginSyncStatus.mConversationSettingResult = result;
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_CONVERSATION_SETTING_STATUS, result);
    }

    public void setUserSettingSyncStatus(Context context, boolean result) {
        LoginSyncStatus loginSyncStatus = getLoginSyncStatus(context);

        loginSyncStatus.mUserSettingResult = result;
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_FILE, SYNC_LOGIN_USER_SETTING_STATUS, result);
    }


    public void setUserNeedClearWebview(Context context, boolean UserNeedClearWebview) {
        PreferencesUtils.putBoolean(context, SP_USER_LOGIN_FILE, LOGIN_NEED_CLEAR_WEBVIEW, UserNeedClearWebview);
    }


    public boolean isUserNeedClearWebview(Context context) {
        return PreferencesUtils.getBoolean(context, SP_USER_LOGIN_FILE, LOGIN_NEED_CLEAR_WEBVIEW, false);
    }

}
