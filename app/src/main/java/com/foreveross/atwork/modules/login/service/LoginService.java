package com.foreveross.atwork.modules.login.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.AppSyncNetService;
import com.foreveross.atwork.api.sdk.app.responseJson.AppListResponseJson;
import com.foreveross.atwork.api.sdk.auth.LoginSyncNetService;
import com.foreveross.atwork.api.sdk.auth.inter.OnEncryptInitListener;
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResponse;
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult;
import com.foreveross.atwork.api.sdk.auth.model.LoginInitResp;
import com.foreveross.atwork.api.sdk.auth.model.LoginTokenJSON;
import com.foreveross.atwork.api.sdk.auth.model.LoginTokenResponseJSON;
import com.foreveross.atwork.api.sdk.auth.model.LoginWithFaceBioRequest;
import com.foreveross.atwork.api.sdk.auth.model.LoginWithMobileRequest;
import com.foreveross.atwork.api.sdk.auth.model.PhoneSecureCodeRequestJson;
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionListResponseJson;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.setting.DynamicPropertiesAsyncNetService;
import com.foreveross.atwork.api.sdk.users.responseJson.ContactSyncItemJson;
import com.foreveross.atwork.api.sdk.users.responseJson.ContactSyncResponse;
import com.foreveross.atwork.api.sdk.users.responseJson.FriendSyncItemJson;
import com.foreveross.atwork.api.sdk.users.responseJson.FriendSyncResponse;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.LoginToken;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.AppWrapHelper;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.infrastructure.utils.encryption.RsaUtilKt;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.manager.AgreementManager;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.DomainSettingsHelper;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.chat.util.SessionAppHelper;
import com.foreveross.atwork.modules.login.listener.BasicLoginNetListener;
import com.foreveross.atwork.modules.login.listener.LoginNetListener;
import com.foreveross.atwork.modules.login.model.LoginHandleInfo;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.UserRemoveHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dasunsy on 16/6/11.
 */
@SuppressLint("StaticFieldLeak")
public class LoginService {

    public static final String ACTION_FROM_LOGIN = "ACTION_FROM_LOGIN";

    private Context context;

    private UrlConstantManager mUrlConstantManager = UrlConstantManager.getInstance();

    private final static int SYNC_ERROR_TIMES = 3;

