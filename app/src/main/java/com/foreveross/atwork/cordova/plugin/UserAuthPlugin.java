package com.foreveross.atwork.cordova.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult;
import com.foreveross.atwork.api.sdk.auth.model.LoginWithFaceBioRequest;
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService;
import com.foreveross.atwork.api.sdk.faceBio.requestModel.FaceBioToggle;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.CordovaBasicResponse;
import com.foreveross.atwork.cordova.plugin.model.GetUserTicketRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.plugin.face.OnFaceBioDetectListener;
import com.foreveross.atwork.infrastructure.plugin.face.aliyun.IAliyunFaceIDPluginKt;
import com.foreveross.atwork.infrastructure.plugin.face.model.FaceBioDetectResult;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.login.listener.LoginNetListener;
import com.foreveross.atwork.modules.login.service.LoginService;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.CordovaHelper;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.foreveross.atwork.infrastructure.plugin.face.aliyun.IAliyunFaceIDPluginKt.SDK_CODE_NO_TOUCH;
import static com.foreveross.atwork.modules.login.fragment.AccountLoginFragment.REQUEST_CAMERA_CODE;

/**
 * Created by lingen on 15/5/1.
 */
public class UserAuthPlugin extends WorkPlusCordovaPlugin implements OnFaceBioDetectListener {

    //获取当前登陆用户信息
    private static final String GET_USER_INFO = "getUserInfo";

    private static final String GET_USER_TOKEN = "getUserToken";

//    private static final String GET_ACCESS_TOKEN = "getAccessToken";

    private static final String GET_SERVER_INFO = "getServerInfo";

    private static final String ACCESS_TOKEN_EXPIRES = "onAccessTokenOverdue";

    private static final String GET_USER_TICKET = "getUserTicket";

    private static final String GET_TENANT_ID = "getTenantID";

    private static final String GET_LOGIN_USER_INFO = "getLoginUserInfo";

    private static final int REQUEST_CODE_FACE_AGREE = 0X22;

    private static final int REQEUST_CODE_FACE_AGREE_TO_LOGIN = 0X23;

    private CallbackContext mCallbackContext;

    private boolean isFaceLogin;


    private Handler mDelayHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Override
    public boolean execute(String action, String rawArgs, final CallbackContext callbackContext) throws JSONException {
        if (!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        mCallbackContext = callbackContext;
        //获取当前登陆用户信息
        if (GET_USER_INFO.equalsIgnoreCase(action)) {
            queryCurrentLoginInfo(callbackContext);
            return true;
        }

        if (GET_USER_TOKEN.equalsIgnoreCase(action)) {
            queryUserToken(callbackContext);
            return true;
        }

        if (GET_USER_TICKET.equalsIgnoreCase(action)) {
            getUserTicketRequest(rawArgs, callbackContext);
            return true;
        }

        if (ACCESS_TOKEN_EXPIRES.equalsIgnoreCase(action)) {
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.TOKEN_EXPIRE));
            return true;
        }

//        //获取ACCESS_TOKEN
//        if (GET_ACCESS_TOKEN.equalsIgnoreCase(action)) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("access_token", LoginUserInfo.getInstance().getLoginUserAccessToken(cordova.getActivity()));
//            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
//            callbackContext.sendPluginResult(pluginResult);
//            callbackContext.success();
//        }

        if (GET_SERVER_INFO.equalsIgnoreCase(action)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("api_url", AtworkConfig.API_URL);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
            callbackContext.sendPluginResult(pluginResult);
            callbackContext.success();
            return true;
        }

        if (GET_TENANT_ID.equalsIgnoreCase(action)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tenant_id", AtworkConfig.DOMAIN_ID);
            jsonObject.put("domain_id", AtworkConfig.DOMAIN_ID);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
            callbackContext.sendPluginResult(pluginResult);
            callbackContext.success();
            return true;
        }

        if (GET_LOGIN_USER_INFO.equalsIgnoreCase(action)) {
            fetchCurrentLoginInfo(callbackContext);
            return true;
        }

