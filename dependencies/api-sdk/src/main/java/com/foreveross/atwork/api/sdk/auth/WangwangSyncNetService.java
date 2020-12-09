package com.foreveross.atwork.api.sdk.auth;

import android.content.Context;

import com.foreveross.atwork.api.sdk.auth.model.WangwangAuthRequest;
import com.foreveross.atwork.api.sdk.auth.model.WangwangAuthResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;

public class WangwangSyncNetService {

    public static String auth(Context context) {


        String userName = LoginUserInfo.getInstance().getLoginUserUserName(context);
//        userName = "00332812"; //test
        String deviceId = AtworkConfig.getDeviceId();
        String uGuess = LoginUserInfo.getInstance().getLoginUserBasic(context).getPassword();
//        uGuess = "111LIyi@"; // test


        String msgUid = MD5Utils.encoderByMd5(userName + deviceId);
        String lastLetter = userName.substring(userName.length() - 1);
        String rm = Base64Util.encode((Base64Util.encode(uGuess.getBytes()) + lastLetter).getBytes());



        WangwangAuthRequest wangwangAuthRequest = WangwangAuthRequest.newBuilder()
                .setUserId(userName)
                .setDeviceId(deviceId)
                .setVersion(AppUtil.getVersionName(context))
                .setOsType("AZ")
                .setMsgUid(msgUid)
                .setRm(rm)
                .build();


        String apiUrl = AtworkConfig.WANGWANG_AUTH_URL_CONFIG.getApiUrl();


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttpForm(apiUrl, wangwangAuthRequest.getFormParm());
        if(httpResult.isNetSuccess()) {
            WangwangAuthResponse wangwangAuthResponse = JsonUtil.fromJson(httpResult.result, WangwangAuthResponse.class);
            if(null != wangwangAuthResponse) {
                String token = wangwangAuthResponse.getToken();
                LogUtil.e("wangwang token -> " + token);
                return token;
            }
        }

        return StringUtils.EMPTY;

    }
}
