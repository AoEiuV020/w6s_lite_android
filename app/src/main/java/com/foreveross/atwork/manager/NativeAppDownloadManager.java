package com.foreveross.atwork.manager;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.NativeApp;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.utils.FileHelper;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * 原生应用下载管理类
 *
 * 主要管理原生应用下载进度显示等，意在通过该类来刷新界面，而不是通知notifydatasetchange来频繁刷新listview
 */
public class NativeAppDownloadManager {

    private static final Object sLock = new Object();

    private static NativeAppDownloadManager sNativeAppDownloadManager;

    public Map<String, DownloadAppInfo> mDownLoadInfoMap = new Hashtable<>();

    public enum DownLoadStatus {
        /**
         * 尚未安装
         */
        STATUS_NOT_INSTALL,
        /**
         * 未安装状态下载中
         */
        STATUS_DOWNLOADING,
        /**
         * 已安装
         */
        STATUS_INSTALLED,
        /**
         * 已下载未安装
         */
        STATUS_DOWNLOADED_NOT_INSTALL

    }


    private NativeAppDownloadManager() {

    }

    public static NativeAppDownloadManager getInstance() {
        synchronized (sLock) {
            if (sNativeAppDownloadManager == null) {
                sNativeAppDownloadManager = new NativeAppDownloadManager();
            }
            return sNativeAppDownloadManager;
        }
    }

    public void updateDownloadAppInfo(String packageName, boolean isInstalled) {
        if (!MapUtil.isEmpty(mDownLoadInfoMap)) {
            for(DownloadAppInfo downloadAppInfo : mDownLoadInfoMap.values()) {
                if(packageName.equals(downloadAppInfo.packageName) && isAppDownloaded(downloadAppInfo)) {

                    if(isInstalled) {
                        downloadAppInfo.status = DownLoadStatus.STATUS_INSTALLED;
                    } else {
                        downloadAppInfo.status = DownLoadStatus.STATUS_DOWNLOADED_NOT_INSTALL;

                    }

                    break;
                }
            }
        }
    }

    public boolean isAppDownloaded(DownloadAppInfo downloadAppInfo) {
        return DownLoadStatus.STATUS_INSTALLED == downloadAppInfo.status || DownLoadStatus.STATUS_DOWNLOADED_NOT_INSTALL == downloadAppInfo.status;
    }

    /**
     *
     * @param appBundle
     * @param appContainer
     * @param nativeAppLayout
     * @param progressBar
     * @return
     */
	public void getDownloadButtonStatusAndUpdateProgress(Context context, AppBundles appBundle, RelativeLayout appContainer, RelativeLayout nativeAppLayout, TextView status, TextView progressBar) {
        //通过 view 打 tag 防止滑动 listview 时ui 混乱
        if(null != status.getTag() && !status.getTag().equals(appBundle.mBundleId)){
            return;
        }

		if (appBundle == null || mDownLoadInfoMap == null ) {
            appContainer.setEnabled(true);
            nativeAppLayout.setVisibility(View.VISIBLE);
            status.setText(context.getString(R.string.waiting_download));
            progressBar.setText("");
            return;
        }

        DownLoadStatus buttonStatus = DownLoadStatus.STATUS_NOT_INSTALL;

        if (!StringUtils.isEmpty(appBundle.mPackageId) && mDownLoadInfoMap.containsKey(appBundle.mBundleId)) {
			DownloadAppInfo downloadAppInfo = mDownLoadInfoMap.get(appBundle.mBundleId);
            buttonStatus = downloadAppInfo.status;
			switch (buttonStatus) {

                case STATUS_DOWNLOADED_NOT_INSTALL:
                    appContainer.setEnabled(true);
                    nativeAppLayout.setVisibility(View.VISIBLE);
                    status.setText(context.getString(R.string.install));
                    progressBar.setText("");
                    break;

                case STATUS_NOT_INSTALL:
                    appContainer.setEnabled(true);
                    nativeAppLayout.setVisibility(View.VISIBLE);
                    status.setText(context.getString(R.string.waiting_download));
                    progressBar.setText("");
                    break;
                case STATUS_INSTALLED:
                    appContainer.setEnabled(true);
                    nativeAppLayout.setVisibility(View.GONE);
                    break;

                case STATUS_DOWNLOADING:
                    appContainer.setEnabled(false);
                    nativeAppLayout.setVisibility(View.VISIBLE);
                    status.setText(context.getString(R.string.file_transfer_status_downloading));
                    DownloadAppInfo info = mDownLoadInfoMap.get(appBundle.mBundleId);
                    if (info == null) {
                        updateProgressBar(appBundle.mBundleName, progressBar,  0, DownLoadStatus.STATUS_DOWNLOADING);
                    } else {
                        updateProgressBar(appBundle.mBundleId, progressBar, info.currentSize, DownLoadStatus.STATUS_DOWNLOADING);
                    }

			}
		} else {
            appContainer.setEnabled(true);
			if (AtworkApplicationLike.getInstalledApps().contains(appBundle.mBundleId)) {
                nativeAppLayout.setVisibility(View.GONE);
			} else {
                nativeAppLayout.setVisibility(View.VISIBLE);
				progressBar.setText("");
                status.setText(context.getString(R.string.waiting_download));
			}
		}
	}

