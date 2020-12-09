package com.foreveross.atwork.modules.web.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.webview.WebkitSdkUtil;
import com.foreverht.webview.WebviewCoreFragment;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.SystemDownloadUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.manager.notification.DownloadNoticeManager;
import com.foreveross.atwork.modules.web.auth.CordovaInjectType;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.OfficeHelper;

import java.util.UUID;

/**
 * Created by dasunsy on 2018/3/22.
 */

public class WebviewFragment extends WebviewCoreFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDownloadListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @SuppressLint("StaticFieldLeak")
    private void startWorkplusDownload(String url, String contentDisposition, String mimetype) {
        if (AtworkConfig.WEBVIEW_CONFIG.isDownloadUseSystem()) {
            String cookie = WebkitSdkUtil.getCookies(getWebView(), url);
            SystemDownloadUtil.download(BaseApplicationLike.baseContext, url, contentDisposition, mimetype, cookie);

            return;
        }


        if(AtworkConfig.WEBVIEW_CONFIG.isDownloadUseNotification()) {
            handleWorkplusNotificationDownload(url, contentDisposition, mimetype);
            return;
        }

        handleWorkplusSelfDownload(url, contentDisposition, mimetype);
    }

    private void handleWorkplusNotificationDownload(String url, String contentDisposition, String mimetype) {
        FileUtil.FileInfo fileInfo = SystemDownloadUtil.getFileInfoFromDownloadUrlChecking(BaseApplicationLike.baseContext, url, contentDisposition, mimetype);

        final String filePath = fileInfo.filePath;
        if (FileUtil.isExist(filePath)) {
            DownloadNoticeManager.INSTANCE.previewDownloadFileOnFileStatusView(filePath);
            return;
        }


        MediaCenterNetManager.brokenDownloadingOrUploading(url, () -> new Handler().postDelayed(()-> doHandleWorkplusNotificationDownload(url, filePath), 1000));



    }

    private void doHandleWorkplusNotificationDownload(String url, String filePath) {
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {


            boolean downloadHasResult = false;

            @Override
            public String getMsgId() {
                return url;
            }

            @Override
            public void downloadSuccess() {

                LogUtil.e("doHandleWorkplusNotificationDownload -> downloadSuccess");
//                DownloadNoticeManager.INSTANCE.clear(getMsgId().hashCode());

                if (AtworkConfig.WEBVIEW_CONFIG.isOpenFileAfterDownloaded()) {
                    DownloadNoticeManager.INSTANCE.previewDownloadFileOnFileStatusView(filePath);
                }


                downloadHasResult = true;

                AtworkToast.showResToast(R.string.file_transfer_status_download_success);

                DownloadNoticeManager.INSTANCE.notifyNotificationSuccess(BaseApplicationLike.baseContext, getMsgId().hashCode(), filePath, url);

                MediaCenterNetManager.removeMediaDownloadListener(this);

            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {

                LogUtil.e("doHandleWorkplusNotificationDownload -> downloadFailed");

                downloadHasResult = true;

                if (-99 != errorCode) {
                    AtworkToast.showResToast(R.string.file_transfer_status_download_fail);
                }

                DownloadNoticeManager.INSTANCE.clear(getMsgId().hashCode());
                MediaCenterNetManager.removeMediaDownloadListener(this);
            }

            @Override
            public void downloadProgress(double progress, double value) {
                int progressInt = (int) progress;
                LogUtil.e("doHandleWorkplusNotificationDownload -> progressInt : " + progressInt);

                if (!downloadHasResult) {
                    if(CommonUtil.isFastClick("webDownload", 1000)) {
                        return;
                    }

                    DownloadNoticeManager.INSTANCE.notifyNotificationProgress(BaseApplicationLike.baseContext, getMsgId().hashCode(), 100, progressInt, filePath);
                }
            }
        });


        AtworkToast.showResToast(R.string.start_downloading_file);
        DownloadNoticeManager.INSTANCE.notifyNotificationProgress(BaseApplicationLike.baseContext, url.hashCode(), 100, 0, filePath);


        String cookie = WebkitSdkUtil.getCookies(getWebView(), url);

        new MediaCenterNetManager(BaseApplicationLike.baseContext).downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setDownloadId(url).setMediaId(url).setDownloadPath(filePath)
                        .setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
        );
    }

    @SuppressLint("StaticFieldLeak")
    private void handleWorkplusSelfDownload(String url, String contentDisposition, String mimetype) {
        if(!isAdded()) {
            return;
        }

//        String path = AtWorkDirUtils.getInstance().getFiles(BaseApplicationLike.baseContext) + SystemDownloadUtil.getNameFromDownloadUrl(url, contentDisposition, mimetype);
        String path = SystemDownloadUtil.getFilePathFromDownloadUrlChecking(BaseApplicationLike.baseContext, url, contentDisposition, mimetype);
        if (FileUtil.isExist(path)) {

            previewDownloadedFile(path);

            return;
        }


        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
        progressDialogHelper.show();

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                String cookie = WebkitSdkUtil.getCookies(getWebView(), url);

                HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                        DownloadFileParamsMaker.Companion.newRequest().setDownloadId(UUID.randomUUID().toString()).setDownloadUrl(url)
                                .setDownloadPath(path).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
                                .setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION).setCookie(cookie)
                );
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                progressDialogHelper.dismiss();

                if (httpResult.isNetSuccess()) {

                    previewDownloadedFile(path);

                } else {
                    AtworkToast.showResToast(R.string.file_transfer_status_download_fail);
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    private void previewDownloadedFile(String path) {
        AtworkToast.showResToast(R.string.file_transfer_status_download_success);

        if (OfficeHelper.isSupportType(path)) {
            EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false, fileName -> {
                OfficeHelper.previewByX5(BaseApplicationLike.baseContext, fileName);

            });

        } else {


            EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false, fileName -> {
                FileData.FileType fileType = FileData.getFileType(fileName);
                IntentUtil.previewIntent(BaseApplicationLike.baseContext, fileName, fileType.getFileType());
            });

        }
    }


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        startWorkplusDownload(url, contentDisposition, mimetype);
    }

    @Override
    public void setCmdFinishCheckNoGoBack(boolean cmd) {

    }
}
