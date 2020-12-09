package com.foreveross.atwork.modules.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.db.service.repository.AppRepository;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.NativeApp;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.NativeAppDownloadManager;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dasunsy on 2017/3/10.
 */

public class AppRefreshHelper {
    public static final String ACTION_REFRESH_APP = "action_refresh_app";

    public static final String ACTION_REFRESH_APP_LIGHTLY = "action_refresh_app_lightly";




    @Nullable
    public static List<GroupAppItem> sortApp(Context context, List<App> data) {

        if (null == data) {
            return null;
        }



        Map<String, List<AppBundles>> appMap = new HashMap<>();
        List<AppBundles> appBundleList = null;
        for (App app : data) {
            if ( app == null || app.mBundles.isEmpty()) {
                continue;
            }
            if (app instanceof NativeApp) {
                for (AppBundles appBundle: app.mBundles) {
                    File dir = new File(AtWorkDirUtils.getInstance().getAppUpgrade(LoginUserInfo.getInstance().getLoginUserUserName(context)));
                    if (dir.exists()) {
                        String[] files = dir.list();
                        setAppStatusDependsOnLocal(context, files, appBundle);
                    }
                    if (AppHelper.isAppInstalled(context, appBundle.mPackageName)) {
                        NativeAppDownloadManager.DownloadAppInfo info = NativeAppDownloadManager.getInstance().mDownLoadInfoMap.get(appBundle.mBundleId);
                        if (info != null) {
                            info.status = NativeAppDownloadManager.DownLoadStatus.STATUS_INSTALLED;

                        } else {
                            info = new NativeAppDownloadManager.DownloadAppInfo(context, appBundle);
                            info.status = NativeAppDownloadManager.DownLoadStatus.STATUS_INSTALLED;
                            NativeAppDownloadManager.getInstance().mDownLoadInfoMap.put(app.mAppId, info);
                        }
                    }
                }
            }
            String categoryNameI18n = app.getCategoryNameI18n(context);
            if (appMap.containsKey(categoryNameI18n)) {
                for(AppBundles bundle : app.mBundles) {
                    if (bundle == null || bundle.mHidden) {
                        continue;
                    }
                    List<AppBundles> listInMap = appMap.get(categoryNameI18n);
                    if (listInMap != null) {
                        listInMap.add(bundle);
                    }

                }
            } else {
                appBundleList = new ArrayList<>();
                for(AppBundles bundle : app.mBundles) {
                    if (bundle == null || bundle.mHidden) {
                        continue;
                    }
                    appBundleList.add(bundle);
                }
                appMap.put(categoryNameI18n, appBundleList);
            }
        }

        List<GroupAppItem> groupAppItems = new ArrayList<>();

        Set<String> keySet = appMap.keySet();
        for (String key : keySet) {
            GroupAppItem groupAppItem = new GroupAppItem(key, appMap.get(key), GroupAppItem.TYPE_APP);
            if (ListUtil.isEmpty(appMap.get(key))) {
                continue;
            }
            List<AppBundles> bundles = appMap.get(key);
            int sort = 0;
            if (!ListUtil.isEmpty(bundles)) {
                App app = AppManager.getInstance().queryAppLocalSync(bundles.get(0).appId);
                if (app != null) {
                    sort = app.mCategorySort;
                } 
            }
            groupAppItem.order = sort;
            GroupAppItem titleAppItem = new GroupAppItem(key, GroupAppItem.TYPE_TITLE);
            titleAppItem.order = sort;
            groupAppItems.add(titleAppItem);
            groupAppItems.add(groupAppItem);
        }

        Collections.sort(groupAppItems, (lhs, rhs) -> {
            int result = lhs.order - rhs.order;
            if (result == 0) {
                String lhsPinyin = FirstLetterUtil.getFullLetter(lhs.title);
                String rhsPinyin = FirstLetterUtil.getFullLetter(rhs.title);
                result = lhsPinyin.compareTo(rhsPinyin);
            }
            return result;
        });

        for (GroupAppItem groupAppItem : groupAppItems) {
            groupAppItem.sortAppBundles();
        }

        return groupAppItems;

    }


    private static void setAppStatusDependsOnLocal(Context context, String[] files, AppBundles appBundle) {
        if (files == null) {
            return;
        }
        for (String filePath : files) {
            String pkgName = AppUtil.getAPKPkgName(context, AtWorkDirUtils.getInstance().getAppUpgrade(LoginUserInfo.getInstance().getLoginUserUserName(context)) + File.separator + filePath);
            if (!pkgName.equalsIgnoreCase(appBundle.mPackageName)) {
                continue;
            }
            NativeAppDownloadManager.DownloadAppInfo info = NativeAppDownloadManager.getInstance().mDownLoadInfoMap.get(appBundle.mBundleId);
            if (info == null) {
                info = new NativeAppDownloadManager.DownloadAppInfo(context, appBundle);
                info.status = NativeAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADED_NOT_INSTALL;
                NativeAppDownloadManager.getInstance().mDownLoadInfoMap.put(appBundle.mBundleId, info);
                break;
            }
            info.status = NativeAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADED_NOT_INSTALL;
            break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static void refreshAppAbsolutely() {
        String orgId = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);
        refreshAppAbsolutely(orgId, false);
    }

    @SuppressLint("StaticFieldLeak")
    public static void refreshAppAbsolutely(String orgCode, boolean checkUpdate) {
        new AsyncTask<Void, Void, List<App>>() {
            @Override
            protected List<App> doInBackground(Void... params) {

                return AppRepository.getInstance().queryAccessApps(orgCode);
            }

            @Override
            protected void onPostExecute(List<App> appList) {
                AppManager.getInstance().updateAppList(appList, orgCode);

                refreshApp();

                LogUtil.e("AdvertisementManager.INSTANCE.notifyRefreshAdvertisements(orgCode)");

                if(checkUpdate) {
                    checkAppUpdate(orgCode);
                }

            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());

    }

    private static void checkAppUpdate(String orgCode) {
        if(StringUtils.isEmpty(orgCode)) {
            orgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);
        }
        AppManager.getInstance().getAppCheckUpdateController().checkAppsUpdate(orgCode, null, new AppManager.CheckAppListUpdateListener() {
            @Override
            public void refresh(boolean needUpdate) {
                if (needUpdate) {
                    AppRefreshHelper.refreshApp();

                }

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }

    public static void refreshApp() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_REFRESH_APP));
    }

    public static void refreshAppLightly() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_REFRESH_APP_LIGHTLY));
    }



}
