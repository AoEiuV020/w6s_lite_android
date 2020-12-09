package com.foreveross.atwork.modules.app.component;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.AppAsyncNetService;
import com.foreveross.atwork.api.sdk.app.model.InstallOrDeleteAppJSON;
import com.foreveross.atwork.api.sdk.app.model.InstallOrRemoveAppResponseJson;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LocalApp;
import com.foreveross.atwork.infrastructure.model.app.NativeApp;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleType;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.DataPackageManager;
import com.foreveross.atwork.manager.LightAppDownloadManager;
import com.foreveross.atwork.manager.NativeAppDownloadManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.app.fragment.AppFragment;
import com.foreveross.atwork.modules.app.inter.AppRemoveListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.atwork.modules.app.util.AppHelper;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.common.component.LightNoticeItemView;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.TabHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AppIconHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lingen on 15/5/8.
 */
public class AppItemView extends RelativeLayout {

    private View mVIconRoot;
    private ImageView mAppIconView;

    private TextView mAppNameView;

    private AppBundles mAppBundle;

    private ImageView mDeleteView;

    private ImageView mIvSessionSomeStatus;

    private ImageView mIvAppNewVersionNotice;

    private boolean mIsDeleteMode;

    private boolean mCanClick = true;

    private AppRemoveListener mAppRemoveListener;

    private GroupAppItem mGroupAppItem;

    private Activity mActivity;

    //--------------原生应用-------------
    private RelativeLayout mAppContainer;
    public RelativeLayout mDownloadLayout;
    private TextView mDownLoadProgress;
    private TextView mDownLoadStatus;

    private BackHandledFragment.OnK9MailClickListener mOnK9MailClickListener;

    private LightNoticeItemView mNoticeView;

    public AppItemView(Activity context) {
        super(context);
        initView();
        registerListener();
        initData();
        mActivity = context;
    }

    private void initData() {
    }

    private void registerListener() {

        mDeleteView.setOnClickListener(v -> {
            if (mAppBundle.mShortcut) {
                AtworkToast.showToast(mActivity.getString(R.string.app_is_enforce));
                return;
            }
            final AtworkAlertDialog alertDialog = new AtworkAlertDialog(mActivity, AtworkAlertDialog.Type.CLASSIC);
            alertDialog.setTitleText(R.string.remove_app);
            alertDialog.setContent(R.string.remove_app_submit);
            alertDialog.setCancelable(true);
            alertDialog.setClickDeadColorListener(dialog -> {
                alertDialog.dismiss();
            });
            alertDialog.setClickBrightColorListener(dialog -> {
                if (mAppBundle.isNativeAppBundle() && BundleType.Native.equals(mAppBundle.mBundleType)) {
                    //首先判断安装列表里面是否安装了该应用
                    if (AppHelper.isAppInstalled(getContext(), mAppBundle.mPackageId)) {
                        mAppRemoveListener.getNativeAppRemoveFlagHashTable().put(mAppBundle.mPackageId, mAppBundle);
                        AppUtil.unInstallApp(mActivity, mAppBundle.mPackageId);
                        return;
                    }
                }

                List<String> appList = new ArrayList<>();
                appList.add(mAppBundle.mBundleId);
                InstallOrDeleteAppJSON.AppEntrances entrance = new InstallOrDeleteAppJSON.AppEntrances();
                entrance.mAppId = mAppBundle.appId;
                entrance.mEntries = appList;
                AppAsyncNetService appAsyncNetService = new AppAsyncNetService(mActivity);
                List<InstallOrDeleteAppJSON.AppEntrances> entrances = new ArrayList<>();
                entrances.add(entrance);
                appAsyncNetService.asyncInstallOrRemoveAppFromRemote(mActivity, PersonalShareInfo.getInstance().getCurrentOrg(mActivity), entrances, false, true,
                        new AppAsyncNetService.AddOrRemoveAppListener() {
                            @Override
                            public void addOrRemoveSuccess(InstallOrRemoveAppResponseJson json) {
                                updateAppDB(appList);
                            }

                            @Override
                            public void networkFail(int errorCode, String errorMsg) {
                                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.App, errorCode, errorMsg);
                            }
                        });

            });
            alertDialog.show();

        });

        setOnLongClickListener(v -> {
            if (mAppRemoveListener != null) {
                mAppRemoveListener.removeMode(!mIsDeleteMode);
            }
            return true;
        });

