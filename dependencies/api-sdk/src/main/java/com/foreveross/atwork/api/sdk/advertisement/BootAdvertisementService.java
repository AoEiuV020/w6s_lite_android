package com.foreveross.atwork.api.sdk.advertisement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig;
import com.foreveross.atwork.infrastructure.shared.AdvertisementInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by reyzhang22 on 17/9/15.
 */

public class BootAdvertisementService {

    public static final String TAG = BootAdvertisementService.class.getName();

    public static final BootAdvertisementService sInstance = new BootAdvertisementService();

    public static BootAdvertisementService getInstance() {
        return sInstance;
    }

    /**
     * 获取组织下最新的广告配置
     * @param context
     * @param orgId
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void getLatestBootAdvertisements(final Context context, final String orgId, final OnFetchLatestAdvertisementListener listener) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().getBootAdvertisements(), orgId, accessToken);
        new AsyncTask<Void, Void, List<AdvertisementConfig>>() {

            @Override
            protected List<AdvertisementConfig> doInBackground(Void... params) {
                HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
                if (httpResult.isNetError()) {
                    Logger.e(TAG, "get advertisement error :" + httpResult.error + "_" + httpResult.statusCode);
                    return null;
                }
                if (httpResult.isNetFail()) {
                    Logger.e(TAG, "get advertisement fail :" + httpResult.error + "_" + httpResult.statusCode);
                    return null;
                }
                int resultStatus = NetWorkHttpResultHelper.getResultStatus(httpResult.result);
                if (resultStatus != 0) {
                    Logger.e(TAG, "get advertisement status error :" + resultStatus);
                    return null;
                }
                String resultText = NetWorkHttpResultHelper.getResultText(httpResult.result);
                if (TextUtils.isEmpty(resultText)) {
                    Logger.e(TAG, "failData : resultText");
                    Logger.e(TAG, "get advertisement fail : data is empty");
                    return null;
                }
                AdvertisementInfo.getInstance().setOrgAdvertisementData(context, orgId, resultText);
                return AdvertisementConfig.getGson().fromJson(resultText, new TypeToken<List<AdvertisementConfig>>(){}.getType());
            }

            @Override
            protected void onPostExecute(List<AdvertisementConfig> advertisementConfigList) {
                super.onPostExecute(advertisementConfigList);
                if (listener == null) {
                    return;
                }
                if (advertisementConfigList == null) {
                    Logger.e(TAG, "parse ad result json fail");
                    listener.onFetchFail();
                    return;
                }
                listener.onFetchSuccess(advertisementConfigList);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }



    public interface OnFetchLatestAdvertisementListener {
        void onFetchSuccess(List<AdvertisementConfig> advertisementConfigList);
        void onFetchFail();
    }

    public interface OnPostAdvertisementEventListener extends NetWorkFailListener {
        void onPostSuccess();
    }
}
