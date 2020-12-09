package com.foreveross.atwork.infrastructure.utils;

import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.model.app.LocalApp;
import com.foreveross.atwork.infrastructure.model.app.NativeApp;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.app.Settings;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.foreveross.atwork.infrastructure.model.app.SystemApp;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleType;

import java.util.List;

/**
 * Created by dasunsy on 2016/11/8.
 */

public class AppWrapHelper {

    public static App transferApp(App app) {
        App appWrapped = null;
        if(AppKind.LightApp.equals(app.mAppKind)) {
            appWrapped = new LightApp();

            AppWrapHelper.wrapLightApp((LightApp) appWrapped, app.mBundles);

        } else if(AppKind.ServeNo.equals(app.mAppKind)) {
            appWrapped =  new ServiceApp();

        } else if(AppKind.NativeApp.equals(app.mAppKind)) {

            if (!ListUtil.isEmpty(app.mBundles)) {
                AppBundles appBundle = app.mBundles.get(0);

                if(BundleType.System.equals(appBundle.mBundleType)) {
                    appWrapped = new SystemApp();
                    AppWrapHelper.wrapSystemApp((SystemApp) appWrapped, appBundle);

                } else {
                    appWrapped = new NativeApp();
                    AppWrapHelper.wrapNativeApp((NativeApp) appWrapped, appBundle);
                }
            }



        } else if(AppKind.NativeEmail.equals(app.mAppKind)) {
            appWrapped = new LocalApp();

        }

        if (null == appWrapped) {
            appWrapped = new LightApp();

        }


        if (!ListUtil.isEmpty(app.mBundles)) {
            AppBundles appBundle = app.mBundles.get(0);
            app.mNewVersionNotice = appBundle.mNewVersionNotice;
            app.mVersion = appBundle.mBundleVersion;
        }

        AppBundles.wrapAppBundleData(appWrapped);

        appWrapped.mAppId = app.mAppId;
        appWrapped.mDomainId = app.mDomainId;
        appWrapped.mAppType = app.mAppType;
        appWrapped.mAvatar = app.mAvatar;
        appWrapped.mOrgId = app.mOrgId;
        appWrapped.mCategoryId = app.mCategoryId;
        appWrapped.mCategoryName = app.mCategoryName;
        appWrapped.mCategoryEnName = app.mCategoryEnName;
        appWrapped.mCategoryTwName = app.mCategoryTwName;
        appWrapped.mCategorySort = app.mCategorySort;
        appWrapped.mCategoryCreateTime = app.mCategoryCreateTime;
        appWrapped.mCategoryRefreshTime = app.mCategoryRefreshTime;
        appWrapped.mCategoryPinYin = app.mCategoryPinYin;
        appWrapped.mCategoryInitial = app.mCategoryInitial;
        appWrapped.mAppName = app.mAppName;
        appWrapped.mAppEnName = app.mAppEnName;
        appWrapped.mAppTwName = app.mAppTwName;
        appWrapped.mAppKind = app.mAppKind;
        appWrapped.mAppIntro = app.mAppIntro;
        appWrapped.mAppInitial = app.mAppInitial;
        appWrapped.mAppPinYin = app.mAppPinYin;
        appWrapped.mAppSort = app.mAppSort;
        appWrapped.mAppRecommendMode = app.mAppRecommendMode;
        appWrapped.mAppDistributeMode = app.mAppDistributeMode;
        appWrapped.mAppCreateTime = app.mAppCreateTime;
        appWrapped.mAppRefreshTime = app.mAppRefreshTime;
        appWrapped.mBundles = app.mBundles;
        appWrapped.mTop = app.mTop;
        appWrapped.mHideMode = app.mHideMode;
        appWrapped.mVersion = app.mVersion;
        appWrapped.mAppBiologicalAuth = app.mAppBiologicalAuth;
        appWrapped.mComponentMode = app.mComponentMode;
        appWrapped.mIsDomainApp = app.mIsDomainApp;

        return appWrapped;
    }

    public static void wrapSystemApp(SystemApp app, AppBundles appBundle) {
        app.mTargetUrl = appBundle.mTargetUrl;
        if(StringUtils.isEmpty(app.mTargetUrl)) {
            app.mTargetUrl = appBundle.mInitUrl;
        }
    }

    public static void wrapNativeApp(NativeApp app, AppBundles appBundle) {
        app.mPackageId = appBundle.mPackageId;
        app.mPackageName = appBundle.mPackageName;
        app.mPackageNo = appBundle.mPackageNo;
        app.mPackageSignature = appBundle.mPackageSignature;
        app.mAppParams = appBundle.mBundleParams;
    }

    public static void wrapLightApp(LightApp app, List<AppBundles> appBundles) {
        if (!ListUtil.isEmpty(appBundles)) {
            app.mAccessEndPoints = appBundles.get(0).mAccessEndPoints;

            Settings settings = appBundles.get(0).mSettings;

            if(null != settings) {
                Settings.Behaviour behaviour = settings.mMobileBehaviour;

                if (null != behaviour) {
                    app.mScreenMode = behaviour.mScreenMode;
                    app.mShowMode = behaviour.mShowMode;

                    app.mBannerType = behaviour.mBannerType;
                    app.mBannerProp = behaviour.mBannerProp;
                    app.mProgressBarType = behaviour.mProgressBarType;
                    app.mProgressBarColor = behaviour.mProgressBarColor;
                    app.mRelease = behaviour.mRelease;

                }

            } else { //旧版本兼容处理
                app.mScreenMode = appBundles.get(0).mScreenMode;
                app.mShowMode = appBundles.get(0).mShowMode;
            }



        }
    }

    public static void wrapAppShortcutList(List<App> accessAppList, List<Shortcut> shortcutList) {
        if (!ListUtil.isEmpty(accessAppList) && !ListUtil.isEmpty(shortcutList)) {
            for(App app : accessAppList) {

                for(Shortcut shortcut : shortcutList) {

                    if(app.mAppId.equals(shortcut.mAppId)) {
                        app.mShortcut = shortcut;
                        break;
                    }
                }

            }
        }
    }
}