        setOnClickListener(v -> {

            if(!mCanClick) {
                return;
            }

            if (mIsDeleteMode) {
                return;
            }

            if (CommonUtil.isFastClick(500)) {
                return;
            }

            if(LightAppDownloadManager.mRefreshAppIdList.size()>0) {
                for(LightAppDownloadManager.DownLoadData downLoadData : LightAppDownloadManager.mRefreshAppIdList){
                    if(mAppBundle.mBundleId.equals(downLoadData.getAppBundleId())){
                        return;
                    }
                }
            }
            doHandleClickApp();
        });


    }

    private void doHandleClickApp() {
        if (mAppBundle.isLightAppBundle()) {
            handleLightAppClick();
            return;
        }

        if (mAppBundle.isNativeAppBundle()) {
            if (LocalApp.EMAIL_PREFIX.equalsIgnoreCase(mAppBundle.mTargetUrl)) {
                handleK9MailClick();
                return;
            }
            if (BundleType.System.equals(mAppBundle.mBundleType)) {
                handleSystemAppClick();
                return;
            }
            AndPermission
                    .with(mActivity)
                    .runtime()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                    .onGranted(data -> {
                        handleNativeAppClick();
                    })
                    .onDenied(data -> AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .start();

        }

        if (mAppBundle.isServiceAppBundle()) {
            handleServiceAppClick();
            return;
        }
    }

    private void handleSystemAppClick() {
        IntentUtil.startSystemApp(getContext(), mAppBundle.mTargetUrl);
    }

    private void handleServiceAppClick() {
        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Service, mAppBundle).setOrgId(mAppBundle.mOrgId);
        ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);
        Intent intent = ChatDetailActivity.getIntent(getContext(), mAppBundle.appId);
        intent.putExtra(ChatDetailFragment.APP_BUNDLE_ID, mAppBundle.mBundleId);
        intent.putExtra(ChatDetailFragment.RETURN_BACK, true);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void handleNativeAppClick() {
        if (TextUtils.isEmpty(mAppBundle.mPackageName)) {
            AtworkToast.showToast("不正确的原生应用，未指定包名");
            return;
        }

        if (!AtworkApplicationLike.getInstalledApps().contains(mAppBundle.mPackageName) && TextUtils.isEmpty(mAppBundle.mPackageId)) {
            AtworkToast.showToast("未安装且未指定下载地址");
            return;
        }
        if (!AtworkApplicationLike.getInstalledApps().contains(mAppBundle.mPackageName) && !TextUtils.isEmpty(mAppBundle.mPackageId)) {

            String[] files = AtworkUtil.getNativeAppFiles(mActivity);
            String apkName = mAppBundle.getNativeAppName();
            if (files != null && Arrays.asList(files).contains(apkName)) {
                String appPath = AtWorkDirUtils.getInstance().getAppUpgrade(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)) + "/" + apkName;
                EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(appPath, false, fileName -> IntentUtil.installApk(getContext(), fileName));
                return;
            }
            if (mAppBundle.mDownloadStatus == NativeApp.DownLoadStatus.DOWNLOADING) {
                return;
            }
            final NativeAppDownloadManager manager = NativeAppDownloadManager.getInstance();
            final NativeAppDownloadManager.DownloadAppInfo appInfo = new NativeAppDownloadManager.DownloadAppInfo(mActivity, mAppBundle);
            mAppBundle.mDownloadStatus = NativeApp.DownLoadStatus.DOWNLOADING;
            appInfo.status = NativeAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADING;
            manager.mDownLoadInfoMap.put(mAppBundle.mBundleId, appInfo);
            manager.getDownloadButtonStatusAndUpdateProgress(getContext(), mAppBundle, mAppContainer, mDownloadLayout, mDownLoadStatus, mDownLoadProgress);

            manager.startDownload(getContext(), mAppBundle, appInfo, new MediaCenterNetManager.MediaDownloadListener() {
                @Override
                public String getMsgId() {
                    return mAppBundle.mBundleId;
                }

                @Override
                public void downloadSuccess() {
                    appInfo.status = NativeAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADED_NOT_INSTALL;
                    manager.getDownloadButtonStatusAndUpdateProgress(getContext(), mAppBundle, mAppContainer, mDownloadLayout, mDownLoadStatus, mDownLoadProgress);
                }

                @Override
                public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                    mAppBundle.mDownloadStatus = NativeApp.DownLoadStatus.UNINSTALL;
                    appInfo.status = NativeAppDownloadManager.DownLoadStatus.STATUS_NOT_INSTALL;
                    manager.getDownloadButtonStatusAndUpdateProgress(getContext(), mAppBundle, mAppContainer, mDownloadLayout, mDownLoadStatus, mDownLoadProgress);
                    if (errorCode == -99 && errorMsg == null) {
                        return;
                    }
                    AtworkToast.showToast(getContext().getString(R.string.download_native_app_fail));
                }

                @Override
                public void downloadProgress(double progress, double value) {
                    manager.updateProgressBar(mAppBundle.mBundleId, mDownLoadProgress, (long) progress, NativeAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADING);
                }
            });

            return;
        }

        IntentUtil.startApp(getContext(), mAppBundle.mPackageName, true, mAppBundle);
        return;
    }

    private void handleLightAppClick() {
        String title = mAppBundle.getTitleI18n(mActivity);
        lightAppDownload(title);
    }

    private void lightAppDownload(String title) {
        if (AppManager.getInstance().useOfflinePackageNeedReLoad(mAppBundle)) {
            AppFragment.isPutUp = true;
            LightAppDownloadManager lightAppDownloadManager = LightAppDownloadManager.getInstance();
            lightAppDownloadManager.addDownloadApp(mAppBundle.mBundleId);
            lightAppDownloadManager.getDownloadButtonStatusAndUpdateProgress(mActivity, mAppBundle, mAppContainer, mDownloadLayout, mDownLoadStatus, mDownLoadProgress);
            lightAppDownloadManager.startDownload(mActivity, mAppBundle, new DataPackageManager.OnLoadDataListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess() {
                    for(LightAppDownloadManager.DownLoadData downLoadData : LightAppDownloadManager.mRefreshAppIdList){
                        if(mAppBundle.mBundleId.equals(downLoadData.getAppBundleId())){
                            LightAppDownloadManager.mRefreshAppIdList.remove(downLoadData);
                        }
                    }
                    mDownloadLayout.setVisibility(View.GONE);
                    if (AppFragment.isPutUp) {
                        WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setLightApp(mAppBundle).setTitle(title).setArticleItem(null);
                        mActivity.startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
                    }
                }

                @Override
                public void onError() {
                    for(LightAppDownloadManager.DownLoadData downLoadData : LightAppDownloadManager.mRefreshAppIdList){
                        if(mAppBundle.mBundleId.equals(downLoadData.getAppBundleId())){
                            LightAppDownloadManager.mRefreshAppIdList.remove(downLoadData);
                        }
                    }
                    mDownloadLayout.setVisibility(View.GONE);
                    new AtworkAlertDialog(mActivity, AtworkAlertDialog.Type.SIMPLE)
                            .setContent(R.string.offline_failed)
                            .setBrightBtnText(R.string.retry)
                            .setClickBrightColorListener(dialog -> {
                                lightAppDownload(title);
                            })
                            .show();
                }

                @Override
                public void downloadProgress(double progress, double size) {
                    if(progress == LightAppDownloadManager.HAVED_DOWNLOAD){
                        mDownloadLayout.setVisibility(View.GONE);
                    }else {
                        if (mDownLoadProgress.getVisibility() != View.VISIBLE) {
                            mDownLoadProgress.setVisibility(View.VISIBLE);
                        }
                        mDownLoadProgress.setText(String.valueOf(progress) + "%");
                    }
                }
            });
        } else {
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setLightApp(mAppBundle).setTitle(title).setArticleItem(null);
            mActivity.startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
        }
    }

    private void handleK9MailClick() {
        if (mOnK9MailClickListener != null) {
            mOnK9MailClickListener.onK9MailClick(mAppBundle.appId);
            Session session = ChatSessionDataWrap.getInstance().getSession(mAppBundle.appId, null);
            if (session != null) {
                ChatSessionDataWrap.getInstance().emptySessionUnread(mActivity, session);
            }
        }
    }



    public void updateAppDB(List<String> deleteList) {
        if (ListUtil.isEmpty(deleteList)) {
            return;
        }
        String deletedId = deleteList.get(0);
        AppDaoService.getInstance().updateAppBundle(mAppBundle.appId, deletedId, new AppDaoService.AddOrRemoveAppListener() {
            @Override
            public void addOrRemoveSuccess() {
                mAppRemoveListener.removeComplete(mGroupAppItem, mAppBundle);
//                ChatDaoService.getInstance().syncRemoveSession(mAppBundle.appId, true);
                ChatSessionDataWrap.getInstance().removeSessionSafely(mAppBundle.appId);
            }

            @Override
            public void addOrRemoveFail() {
                AtworkToast.showToast(getResources().getString(R.string.remove_app_fail));
            }
        });
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_grid_apps_child, this);
        mVIconRoot = view.findViewById(R.id.v_icon_bg);
        mAppIconView = view.findViewById(R.id.app_icon);
        mAppNameView = view.findViewById(R.id.app_name);
        mDeleteView = view.findViewById(R.id.app_remove);
        mIvAppNewVersionNotice = view.findViewById(R.id.iv_app_new_version_notice);
        mIvSessionSomeStatus = view.findViewById(R.id.iv_bio_auth_protected);
        mAppContainer = view.findViewById(R.id.app_container);
        mDownloadLayout = view.findViewById(R.id.native_app_background);
        mDownLoadProgress = view.findViewById(R.id.native_app_progress);
        mDownLoadStatus = view.findViewById(R.id.native_app_status);
        mNoticeView = view.findViewById(R.id.app_item_view);
    }

    public void refreshView(GroupAppItem groupAppItem, final AppBundles appBundle, boolean deleteMode, boolean canClick) {
        boolean avatarNeedLoading = isAvatarNeedLoading(appBundle);
        this.mIsDeleteMode = deleteMode;
        this.mCanClick = canClick;
        this.mAppBundle = appBundle;
        this.mGroupAppItem = groupAppItem;

        mAppNameView.setText(appBundle.getTitleI18n(BaseApplicationLike.baseContext));
        mIvSessionSomeStatus.setVisibility(GONE);

        if (appBundle.isNativeAppBundle() && BundleType.Native.equals(appBundle.mBundleType)) {
            mDownLoadStatus.setTag(appBundle.mBundleId);
            NativeAppDownloadManager manager = NativeAppDownloadManager.getInstance();
            manager.getDownloadButtonStatusAndUpdateProgress(getContext(), appBundle, mAppContainer, mDownloadLayout, mDownLoadStatus, mDownLoadProgress);
        } else {
            if(LightAppDownloadManager.mRefreshAppIdList.size() > 0 && !LightAppDownloadManager.loadFailed) {
                for(LightAppDownloadManager.DownLoadData downLoadData : LightAppDownloadManager.mRefreshAppIdList){
                    if(mAppBundle.mBundleId.equals(downLoadData.getAppBundleId()) && mAppBundle.isLightAppBundle()){
                        LightAppDownloadManager.getInstance().getDownloadButtonStatusAndUpdateProgress(mActivity, mAppBundle, mAppContainer, mDownloadLayout, mDownLoadStatus, mDownLoadProgress);
                        if(downLoadData.getProgress() != LightAppDownloadManager.HAVED_DOWNLOAD){
                            if (mDownLoadProgress.getVisibility() != View.VISIBLE) {
                                mDownLoadProgress.setVisibility(View.VISIBLE);
                            }
                            mDownLoadProgress.setText(downLoadData.getProgress() + "%");
                        }
                    }
                }
            }else{
                mDownloadLayout.setVisibility(GONE);
            }
        }


        handleRightTopConnerView(appBundle);

        if (mAppBundle.isLightAppBundle()) {
            LightNoticeData lightNoticeJson = TabNoticeManager.getInstance().getLightNoticeDataInRange(TabHelper.getAppFragmentId(), appBundle.mBundleId);
            refreshLightNotice(lightNoticeJson);
        }

        AppIconHelper.setAppIcon(getContext(), appBundle, mAppIconView, avatarNeedLoading);

    }

    private void handleRightTopConnerView(AppBundles appBundle) {
        if (mIsDeleteMode) {
            mDeleteView.setVisibility(VISIBLE);
            mNoticeView.setVisibility(GONE);
            return;
        }

        mDeleteView.setVisibility(GONE);

        if (appBundle.isLightAppBundle()) {
            LightNoticeData lightNoticeJson = TabNoticeManager.getInstance().getLightNoticeDataInRange(TabHelper.getAppFragmentId(), appBundle.mBundleId);
            if (null != lightNoticeJson && !lightNoticeJson.isNothing()) {
                mNoticeView.refreshLightNotice(lightNoticeJson);
                mNoticeView.setVisibility(VISIBLE);
                return;
            }
        }


        if (appBundle.mNewVersionNotice) {
//            LightNoticeData lightNoticeJson = LightNoticeData.createDotLightNotice();
//            mNoticeView.refreshLightNotice(lightNoticeJson);
//            mNoticeView.setVisibility(VISIBLE);
//            return;
        }


        mNoticeView.setVisibility(GONE);

    }

    private boolean isAvatarNeedLoading(AppBundles app) {
        boolean avatarNeedLoading = true;
        if (null != this.mAppBundle && this.mAppBundle.equals(app)) {
            avatarNeedLoading = false;
        }
        return avatarNeedLoading;
    }


    private void refreshLightNotice(@Nullable LightNoticeData json) {
        if (null != json) {
            mNoticeView.refreshLightNotice(json);
        }

        //refresh ui
        if (!mIsDeleteMode && null != json) {
            mNoticeView.setVisibility(VISIBLE);
        } else {
            mNoticeView.setVisibility(GONE);

        }
    }

    public void setAppRemoveListener(AppRemoveListener appRemoveListener) {
        this.mAppRemoveListener = appRemoveListener;
    }

    public void setOnK9MailClickListener(BackHandledFragment.OnK9MailClickListener listener) {
        mOnK9MailClickListener = listener;
    }

}
