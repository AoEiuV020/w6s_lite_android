package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.setting.DynamicPropertiesAsyncNetService;
import com.foreveross.atwork.api.sdk.setting.DynamicPropertiesSyncNetService;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.domain.AppVersions;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.model.domain.GlobalSettings;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.DomainSettingInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.modules.aboutme.fragment.AboutMeFragment;
import com.google.gson.Gson;

import static com.foreveross.atwork.infrastructure.manager.DomainSettingsManager.DOMAIN_SETTINGS_CHANGE;

/**
 * Created by reyzhang22 on 17/9/26.
 */

public class DomainSettingsHelper {

    public static final DomainSettingsHelper sInstance = new DomainSettingsHelper();

    public static DomainSettingsHelper getInstance() {
        return sInstance;
    }

    /**
     * 请求服务器api获取最新的域设置信息
     */
    public void getDomainSettingsFromRemote(Context context, boolean silentMode, @Nullable final DynamicPropertiesAsyncNetService.OnDomainSettingsListener listener) {
        getDomainSettingAsync(context, AppUtil.getPackageName(AtworkApplicationLike.baseContext), silentMode, listener);
    }


    @SuppressLint("StaticFieldLeak")
    private void getDomainSettingAsync(final Context context, final String pkgName, final boolean silentMode, @Nullable final DynamicPropertiesAsyncNetService.OnDomainSettingsListener listener) {
        new AsyncTask<Void, Void, GlobalSettings>() {
            @Override
            protected GlobalSettings doInBackground(Void... params) {
                HttpResult httpResult = DynamicPropertiesSyncNetService.getInstance().getDomainSettings(context, pkgName);
                GlobalSettings globalSettings = null;
                if (httpResult != null) {
                    String resultText = NetWorkHttpResultHelper.getResultText(httpResult.result);
                    if (!TextUtils.isEmpty(resultText)) {
                        globalSettings = new Gson().fromJson(resultText, GlobalSettings.class);
                    }
                }
                return globalSettings;
            }

            @Override
            protected void onPostExecute(GlobalSettings globalSettings) {

                if (globalSettings != null) {
                    notifyAppUpdate(context, globalSettings.mAppVersions, silentMode);

                    if (null != globalSettings.mAppProfile) {
                        DomainSettingInfo.getInstance().setAppProfileData(context, new Gson().toJson(globalSettings.mAppProfile));
                        BaseApplicationLike.sAppProfile = globalSettings.mAppProfile;
                    }

                    if(null != globalSettings.mDomainSettings) {

                        //对比本地, 看是否需要推送更新
                        if(!(globalSettings.mDomainSettings.equals(DomainSettingInfo.getInstance().getDomainSettingsData(BaseApplicationLike.baseContext)))) {
                            notifyDomainSettingsChange(context, globalSettings.mDomainSettings);

                        }


                        if (null != listener) {
                            listener.onDomainSettingsCallback(globalSettings.mDomainSettings);
                        }

                        return;
                    }
                }

                if (null != listener) {
                    listener.onDomainSettingsFail();
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private void notifyDomainSettingsChange(Context context, DomainSettings domainSettings) {
        DomainSettingInfo.getInstance().clearDomainSettingsData(context);
        DomainSettingInfo.getInstance().setDomainSettingsData(context, new Gson().toJson(domainSettings));
        BaseApplicationLike.sDomainSettings = domainSettings;

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DOMAIN_SETTINGS_CHANGE));
    }


    private void notifyAppUpdate(Context context, @Nullable AppVersions appVersions, boolean silentMode) {

        if (appVersions == null) {
            CommonShareInfo.clearNewVersionCode(context);
            CommonShareInfo.setForcedUpdatedState(context, false);

        } else {
            int nowVersionCode = AppUtil.getVersionCode(context);
            int remoteBuildNo = appVersions.mBuildNo;
            String remoteRelease = appVersions.mRelease;
            boolean forcedUpdate = appVersions.mForcedUpdate;
            if (remoteBuildNo > nowVersionCode) {
                //将当前可更新的最新版本信息写入本地文件
                CommonShareInfo.setNewVersionCode(context, remoteBuildNo);
                CommonShareInfo.setNewVersionName(context, remoteRelease);

                CommonShareInfo.setForcedUpdatedState(context, forcedUpdate);


            } else {
                CommonShareInfo.clearNewVersionCode(context);
                CommonShareInfo.setForcedUpdatedState(context, false);

            }

        }

        Intent intent = new Intent(AtworkConstants.ACTION_RECEIVE_APP_UPDATE);
        intent.putExtra(DynamicPropertiesAsyncNetService.ACTION_INTENT_UPDATE_EXTRA, appVersions);
        intent.putExtra(DynamicPropertiesAsyncNetService.DATA_INTENT_SILENT_UPDATE, silentMode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(new Intent(AboutMeFragment.ACTION_CHECK_UPDATE_NOTICE));
    }


}
