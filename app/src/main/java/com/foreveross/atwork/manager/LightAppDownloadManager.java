package com.foreveross.atwork.manager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.fragment.AppFragment;
import com.foreveross.atwork.utils.IntentUtil;

import java.util.ArrayList;

public class LightAppDownloadManager {

    public static Boolean loadFailed = false;
    private static final Object sLock = new Object();

    private static LightAppDownloadManager sLightAppDownloadManager;

    public static ArrayList<DownLoadData> mRefreshAppIdList = new ArrayList<>();
    public static double HAVED_DOWNLOAD = 100;
    private long lastTime = 0;

    public class DownLoadData {
        private String mAppBundleId;
        private double progress;

        public String getAppBundleId() {
            return mAppBundleId;
        }

        public void setAppBundleId(String mAppBundleId) {
            this.mAppBundleId = mAppBundleId;
        }

        public double getProgress() {
            return progress;
        }

        public void setProgress(double progress) {
            this.progress = progress;
        }
    }


    private LightAppDownloadManager() {

    }

    public static LightAppDownloadManager getInstance() {
        synchronized (sLock) {
            if (sLightAppDownloadManager == null) {
                sLightAppDownloadManager = new LightAppDownloadManager();
            }
            return sLightAppDownloadManager;
        }
    }

    public void addDownloadApp(String bundleId){
        DownLoadData downLoadData = new DownLoadData();
        downLoadData.setAppBundleId(bundleId);
        downLoadData.setProgress(HAVED_DOWNLOAD);
        mRefreshAppIdList.add(downLoadData);
    }

    /**
     * @param appBundle
     * @param appContainer
     * @param lightAppLayout
     * @param progressBar
     * @return
     */
    public void getDownloadButtonStatusAndUpdateProgress(Context context, AppBundles appBundle, RelativeLayout appContainer, RelativeLayout lightAppLayout, TextView status, TextView progressBar) {
        //通过 view 打 tag 防止滑动 listview 时ui 混乱
        if (null != status.getTag() && !status.getTag().equals(appBundle.mBundleId)) {
            return;
        }

        appContainer.setEnabled(true);
        lightAppLayout.setVisibility(View.VISIBLE);
        status.setText(context.getString(R.string.file_transfer_status_downloading));
        progressBar.setText("");
    }

    /*
     * 启动下载
     */
    public void startDownload(Context context, AppBundles appBundle) {
        if(LightAppDownloadManager.mRefreshAppIdList.size()>0) {
            for(DownLoadData downLoadData : LightAppDownloadManager.mRefreshAppIdList){
                if(downLoadData.getAppBundleId().equals(appBundle.mBundleId)){
                    AppFragment.isPutUp = true;
                    return;
                }
            }
        }
        DownLoadData downLoadData = new DownLoadData();
        downLoadData.setAppBundleId(appBundle.mBundleId);
        downLoadData.setProgress(HAVED_DOWNLOAD);
        mRefreshAppIdList.add(downLoadData);
        loadFailed = false;

        AppFragment.isPutUp = true;
        DataPackageManager.loadData(context, appBundle, new DataPackageManager.OnLoadDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                for(DownLoadData downLoadData : mRefreshAppIdList){
                    if(appBundle.mBundleId.equals(downLoadData.getAppBundleId())){
                        mRefreshAppIdList.remove(downLoadData);
                    }
                }
                refreshLightApp(appBundle.mBundleId);
                if (AppFragment.isPutUp) {
                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setLightApp(appBundle).setTitle(appBundle.getTitleI18n(context)).setArticleItem(null);
                    context.startActivity(WebViewActivity.getIntent(context, webViewControlAction));
                }
            }

            @Override
            public void onError() {
                loadFailed = true;
                refreshLightApp(appBundle.mBundleId);
                for(DownLoadData downLoadData : mRefreshAppIdList){
                    if(appBundle.mBundleId.equals(downLoadData.getAppBundleId())){
                        mRefreshAppIdList.remove(downLoadData);
                    }
                }
                new AtworkAlertDialog(context, AtworkAlertDialog.Type.SIMPLE)
                        .setContent(R.string.offline_failed)
                        .setBrightBtnText(R.string.retry)
                        .setClickBrightColorListener(dialog -> {
                            IntentUtil.loadOfflineData(context, appBundle);
                        })
                        .show();
            }

            @Override
            public void downloadProgress(double progress, double size) {
                long nowTime = System.currentTimeMillis();
                //延迟发送广播，避免大量广播发送造成卡顿
                if(nowTime-lastTime > 500){
                    lastTime = nowTime;
                    if(LightAppDownloadManager.mRefreshAppIdList.size()>0) {
                        for(DownLoadData downLoadData1 : LightAppDownloadManager.mRefreshAppIdList){
                            if(downLoadData1.getAppBundleId().equals(appBundle.mBundleId)){
                                downLoadData1.setProgress(progress);
                                refreshLightApp(appBundle.mBundleId);
                            }
                        }
                    }
                }
            }
        });
    }

    private static void refreshLightApp(String appId) { //通知刷新结果
        Intent intent = new Intent(AppFragment.REFRESH_APP_NOTICE);
        intent.putExtra(AppFragment.REFRESH_APP, appId);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    /**
     * 启动下载
     */
    public void startDownload(Context context, AppBundles appBundle, DataPackageManager.OnLoadDataListener listener) {
        DataPackageManager.loadData(context, appBundle, new DataPackageManager.OnLoadDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onError() {
                listener.onError();
            }

            @Override
            public void downloadProgress(double progress, double size) {
                listener.downloadProgress(progress,size);
            }
        });
    }

    /**
     * 取消下载
     *
     * @param id
     */
    public void cancelDownload(long id) {

    }

    /*public static class DownloadAppInfo implements Serializable {
        public String iconUrl;//应用图标
        public String iconReserveDir;//本地存储路径
        public String packageName;
        public String id;
        public String name;
        public LightAppDownloadManager.DownLoadStatus status;
        public long currentSize;
        public long totalSize;
        public String versionName;
        public String versionCode;

        public DownloadAppInfo(Context context, LightApp app) {
            if (app == null) {
                return;
            }
            iconUrl = app.mBundles.get(0).mIcon;
            iconReserveDir = AtWorkDirUtils.getInstance().getAppUpgrade(LoginUserInfo.getInstance().getLoginUserUserName(context)) + "/" + app.getApkName();
            packageName = app.mPackageName;
            id = app.mAppId;
            name = app.getTitleI18n(context);
            status = toDownloadStatus(app);
            currentSize = 0;
            totalSize = 0;
            versionName = app.mPackageName;
            versionCode = app.mPackageNo;
        }

        private LightAppDownloadManager.DownLoadStatus toDownloadStatus(LightApp app) {
            switch (app.mDownloadStatus) {
                case 0:
                    return LightAppDownloadManager.DownLoadStatus.STATUS_NOT_INSTALL;

                case 1:
                    return LightAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADING;

                case 2:
                    return LightAppDownloadManager.DownLoadStatus.STATUS_INSTALLED;
            }
            return LightAppDownloadManager.DownLoadStatus.STATUS_NOT_INSTALL;
        }
    }*/
}
