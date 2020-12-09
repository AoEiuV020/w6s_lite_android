package com.foreveross.atwork.api.sdk.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.domain.AppProfile;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;

/**
 * Created by ReyZhang on 2015/5/26.
 */
public class AppUpgradeNetService {

    private static final String TAG = AppUpgradeNetService.class.getSimpleName();
    private static AppUpgradeNetService sInstance;

    private UrlConstantManager mUrlConstantManager = UrlConstantManager.getInstance();


    public static AppUpgradeNetService getInstance() {
        synchronized (TAG) {
            if (null == sInstance) {
                sInstance = new AppUpgradeNetService();
            }
            return sInstance;
        }
    }


    /**
     * 应用升级
     *
     * @param packageName
     */

    private long finalPos;
    @SuppressLint("StaticFieldLeak")
    public void onAppUpgrade(Context context, final String uuid, String packageName, final MediaCenterNetManager.MediaDownloadListener downloadListener, final String filePath, long downloadPos) {
        finalPos = downloadPos;
        String url = String.format(getAppUpgradeUrl(), packageName);
        if(StringUtils.isEmpty(url)) {
            url = mUrlConstantManager.getAdminDownloadUrl();
        }


        if(!url.contains("_v")) {
            int newVersionCode = CommonShareInfo.getNewVersionCode(context);
            if(-1 != newVersionCode) {
                url  = UrlHandleHelper.addParam(url, "_v", newVersionCode + "");
            }
        }

        final String finalUrl = url;
        new AsyncTask<Void, Double, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                return MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                        DownloadFileParamsMaker.Companion.newRequest().setDownloadId(uuid).setDownloadUrl(finalUrl).setDownloadPath(filePath).setEncrypt(false).setProgressListener(
                                new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                                    @Override
                                    public void progress(double value, double size) {
                                        publishProgress(value, size);
                                    }
                                }
                        ).setDownloadPos(finalPos)
                );
            }

            @Override
            protected void onProgressUpdate(Double... values) {
                downloadListener.downloadProgress(values[0], values[1]);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isNetError()) {
                    downloadListener.downloadFailed(httpResult.status, httpResult.error, true);
                } else if (httpResult.isNetFail()) {
                    downloadListener.downloadFailed(httpResult.statusCode, null, true);
                } else {
                    downloadListener.downloadSuccess();
                }
            }
        }.executeOnExecutor(MediaCenterNetManager.DOWNLOAD_THREAD_POOL);
    }

    public String getAppUpgradeUrl() {

        if(!StringUtils.isEmpty(BeeWorks.getInstance().config.androidDownloadUrl)) {
            finalPos = -1L;
            return BeeWorks.getInstance().config.androidDownloadUrl;
        }

        AppProfile appProfile = BaseApplicationLike.getAppProfile();
        if (appProfile != null && !TextUtils.isEmpty(appProfile.mAndroidUrl)) {
            finalPos = -1L;
            return appProfile.mAndroidUrl;
        }

        if(StringUtils.isEmpty(AtworkConfig.PROFILE)) {
            return AtworkConfig.getApiMediaUrl() + "upgrade/%s?platform=android&domain_id=" + AtworkConfig.DOMAIN_ID;
        }
        return StringUtils.EMPTY;
    }

}