        return false;
    }



    private void queryUserToken(final CallbackContext callbackContext) {
        CordovaAsyncNetService.getUserToken(BaseApplicationLike.baseContext, new CordovaAsyncNetService.GetUserTokenListener() {
            @Override
            public void getUserTokenSuccess(String accessToken) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_token", accessToken);

                    CordovaHelper.doSuccess(jsonObject, callbackContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                callbackContext.error();
            }
        });
    }


    private void fetchCurrentLoginInfo(CallbackContext callbackContext) {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(LoginUserInfo.getInstance()));
            CordovaHelper.doSuccess(jsonObject, callbackContext);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取当前登录用户信息
     */
    private void queryCurrentLoginInfo(CallbackContext callbackContext) {
        UserManager.getInstance().queryUserByUserId(AtworkApplicationLike.baseContext, LoginUserInfo.getInstance().getLoginUserId(AtworkApplicationLike.baseContext), LoginUserInfo.getInstance().getLoginUserDomainId(AtworkApplicationLike.baseContext), new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(gson.toJson(user));
                    CordovaHelper.doSuccess(jsonObject, callbackContext);
                } catch (Exception e) {
                    LogUtil.e("error!", e.getMessage(), e);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

            }
        });



    }


    private String getUserTicketRequestSync(String getUserTicketUrl, String rawArgs, final CallbackContext callbackContext) {

        GetUserTicketRequest getUserTicketRequest = NetGsonHelper.fromCordovaJson(rawArgs, GetUserTicketRequest.class);

        String orgCode = null;
        if(null != getUserTicketRequest) {
            orgCode = getUserTicketRequest.mOrgCode;
        }

        //检查传入的 orgCode, 当前 user 是否在里面
        if(!StringUtils.isEmpty(orgCode)) {
            List<String> myOrgCodeList = OrganizationManager.getInstance().getLoginOrgCodeListSync();

            if(!myOrgCodeList.contains(orgCode)) {
                callbackContext.error();
            }

        }


        return CordovaAsyncNetService.getUserTicketResultSync(AtworkApplicationLike.baseContext, orgCode, getUserTicketUrl,null);
    }

    @SuppressLint("StaticFieldLeak")
    private void getUserTicketRequest(String rawArgs, final CallbackContext callbackContext) {

        final String getUserTicketUrl = String.format(UrlConstantManager.getInstance().getTicketUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(AtworkApplicationLike.baseContext));

        cordova.getThreadPool().submit(() -> {
            String ticket = getUserTicketRequestSync(getUserTicketUrl, rawArgs, callbackContext);
            try {
                if (!StringUtils.isEmpty(ticket)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_ticket", ticket);
                    CordovaHelper.doSuccess(jsonObject, callbackContext);

                } else {
                    callbackContext.error("获取失败");
                }

            } catch (Exception e) {
                e.printStackTrace();
                callbackContext.error("获取失败");
            }
        });
    }



    private String loginUsername = "";
    private boolean needSaveToken = false;


    private void login(String secret) {

        final LoginWithFaceBioRequest loginWithFaceBioRequest = new LoginWithFaceBioRequest();
        loginWithFaceBioRequest.clientId = loginUsername;
        loginWithFaceBioRequest.clientSecret = secret;
        loginWithFaceBioRequest.setSaveToken(needSaveToken);

        new LoginService(cordova.getActivity()).loginWithFaceBio(loginWithFaceBioRequest, new LoginNetListener() {
            @Override
            public void loginDeviceNeedAuth(LoginDeviceNeedAuthResult result) {

            }

            @Override
            public void loginSuccess(String clientId, boolean needInitPwd) {
                sendFaceBioPluginCallback(0, "valid success");
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                sendFaceBioPluginCallback(errorCode, errorMsg);
            }
        });
    }


    private static final int CODE_VERIFY_TOKEN_FAILED = 10003;

    private static final int CODE_FACE_BIO_CANCELED = 10004;

    private static final int CODE_FACE_BIO_FAILED = 10005;

    private void faceBioCordovaErrorBack(int sdkErrorCode, int netErrorCode, @Nullable String netErrorMsg, CallbackContext callbackContext) {
        if(SDK_CODE_NO_TOUCH == sdkErrorCode) {
            //10003 表示初始化失败(获取token失败)
            CordovaHelper.doSuccess(new CordovaBasicResponse(CODE_VERIFY_TOKEN_FAILED, netErrorMsg), callbackContext);

        } else {

            int errorCode = sdkErrorCode;
            switch (sdkErrorCode) {
                case IAliyunFaceIDPluginKt.SDK_CODE_NOT:
                    errorCode = CODE_FACE_BIO_CANCELED;
                    break;

                case IAliyunFaceIDPluginKt.SDK_CODE_FAIL:
                    errorCode = CODE_FACE_BIO_FAILED;
                    break;


            }

            CordovaHelper.doSuccess(new CordovaBasicResponse(errorCode, "检测不通过"), callbackContext);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            if (intent == null) {
                return;
            }
            String photoPath = intent.getStringExtra("BIO_FACE_PHOTO_PATH");
            if (isFaceLogin) {
                login(photoPath);
            } else {
                FaceBioToggle toggle = new FaceBioToggle("");
                toggle.setToggleEnable(true);
                toggle.setPhotoPath(photoPath);
            }

        }
    }

    private void sendFaceBioPluginCallback(int errorCode, String errorMsg) {

        JSONObject json = new JSONObject();
        try {
            json.put("resultCode", errorCode);
            if(errorCode == 0) {
                json.put("resultMsg", "认证成功");
            } else {
                json.put("resultMsg", String.format(cordova.getActivity().getString(R.string.cloud_auth_fail), errorCode + ""));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
        mCallbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onDetectSuccess(@NotNull FaceBioDetectResult detectResult) {

    }

    @Override
    public void onDetectFailure(int errorCode, @NotNull String errorMsg) {

    }

}