	public void updateProgressBar(String packageName, TextView progressBar, long currentSize, DownLoadStatus buttonStatus) {
        DownloadAppInfo info = mDownLoadInfoMap.get(packageName);
        info.currentSize = currentSize;
		if (buttonStatus == DownLoadStatus.STATUS_NOT_INSTALL) {
			progressBar.setText("");
		}else {
			if (progressBar.getVisibility() != View.VISIBLE) {
				progressBar.setVisibility(View.VISIBLE);
			}
            progressBar.setText(String.valueOf(currentSize) + "%");
		}
	}


	public String getProgressDesc(String buttonStatusDesc, NativeApp app) {
		DownloadAppInfo downloadAppInfo = mDownLoadInfoMap.get(app.mAppId);
		String result = null;
		if (downloadAppInfo != null) {
			String currentSize = FileHelper.getFileSizeStr(downloadAppInfo.currentSize);
			String totalSize = FileHelper.getFileSizeStr(downloadAppInfo.totalSize);
			result = buttonStatusDesc + " " + currentSize + "/" + totalSize;
		}
		return result;

	}


    /**
     * 启动下载
     */
    public void startDownload(Context context, AppBundles appBundle, DownloadAppInfo info,  MediaCenterNetManager.MediaDownloadListener listener) {
        MediaCenterNetManager service = new MediaCenterNetManager(context);
        MediaCenterNetManager.addMediaDownloadListener(listener);

        service.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(appBundle.mPackageId).setDownloadId(appBundle.mBundleId).setDownloadPath(info.iconReserveDir)
                        .setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
        );
    }

    /**
     * 取消下载
     * @param id
     */
    public void cancelDownload(long id) {

    }

    public static class DownloadAppInfo implements Serializable {
        public String iconUrl;//应用图标
        public String iconReserveDir;//本地存储路径
        public String packageName;
        public String id;
        public String name;
        public DownLoadStatus status;
        public long currentSize;
        public long totalSize;
        public String versionName;
        public String versionCode;

        public DownloadAppInfo(Context context, AppBundles appBundle) {
            if (appBundle == null) {
                return;
            }
            iconUrl = appBundle.mIcon;
            iconReserveDir = AtWorkDirUtils.getInstance().getAppUpgrade(LoginUserInfo.getInstance().getLoginUserUserName(context)) + "/" + appBundle.getNativeAppName();
            packageName = appBundle.mPackageName;
            id = appBundle.mBundleId;
            name = appBundle.getTitleI18n(context);
            status = toDownloadStatus(appBundle);
            currentSize = 0;
            totalSize = 0;
            versionName = appBundle.mPackageName;
            versionCode = appBundle.mPackageNo;
        }

        private DownLoadStatus toDownloadStatus(AppBundles appBundle) {
            switch (appBundle.mDownloadStatus) {
                case 0:
                    return DownLoadStatus.STATUS_NOT_INSTALL;

                case 1:
                    return DownLoadStatus.STATUS_DOWNLOADING;

                case 2:
                    return DownLoadStatus.STATUS_INSTALLED;
            }
            return DownLoadStatus.STATUS_NOT_INSTALL;
        }
    }
}
