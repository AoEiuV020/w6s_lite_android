package com.foreveross.atwork.api.sdk.startPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.startPage.model.CheckStartPageDataResponse;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ZipUtil;

import java.io.IOException;

/**
 * Created by dasunsy on 15/12/14.
 */
public class StartPageNetService {

    public static void checkStartPagePackage(Context context, String orgCode, long lastLoadPackageTime, final OnCheckStartPagePackageListener listener ) {
        final String apiUrl = String.format(UrlConstantManager.getInstance().V2_checkStartPagePackageUrl(), lastLoadPackageTime, orgCode, AtworkConfig.DOMAIN_ID);

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return HttpURLConnectionComponent.getInstance().getHttp(apiUrl);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isNetSuccess()) {
                    CheckStartPageDataResponse response = JsonUtil.fromJson(httpResult.result, CheckStartPageDataResponse.class);
                    if(null != response && 0 == response.status) {
                        listener.success(response);
                        return;
                    }
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressLint("StaticFieldLeak")
    public static void loadStartPagePackage(final Context context, final String uuid, final String pkgId, final String orgCode, final String filePath, final String toZipPath, final MediaCenterNetManager.MediaDownloadListener downloadListener) {
        final String url = String.format(UrlConstantManager.getInstance().V2_getStartPagePackageUrl(), pkgId, AtworkConfig.DOMAIN_ID, orgCode);

        new AsyncTask<Void, Double, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                 HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                         DownloadFileParamsMaker.Companion.newRequest().setDownloadId(uuid).setDownloadUrl(url).setDownloadPath(filePath));

                try {
                    ZipUtil.upZipFile(AtWorkDirUtils.getInstance().getDataOrgDir(orgCode) + "startPageData.zip", toZipPath, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return httpResult;
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

    public interface OnCheckStartPagePackageListener extends NetWorkFailListener {
        void success(CheckStartPageDataResponse response);
        void failed();
    }
}