    public LoginService(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 请求登录
     *
     * @param loginUsername
     * @param password
     */
    public void login(final String loginUsername, final String password, final String secureCode, String versionName, final LoginNetListener loginNetListener) {

        if(BaseApplicationLike.sDomainSettings != null && BaseApplicationLike.sDomainSettings.getUserSettings() != null) {
            startLoginFlow(loginUsername, password,  secureCode, versionName, loginNetListener);

        } else {
            DomainSettingsHelper.getInstance().getDomainSettingsFromRemote(BaseApplicationLike.baseContext, true, new DynamicPropertiesAsyncNetService.OnDomainSettingsListener() {
                @Override
                public void onDomainSettingsCallback(DomainSettings domainSettings) {
                    startLoginFlow(loginUsername, password, secureCode, versionName, loginNetListener);
                }

                @Override
                public void onDomainSettingsFail() {
                    startLoginFlow(loginUsername, password, secureCode, versionName, loginNetListener);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    startLoginFlow(loginUsername, password, secureCode, versionName, loginNetListener);
                }
            });
        }

    }

    private void startLoginFlow(final String loginUsername, final String password, final String secureCode, String versionName, final LoginNetListener loginNetListener) {
        //定义登录请求认证的参数对象
        final LoginTokenJSON loginTokenJSON = new LoginTokenJSON();
        //获取相应的参数
        loginTokenJSON.clientId = loginUsername;
        loginTokenJSON.clientSecret = password;
        loginTokenJSON.originalPassword = password;
        loginTokenJSON.productVersion = versionName;
        loginTokenJSON.secureCode = secureCode;
        String romChannel = RomUtil.getRomChannel();
        if (!StringUtils.isEmpty(romChannel)) {
            loginTokenJSON.channelVendor = romChannel;
            loginTokenJSON.channelId = AppUtil.getPackageName(context);

        }
        boolean isPCOnline = PersonalShareInfo.getInstance().isPCOnline(context);
        boolean isOnlineMute = PersonalShareInfo.getInstance().isDeviceOnlineMuteMode(context);
        if (isPCOnline) {
            loginTokenJSON.silently = isOnlineMute;
        }
        loginTokenJSON.deviceName = DeviceUtil.getShowName();
        loginTokenJSON.deviceSystemInfo = "Android " + Build.VERSION.RELEASE;
//        if(AtworkConfig.OPEN_LOGIN_ENCRYPTION) {
//            loginTokenJSON.clientSecretEncrypt = true;
//            initEncryptMode(new OnEncryptInitListener() {
//                @Override
//                public void onInitResultCallback(String secret) {
//                    if (TextUtils.isEmpty(secret)) {
//                        ErrorHandleUtil.handleBaseError(-1, "");
//                        return;
//                    }
//                    StringBuilder encryptData = new StringBuilder(System.currentTimeMillis()+ "");
//                    encryptData.append(password);
//                    StringBuilder encryptedData = new StringBuilder("rsa.");
//                    try {
//                        encryptedData.append(Base64Util.encode(RsaUtilKt.encryptData(encryptData.toString().getBytes("UTF-8"), RsaUtilKt.getPublicKey(Base64Util.decode(secret)))));
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    } catch (InvalidKeySpecException e) {
//                        e.printStackTrace();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    loginTokenJSON.clientSecret = encryptedData.toString();
//                    doLogin(loginTokenJSON, loginNetListener);
//                }
//
//                @Override
//                public void networkFail(int errorCode, String errorMsg) {
//                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
//                }
//            });
//            return;
//        }
        loginTokenJSON.clientSecretEncrypt = false;
        doLogin(loginTokenJSON, loginNetListener);
    }

    public void initEncryptMode(OnEncryptInitListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                HttpResult httpResult = LoginSyncNetService.encryptModeInit();
                if (httpResult.isRequestSuccess()) {
                    return ((LoginInitResp)httpResult.resultResponse).getResult().getSecret();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String secret) {
                if (listener == null) {
                    return;
                }
                listener.onInitResultCallback(secret);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    private void doLogin(final LoginTokenJSON loginTokenJSON, final LoginNetListener loginNetListener) {

        new AsyncTask<String, Double, LoginHandleInfo>() {
            @Override
            protected LoginHandleInfo doInBackground(String... params) {

                if(!UserRemoveHelper.isUserEncryptModeMatch(context, loginTokenJSON.clientId)) {
                    PersonalShareInfo.getInstance().clearAbsolutely(context, loginTokenJSON.clientId);
                }

                //认证
                HttpResult httpResult = LoginSyncNetService.login(loginTokenJSON);
                LoginHandleInfo loginHandleInfo = new LoginHandleInfo();
                loginHandleInfo.mHttpResult = httpResult;

                if(httpResult.isRequestSuccess()) {
                    refreshLoginLocalInfoSync(httpResult, loginTokenJSON.clientId, loginTokenJSON.originalPassword);
                    loginHandleInfo.mAgreementStatus = AgreementManager.isUserLoginAgreementConfirmedSync(context);
                }

                return loginHandleInfo;
            }

            @Override
            protected void onPostExecute(LoginHandleInfo loginHandleInfo) {

                HttpResult httpResult = loginHandleInfo.mHttpResult;

                if(httpResult.isRequestSuccess()) {
                    LoginTokenResponseJSON loginTokenResponseJSON = (LoginTokenResponseJSON) httpResult.resultResponse;
                    String clientId = "";
                    if(!TextUtils.isEmpty(loginTokenResponseJSON.mLoginToken.mClientId)){
                        clientId = loginTokenResponseJSON.mLoginToken.mClientId;
                    }

                    loginNetListener.loginSuccess(clientId, loginTokenResponseJSON.mLoginToken.mNeedInitPwd);


                } else {

                    if(null != httpResult.resultResponse && 201063 == httpResult.resultResponse.status) {
                        LoginDeviceNeedAuthResponse result = JsonUtil.fromJson(httpResult.result, LoginDeviceNeedAuthResponse.class);
                        LoginDeviceNeedAuthResult loginDeviceNeedAuthResult = result.getResult();

                        if(null != loginDeviceNeedAuthResult) {
                            loginDeviceNeedAuthResult.setPreInputPwd(loginTokenJSON.originalPassword);
                            loginNetListener.loginDeviceNeedAuth(loginDeviceNeedAuthResult);
                        }
                        return;
                    }


                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, loginNetListener);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @SuppressLint("StaticFieldLeak")
    public void loginWithMobile(LoginWithMobileRequest loginWithMobileRequest, String preInputPwd, BasicLoginNetListener loginNetListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {

                HttpResult httpResult = LoginSyncNetService.loginWithMobile(context, loginWithMobileRequest);

                if(httpResult.isRequestSuccess()) {
                    refreshLoginLocalInfoSync(httpResult, loginWithMobileRequest.clientId, preInputPwd);
                }


                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    LoginTokenResponseJSON loginTokenResponseJSON = (LoginTokenResponseJSON) httpResult.resultResponse;
                    String clientId = "";
                    if(!TextUtils.isEmpty(loginTokenResponseJSON.mLoginToken.mClientId)){
                        clientId = loginTokenResponseJSON.mLoginToken.mClientId;
                    }

                    loginNetListener.loginSuccess(clientId, loginTokenResponseJSON.mLoginToken.mNeedInitPwd);

                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, loginNetListener);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @SuppressLint("StaticFieldLeak")
    public void loginWithFaceBio(LoginWithFaceBioRequest faceBioRequest, LoginNetListener loginNetListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                File file = new File(faceBioRequest.clientSecret);
                // 如果文件存在，说明是一张照片，进行照片压缩,并替换secret
                if (file.exists()) {
                    Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(file.getPath(), false);
                    byte[] content = BitmapUtil.Bitmap2JpgBytes(bitmap, true);
                    int compressSize = 1 << 20;
                    byte[] photoCompressedContent = ImageShowHelper.compressImage(content, null, Bitmap.CompressFormat.JPEG,
                            ImageShowHelper.ORIGINAL_TARGET_WIDTH * ImageShowHelper.ORIGINAL_TARGET_WIDTH, compressSize);
                    faceBioRequest.clientSecret = Base64.encodeToString(photoCompressedContent, Base64.NO_WRAP);
                }
                HttpResult httpResult = LoginSyncNetService.loginWithFaceBio(context, faceBioRequest);
                LoginHandleInfo loginHandleInfo = new LoginHandleInfo();
                loginHandleInfo.mHttpResult = httpResult;

                if(httpResult.isRequestSuccess()) {
                    if (faceBioRequest.getSaveToken()) {
                        refreshLoginLocalInfoSync(httpResult, faceBioRequest.clientId, null);
                        loginHandleInfo.mAgreementStatus = AgreementManager.isUserLoginAgreementConfirmedSync(context);
                    }
                }
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    LoginTokenResponseJSON loginTokenResponseJSON = (LoginTokenResponseJSON) httpResult.resultResponse;
                    String clientId = "";
                    if(!TextUtils.isEmpty(loginTokenResponseJSON.mLoginToken.mClientId)){
                        clientId = loginTokenResponseJSON.mLoginToken.mClientId;
                    }

                    loginNetListener.loginSuccess(clientId, loginTokenResponseJSON.mLoginToken.mNeedInitPwd);

                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, loginNetListener);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * 发送手机验证码
     * @param phoneSecureCodeRequest
     * @param listener
     * */
    @SuppressLint("StaticFieldLeak")
    public void requestPhoneSecureCode(PhoneSecureCodeRequestJson phoneSecureCodeRequest, BaseCallBackNetWorkListener listener) {
        new AsyncTask<Void, Void, HttpResult>(){

            @Override
            protected HttpResult doInBackground(Void... voids) {
                return LoginSyncNetService.requestPhoneSecureCode(context, phoneSecureCodeRequest);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    listener.onSuccess();
                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void refreshLoginLocalInfoSync(HttpResult httpResult, String loginUsername, String password) {
        LoginTokenResponseJSON loginTokenResponseJSON = (LoginTokenResponseJSON) httpResult.resultResponse;

        LoginUserInfo.getInstance().clear(context);
        PersonalShareInfo.getInstance().clear();

        String clientId = "";
        if(!TextUtils.isEmpty(loginTokenResponseJSON.mLoginToken.mClientId)){
            clientId = loginTokenResponseJSON.mLoginToken.mClientId;
        }

        loginTokenResponseJSON.saveToShared(context);

        User localLoginUser = UserManager.getInstance().queryLocalUser(clientId);
        String localLoginShowName = "";
        String localLoginAvatar = "";
        String localLoginSignature = "";
        if(null != localLoginUser) {
            localLoginShowName = localLoginUser.getShowName();
            localLoginAvatar = localLoginUser.getAvatar();
            localLoginSignature = localLoginUser.getSignature();
        }

        LoginUserInfo.getInstance().setLoginUserBasic(context, clientId, AtworkConfig.DOMAIN_ID, loginUsername, loginUsername, localLoginShowName, localLoginAvatar, localLoginSignature);

        if (AtworkConfig.PERSISTENCE_PWD && null != password) {
            LoginUserInfo.getInstance().setLoginUserPw(context, password);
        }
        LoginUserInfo.getInstance().setLoginTime(context, TimeUtil.getCurrentTimeInMillis());

        PersonalShareInfo.getInstance().resetEncryptMode(context);
    }




    /**
     * 删除设备
     */
    public void deleteDeviceForRemote() {
        LoginToken loginToken = LoginUserInfo.getInstance().getLoginToken(context);
        final String deleteUrl = String.format(mUrlConstantManager.getDelteDevice(), loginToken.mClientId, loginToken.mAccessToken);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnectionComponent.getInstance().deleteHttp(deleteUrl);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 同步所有数据
     *
     * @param syncListener
     */
    @SuppressLint("StaticFieldLeak")
    public void sync(final SyncListener syncListener) {

        new AsyncTask<String, Void, SyncResult>() {
            @Override
            protected SyncResult doInBackground(String... params) {
                SyncResult syncResult = new SyncResult();
                //同步群组
                syncDiscussions(syncResult);

                //同步星标联系人
                syncFlagContacts(syncResult);

                //同步好友
                syncFriends(syncResult);

                //同步组织架构
                syncOrganizations(syncResult);

                if(BeeWorks.getInstance().config.isLite()) {
                    autoJoinOrg(syncResult);
                }

                //同步 app
                syncApps(syncResult);

                return syncResult;
            }

            @Override
            protected void onPostExecute(SyncResult syncResult) {
                syncListener.syncSuccess(syncResult.mFriendList, syncResult.mFlagContactList, syncResult.mDiscussionList, syncResult.mOrganizationList, syncResult.mAppList);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void autoJoinOrg(final SyncResult syncResult){
        if(syncResult.mOrganizationList.isEmpty()){
            OrganizationManager.getInstance().autoJoinOrg(context,(organization) -> {
                if(organization!=null){
                    AtworkApplicationLike.runOnMainThread(() -> {
                        AtworkToast.showToast("自动成功加入组织:" + organization.mName);
                        syncOrganizations(syncResult);
                        syncDiscussions(syncResult);
                    });
                }
            });
        }
    }


    /**
     * 同步用户好友接口
     *
     * @param syncResult
     * @return
     */
    private SyncResult syncFriends(final SyncResult syncResult) {
        HttpResult httpResult = syncResult(UrlConstantManager.getInstance().V2_fetchUserFriendsUrl());
        SyncResult errorResult = handleErrorResult(httpResult);
        if (errorResult != null) {
            return errorResult;
        }

        FriendSyncResponse response = new Gson().fromJson(httpResult.result, FriendSyncResponse.class);

        List<User> friendList = new ArrayList<>();
        friendList.addAll(FriendSyncItemJson.toUserList(response.result));

        syncResult.mFriendList = friendList;
        syncResult.mSyncSuccess = true;
        return syncResult;
    }

    /**
     * 同步星标联系人接口
     *
     * @param syncResult
     * @return
     */
    private SyncResult syncFlagContacts(final SyncResult syncResult) {
        int errorTimes = 0;
        HttpResult httpResult = null;
        SyncResult errorResult = null;
        while (SYNC_ERROR_TIMES > errorTimes) {
            httpResult = syncResult(UrlConstantManager.getInstance().V2_fetchUserFlagContactsUrl());
            errorResult = handleErrorResult(httpResult);

            if (null != errorResult) {
                errorTimes++;

            } else {

                break;
            }
        }


        if (null != errorResult) {
            return errorResult;
        }


        ContactSyncResponse response = new Gson().fromJson(httpResult.result, ContactSyncResponse.class);

        List<User> flagList = new ArrayList<>();
        flagList.addAll(ContactSyncItemJson.toUserList(response.result));

        syncResult.mFlagContactList = flagList;
        syncResult.mSyncSuccess = true;

        LoginUserInfo.getInstance().setStarContactSyncStatus(BaseApplicationLike.baseContext, true);

        return syncResult;
    }


    /**
     * 获取群
     *
     * @param syncResult
     * @return
     */
    private SyncResult syncDiscussions(SyncResult syncResult) {
        int errorTimes = 0;
        HttpResult httpResult = null;
        SyncResult errorResult = null;
        while (SYNC_ERROR_TIMES > errorTimes) {
            httpResult = syncResult(UrlConstantManager.getInstance().V2_fetchUserDiscussionsUrl());
            errorResult = handleErrorResult(httpResult);
            if (null != errorResult) {
                errorTimes++;

            } else {

                break;
            }
        }

        if(null != errorResult) {
            return errorResult;
        }

        DiscussionListResponseJson discussionListResponseJson = JsonUtil.fromJson(httpResult.result, DiscussionListResponseJson.class);

        //获得群组信息
        syncResult.mDiscussionList = discussionListResponseJson.mDiscussionList;
        syncResult.mSyncSuccess = true;

        LoginUserInfo.getInstance().setDiscussionSyncStatus(BaseApplicationLike.baseContext, true);

        return syncResult;
    }

    /**
     * 获取组织架构
     *
     * @param syncResult
     * @return
     */
    private SyncResult syncOrganizations(SyncResult syncResult) {
        List<Organization> organizations = OrganizationManager.getInstance().queryUserOrganizationsFromRemote(context);
        if (organizations == null) {
            return createErrorInstance(-1, "parse organizations on login sync net service");
        }

        //当同步过来的组织列表里包含了上次记录的 orgCode, 则无需设定, 否则, 拿第一个
        if (!ListUtil.isEmpty(organizations) && !(Organization.getOrgCodeList(organizations).contains(PersonalShareInfo.getInstance().getCurrentOrg(context)))) {

            Collections.sort(organizations);
            OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(context, organizations.get(0).mOrgCode, false);

        }

        syncResult.mOrganizationList = organizations;
        syncResult.mSyncSuccess = true;

        LoginUserInfo.getInstance().setOrganizationSyncStatus(BaseApplicationLike.baseContext, true);


        return syncResult;
    }

    private SyncResult syncApps(SyncResult syncResult) {
        List<App> userAppList = new ArrayList<>();
        if (!ListUtil.isEmpty(syncResult.mOrganizationList)) {

            for (Organization org : syncResult.mOrganizationList) {
                if (org == null) {
                    continue;
                }


                MultiResult<Boolean> multiResult = AppManager.getInstance().getAppCheckUpdateController().initAppsSyncDataSync(org.mOrgCode, null);
                if(multiResult.localResult && PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext).equals(org.mOrgCode)) {
                    AppRefreshHelper.refreshApp();
                }

//                doSyncApps(userAppList, org);

            }

        }

        SessionAppHelper.handleSessions(userAppList);

        syncResult.mAppList = userAppList;
        syncResult.mSyncSuccess = true;
        return syncResult;
    }

    private void doSyncApps(List<App> userAppList, Organization org) {
        HttpResult httpResult = AppSyncNetService.getInstance().queryUserAppsFromRemote(context, org.mOrgCode);
        if(httpResult.isRequestSuccess()) {
            AppListResponseJson appListResponseJson = (AppListResponseJson) httpResult.resultResponse;

            if (ListUtil.isEmpty(appListResponseJson.result.mAccessList)) {
                return;
            }

            AppWrapHelper.wrapAppShortcutList(appListResponseJson.result.mAccessList, appListResponseJson.result.mShortcutList);

            for (App app : appListResponseJson.result.mAccessList) {
                if (app == null) {
                    continue;
                }
                app.transferAccessType();
                app.transferKind();
                app = AppWrapHelper.transferApp(app);

                userAppList.add(app);
            }
            for (App app : appListResponseJson.result.mAdminList) {
                if (app == null) {
                    continue;
                }
                app.transferAdminType();
                app.transferKind();
                app = AppWrapHelper.transferApp(app);

                userAppList.add(app);

            }
        } else {

            if(null != httpResult.resultResponse) {
                ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);

            }
        }
    }

    /**
     * 发起http请求，返回http请求数据
     *
     * @param url
     * @return
     */
    private HttpResult syncResult(String url) {
        LoginUserInfo loginUserInfo = LoginUserInfo.getInstance();
        final String accessToken = loginUserInfo.getLoginToken(context).mAccessToken;
        final String userId = loginUserInfo.getLoginToken(context).mClientId;
        url = String.format(url, userId, accessToken);
        return HttpURLConnectionComponent.getInstance().getHttp(url);
    }

    private SyncResult handleErrorResult(HttpResult httpResult) {
        if (httpResult.isNetFail()) {
            return createErrorInstance(httpResult.statusCode, null);
        }

        if (httpResult.isNetError()) {
            return createErrorInstance(httpResult.status, httpResult.error);
        }
        int status = NetWorkHttpResultHelper.getResultStatus(httpResult.result);
        if (status != 0) {
            return createErrorInstance(httpResult.status, NetWorkHttpResultHelper.getResultMessage(httpResult.result));
        }
        return null;
    }

    private SyncResult createErrorInstance(int errorCode, String errorMsg) {
        SyncResult syncResult = new SyncResult();
        syncResult.mErrorCode = errorCode;
        syncResult.mErrorMsg = errorMsg;
        syncResult.mSyncSuccess = false;
        return syncResult;
    }


    /**
     * 同步监听
     */
    public interface SyncListener {
        /**
         * @param userFriendList
         * @param userFlagList
         * @param discussionList
         * @param appList
         */
        void syncSuccess(List<User> userFriendList, List<User> userFlagList, List<Discussion> discussionList, List<Organization> organizationList, List<App> appList);

        /**
         * 同步失败
         *
         * @param errorCode
         * @param errorMsg
         */
        void syncFail(int errorCode, String errorMsg);
    }


    public class SyncResult {

        public boolean mSyncSuccess;

        public int mErrorCode;

        public String mErrorMsg;

        /**
         * 群组列表
         */
        public List<Discussion> mDiscussionList;

        /**
         * 好友列表
         */
        public List<User> mFriendList;

        /**
         * 星标联系人
         */
        public List<User> mFlagContactList;

        /**
         * 组织架构
         */
        public List<Organization> mOrganizationList;

        /**
         * 应用列表(新版本不依赖这个做全量更新了)
         */
        public List<App> mAppList;
    }

}
