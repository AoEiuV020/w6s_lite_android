package com.foreveross.atwork.infrastructure;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.domain.AppProfile;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.shared.DomainSettingInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;

public abstract class BaseApplicationLike extends MultiDexApplication {
    public static String ACTION_CLEAR_EMAIL_UNREAD = "action_clear_email_unread";

    public static boolean sIsLock = false;

    public static boolean sIsHomeStatus = false;

    private static Handler sHandler = null;

    public static Context baseContext;
    public static Application sApp;
    public static BaseApplicationLike sAppLike;
    public static DomainSettings sDomainSettings;
    public static AppProfile sAppProfile;
    public static String sOrgId;

    public static Activity sMainActivity;

    public static boolean sIsDebug = false;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppLike = this;
        makeEmailContentURI();
    }

    public static Uri EMAIL_ATTACHMENT_CONTENT_URI;
    public static String EIM_EMAIL_AUTHORITY;
    public static Uri EIM_EMAIL_CONTENT_URI;
    public static String EIM_MESSAGE_AUTHORITY ;
    public static Uri EIM_MESSAGE_CONTENT_URI;
    private void makeEmailContentURI() {
        String packageName = sAppLike.getApplicationInfo().packageName;
        EMAIL_ATTACHMENT_CONTENT_URI = Uri.parse("content://"+ packageName +".eimattachmentprovider");
        EIM_EMAIL_AUTHORITY = packageName +  ".eimemail";
        EIM_EMAIL_CONTENT_URI = Uri.parse("content://" + EIM_EMAIL_AUTHORITY);
        EIM_MESSAGE_AUTHORITY = packageName + ".eimmessageprovider";
        EIM_MESSAGE_CONTENT_URI = Uri.parse("content://" + EIM_MESSAGE_AUTHORITY);
    }

    public abstract void exitApp(boolean killApp);

    @Nullable
    public static DomainSettings getDomainSetting() {
        if(null == sDomainSettings) {
            sDomainSettings = DomainSettingInfo.getInstance().getDomainSettingsData(baseContext);
            if (sDomainSettings != null && !TextUtils.isEmpty(sDomainSettings.getDashBaseUrl())) {
                AtworkConfig.ADMIN_URL = sDomainSettings.getDashBaseUrl();
            }
        }

        return sDomainSettings;
    }

    @Nullable
    public static AppProfile getAppProfile() {
        if (null == sAppProfile) {
            sAppProfile = DomainSettingInfo.getInstance().getAppProfileData(baseContext);
        }

        return sAppProfile;
    }

    public static void runOnMainThread(Runnable runnable) {
        getHandler().post(runnable);
    }

    public static Handler getHandler() {
        if(null == sHandler) {
            sHandler = new Handler(Looper.getMainLooper());
        }

        return sHandler;
    }
}
