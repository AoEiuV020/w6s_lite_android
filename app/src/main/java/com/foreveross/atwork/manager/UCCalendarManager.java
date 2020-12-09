package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.foreverht.cache.UCCalendarCache;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.uccalendar.UCCalendarToken;
import com.foreveross.atwork.infrastructure.plugin.UCCalendarPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by reyzhang22 on 2017/11/21.
 */

public class UCCalendarManager {

    public static final String TAG = "qsy_calendar";

    private static final UCCalendarManager sInstance = new UCCalendarManager();

    private UCCalendarPlugin.IUCCalendar mPlugin;

    public static UCCalendarManager getInstance() {

        return sInstance;
    }

    /**
     * 初始化全时云日历
     */
    public void initUCCalendarClient() {
        try {
//            Reflect.on("com.foreverht.workplus.uc_calendar_client.UCCalendarPresenter").create();
            WorkplusPluginCore.registerPresenterPlugin("com.foreverht.workplus.uc_calendar_client.UCCalendarPresenter");
        } catch (ReflectException e) {
            e.printStackTrace();
        }
        WorkplusPlugin plugin = WorkplusPluginCore.getPlugin(UCCalendarPlugin.IUCCalendar.class);
        if (plugin != null) {
            mPlugin = ((UCCalendarPlugin.IUCCalendar)plugin);
            mPlugin.onCalendarInit(BaseApplicationLike.baseContext);
        }
    }

    /**
     * 登录全时云日历
     * @param uId
     * @param token
     */
    public void onLoginUCCalendar(String uId, String token, UCCalendarPlugin.OnUCCalendarLoginListener loginListener) {
        if (mPlugin == null) {
            return;
        }
        mPlugin.onCalendarLogin(uId, token, loginListener);
    }

    /**
     * 显示日历
     * @param context
     */
    public void onShowCalendar(Context context) {
        if (mPlugin == null) {
            return;
        }
        mPlugin.onShowCalendar(context);
    }

    /**
     * 显示会议详情
     * @param context
     * @param eventId
     */
    public void onShowConfDetail(Context context, long eventId) {
        if (mPlugin == null) {
            return;
        }
        mPlugin.onShowConfDetail(context, eventId);
    }

    /**
     * 创建会议
     * @param context
     * @param uIdList
     */
    public void onCreateConf(Context context, ArrayList<String> uIdList) {
        if (mPlugin == null) {
            return;
        }
        mPlugin.onCreateConf(context, uIdList);
    }

    /**
     * 加入会议
     * @param context
     * @param confId
     */
    public void onJoinConf(Context context, long confId) {
        if (mPlugin == null) {
            return;
        }
        mPlugin.onJoinConf(context, confId);
    }

    /**
     * 立即加入会议
     * @param context
     * @param confId
     * @param username
     * @param confPwd
     */
    public void onJoinConfImmediately(Context context, int confId, String username, String confPwd, UCCalendarPlugin.OnUCCalendarJoinConfListener listener) {
        if (mPlugin == null) {
            return;
        }
        mPlugin.onJoinConfImmediately(context, confId, username, confPwd, listener);
    }

    /**
     * 退出日历
     */
    public void onCalendarLogout() {
        UCCalendarCache.getInstance().clearUCCalendarCache();
        if (mPlugin == null) {
            return;
        }

        new Handler(Looper.getMainLooper()).post(()->{
            mPlugin.onCalendarLogout();
        });

    }


    public void onAddUserStatusListener(UCCalendarPlugin.OnUCCalendarUserStatusListener onUCCalendarUserStatusListener) {
        if (mPlugin == null) {
            return;
        }

        mPlugin.onAddUserStatusListener(onUCCalendarUserStatusListener);
    }

    public void onUploadLogsWithDescription(String desc, final UCCalendarPlugin.OnUCCalendarCallBack onUCCalendarCallBack) {
        if (mPlugin == null) {
            return;
        }

        String username = LoginUserInfo.getInstance().getLoginUserRealUserName(BaseApplicationLike.baseContext);
        UCCalendarToken ucCalendarToken = UCCalendarCache.getInstance().getUCCalendarCache(username);
        if (ucCalendarToken != null && ucCalendarToken.mIsLogin) {
            mPlugin.onUploadLogsWithDescription(desc, onUCCalendarCallBack);

            return;
        }



        UCCalendarManager.getInstance().getUCCalendarToken(BaseApplicationLike.baseContext, new UCCalendarPlugin.OnUCCalendarTokenListener() {
            @Override
            public void onUCCalendarTokenSuccess(UCCalendarToken token) {
                UCCalendarManager.getInstance().onLoginUCCalendar(token.mSerialNo, token.mToken, new UCCalendarPlugin.OnUCCalendarLoginListener() {
                    @Override
                    public void onUCCalendarLoginSuccess() {
                        new Handler(Looper.getMainLooper()).post(()->{
                            mPlugin.onUploadLogsWithDescription(desc, onUCCalendarCallBack);
                        });
                    }

                    @Override
                    public void onUCCalendarLoginFail(String reason) {
                        onUCCalendarCallBack.onFail(-1000);
                        AtworkToast.showToast("login fail:" + reason);
                    }
                });
            }

            @Override
            public void onUCCalendarTokenFail(int error) {
                onUCCalendarCallBack.onFail(-1000);

                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.QsyCalendar, error, StringUtils.EMPTY);
            }
        });



    }


    public void setPhoneNumber(String phoneNumber) {
        if (mPlugin == null) {
            return;
        }

        mPlugin.setPhoneNumber(phoneNumber);
    }

    /**
     * 获取ucCalendarToken
     * @param context
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void getUCCalendarToken(Context context, UCCalendarPlugin.OnUCCalendarTokenListener listener) {
        String username = LoginUserInfo.getInstance().getLoginUserRealUserName(context);
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getUCCalendarToken(), AtworkConfig.DOMAIN_ID, username, accessToken);
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return HttpURLConnectionComponent.getInstance().getHttp(url);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                super.onPostExecute(httpResult);
                if (listener == null) {
                    return;
                }
                if (httpResult.isNetSuccess()) {
                    UCCalendarToken ucCalendarToken = parseUCCalendarToken(httpResult.result);
                    if (ucCalendarToken == null) {
                        BasicResponseJSON basicResponseJSON = JsonUtil.fromJson(httpResult.result, BasicResponseJSON.class);
                        listener.onUCCalendarTokenFail(basicResponseJSON.status);

                        return;
                    }
                    listener.onUCCalendarTokenSuccess(ucCalendarToken);
                    return;
                }
                listener.onUCCalendarTokenFail(-1);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private UCCalendarToken parseUCCalendarToken(String result) {
        UCCalendarToken token = new UCCalendarToken();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.optJSONArray("result");
            JSONObject subJson = array.optJSONObject(0);
            token.mUsername = subJson.optString("username");
            token.mSerialName = subJson.optString("serial_name");
            token.mSerialNo = subJson.optString("serial_no");
            token.mToken = subJson.optString("auth_key");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        UCCalendarCache.getInstance().setUCCalendarCache(token);
        return token;
    }


}
