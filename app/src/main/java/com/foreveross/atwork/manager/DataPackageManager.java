package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.OrgCommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.infrastructure.utils.ZipUtil;
import com.foreveross.atwork.infrastructure.utils.aliyun.CdnHelper;
import com.foreveross.atwork.modules.app.manager.AppManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by dasunsy on 2017/1/23.
 */


public class DataPackageManager {

    private  static final String ENV_KEY_ONLY_OFFICE_PREDOWNLOAD_URL = "ONLY_OFFICE_PREDOWNLOAD_URL";
    private  static final String ENV_KEY_ONLY_OFFICE_DOWNLOAD_MD5 = "ONLY_OFFICE_DOWNLOAD_MD5";
    private static final String ENV_KEY_ONLY_OFFICE_BASE_URL = "ONLY_OFFICE_BASE_URL";
    private static long lastCheckCacheTime = 0L;
    private static long checkTimeRange = 5 * 60 * 1000;

    @SuppressLint("StaticFieldLeak")
    public static void loadData(Context context, AppBundles appBundle, OnLoadDataListener onLoadDataListener) {
        onLoadDataListener.onStart();

        new AsyncTask<Void, Double, Boolean>() {


            @Override
            protected Boolean doInBackground(Void[] params) {


                String currentOrg = PersonalShareInfo.getInstance().getCurrentOrg(context);
                String lightAppOfflineDataOrgDir = AtWorkDirUtils.getInstance().getLightAppOfflineDataOrgDir(currentOrg, appBundle.mBundleId);
                String zipPath = lightAppOfflineDataOrgDir + appBundle.mSettings.mMobileBehaviour.mRelease + ".zip";
                String releasePath = UrlHandleHelper.getReleasePath(context, appBundle);

                Boolean result = false;

                if(FileUtil.isExist(zipPath)) {

                    try {
                        ZipUtil.upZipFile(zipPath, releasePath, true);

                        OrgCommonShareInfo.setLightappOfflineReleaseUnzipDigest(context, appBundle, AppManager.getInstance().getOfflinePackageDigest(releasePath));

                        result = true;
                    } catch (IOException e) {
                        e.printStackTrace();


                    }


                } else {
                    String downloadUrl = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), appBundle.mSettings.mMobileBehaviour.mRelease, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
                    downloadUrl = CdnHelper.wrapCdnUrl(downloadUrl);
                    String tempZipPath = AtWorkDirUtils.getInstance().getDataOrgDir(currentOrg) + appBundle.mSettings.mMobileBehaviour.mRelease + ".zip";

                    HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                            DownloadFileParamsMaker.Companion.newRequest().setDownloadId(UUID.randomUUID().toString()).setDownloadUrl(downloadUrl).setDownloadPath(tempZipPath).setEncrypt(false).setProgressListener(
                                    (value, size) -> publishProgress(value, size)
                            )
                    );

                    if(httpResult.isNetSuccess()) {
                        try {
                            //clean old version data
                            FileUtil.deleteFile(lightAppOfflineDataOrgDir, true);

                            File file = new File(lightAppOfflineDataOrgDir);
                            file.mkdirs();

                            FileUtil.copyFile(tempZipPath, zipPath);

                            FileUtil.deleteFile(tempZipPath, true);

                            ZipUtil.upZipFile(zipPath, releasePath, true);
                            result = true;


                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }


                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    onLoadDataListener.onSuccess();

                } else {
                    onLoadDataListener.onError();
                }
            }

            @Override
            protected void onProgressUpdate(Double... values) {
                super.onProgressUpdate(values);
                onLoadDataListener.downloadProgress(values[0],values[1]);
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static void checkWebCachePreDownload(Context context) {
        String downloadUrl = DomainSettingsManager.getInstance().getEnvValue(ENV_KEY_ONLY_OFFICE_PREDOWNLOAD_URL);
        String downloadMd5 = DomainSettingsManager.getInstance().getEnvValue(ENV_KEY_ONLY_OFFICE_DOWNLOAD_MD5);
        if (TextUtils.isEmpty(downloadUrl) || TextUtils.isEmpty(downloadMd5)) {
            return;
        }
        String releasePath = AtWorkDirUtils.getInstance().getWebCacheDir() + File.separator;
        File md5Dir = new File(releasePath + downloadMd5);
        if (md5Dir.exists() && md5Dir.isDirectory()) {
            return;
        }
        if (System.currentTimeMillis() - lastCheckCacheTime < checkTimeRange) {
            return;
        }
        if (MediaCenterHttpURLConnectionUtil.downloadHttpURLConnection.containsKey(downloadMd5)) {
            return;
        }
        String zipPath = AtWorkDirUtils.getInstance().getWebCacheDir() + File.separator + downloadMd5 + ".zip";

        loadData(context, downloadMd5, downloadUrl, zipPath, releasePath, null);
    }

    private static void loadData(Context context, String downloadId,  String downloadUrl, String zipPath, String releasePath, OnLoadDataListener onLoadDataListener) {
        if (onLoadDataListener != null) {
            onLoadDataListener.onStart();
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void[] params) {
                boolean result = false;

                if(FileUtil.isExist(zipPath)) {
                    try {
                        ZipUtil.upZipFile(zipPath, releasePath, false);
                        result = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        FileUtil.delete(zipPath);
                    }
                } else {
                    String tempZipPath = zipPath + ".tmp";

                    HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                            DownloadFileParamsMaker.Companion.newRequest().setDownloadUrl(downloadUrl).setDownloadId(downloadId).setDownloadPath(tempZipPath)
                    );
                    if(httpResult.isNetSuccess()) {
                        try {
                            FileUtil.copyFile(tempZipPath, zipPath);
                            FileUtil.deleteFile(tempZipPath, false);
                            ZipUtil.upZipFile(zipPath, releasePath, false);
                            FileUtil.deleteFile(zipPath, false);
                            result = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    lastCheckCacheTime = System.currentTimeMillis();
                }

                if (onLoadDataListener == null) {
                    return;
                }
                if(result) {
                    onLoadDataListener.onSuccess();
                    return;
                }
                onLoadDataListener.onError();
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    public interface OnLoadDataListener {
        void onStart();
        void onSuccess();
        void onError();
        /**
         * 下载进度
         *
         * @param progress
         */
        void downloadProgress(double progress, double size);
    }
}
