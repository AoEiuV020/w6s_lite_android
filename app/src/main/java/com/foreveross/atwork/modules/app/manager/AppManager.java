package com.foreveross.atwork.modules.app.manager;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.cache.AppCache;
import com.foreverht.db.service.repository.AppRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.app.AppSyncNetService;
import com.foreveross.atwork.api.sdk.app.model.InstallOrDeleteAppJSON;
import com.foreveross.atwork.api.sdk.app.model.InstallOrRemoveAppResponseJson;
import com.foreveross.atwork.api.sdk.app.requestJson.QueryAppListByAdminRequest;
import com.foreveross.atwork.api.sdk.app.requestJson.QueryAppListInAppStoreRequest;
import com.foreveross.atwork.api.sdk.app.responseJson.AppListResponseJson;
import com.foreveross.atwork.api.sdk.app.responseJson.CheckAppUpdateResponseJson;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppItemResultByAdmin;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppListByAdminResponse;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppListByAdminResponseResult;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppListInAppStoreResponse;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppListInAppStoreResult;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.foreveross.atwork.infrastructure.model.app.admin.QueryAppItemResultEntry;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppType;
import com.foreveross.atwork.infrastructure.model.clickStatistics.ClickEvent;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.OrgCommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.AppWrapHelper;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.manager.model.GetAppListRequest;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.SessionAppHelper;
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.TabHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kotlin.collections.CollectionsKt;

/**
 * App管理类，处理各种跟应用相关逻辑
 * Created by reyzhang22 on 16/4/14.
 */
public class AppManager {
    private static final String TAG = AppManager.class.getSimpleName();

    private static final String REQUEST_ID_CHECK_APP_UPDATES_REMOTE_PREFIX = "REQUEST_ID_CHECK_APP_UPDATES_REMOTE_";

//    private static final long DEFAULT_THRESHOLD_CHECK_APP_UPDATES_REMOTE = 2 * 60 * 1000L;
    private static final long DEFAULT_THRESHOLD_CHECK_APP_UPDATES_REMOTE = 1L;

    private static AppManager sInstance = new AppManager();


    private CopyOnWriteArrayList<App> mAppListData = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<QueryAppItemResultByAdmin> mAdminQueryAppResultListData = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<AppBundles> mAppBundleListData = new CopyOnWriteArrayList<>();

    private AppCheckUpdateController mAppCheckUpdateController = new AppCheckUpdateController();

    private AppManager() {

    }

    public static AppManager getInstance() {
        return sInstance;
    }

    public AppCheckUpdateController getAppCheckUpdateController() {
        return mAppCheckUpdateController;
    }

    public void addInterceptRequestIdCheckAppUpdatesRemote() {
        RequestRemoteInterceptor.INSTANCE.addInterceptRequestId(getRequestIdCheckAppUpdatesRemote());
    }

    public void removeInterceptRequestCheckAppUpdatesRemote() {
        RequestRemoteInterceptor.INSTANCE.removeInterceptRequestId(getRequestIdCheckAppUpdatesRemote());
    }

    public boolean checkLegalRequestIdCheckAppUpdatesRemote() {
        return RequestRemoteInterceptor.INSTANCE.checkLegal(getRequestIdCheckAppUpdatesRemote(), DEFAULT_THRESHOLD_CHECK_APP_UPDATES_REMOTE);
    }

    public String getRequestIdCheckAppUpdatesRemote() {
        return REQUEST_ID_CHECK_APP_UPDATES_REMOTE_PREFIX + PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);
    }

    public void initAppSyncStatusInLogin() {
        Context context = AtworkApplicationLike.baseContext;
        Set<String> appOrgCodesSyncSuccess = LoginUserInfo.getInstance().getAppOrgCodesSyncStatus(context);
        if(null == appOrgCodesSyncSuccess) {
            appOrgCodesSyncSuccess = new HashSet<>();
        }
        appOrgCodesSyncSuccess.clear();

        LoginUserInfo.getInstance().setAppSyncStatus(context, appOrgCodesSyncSuccess);


    }

    public void removeAppSyncStatus(String orgCode) {
        Context context = AtworkApplicationLike.baseContext;
        Set<String> appOrgCodesSyncSuccess = LoginUserInfo.getInstance().getAppOrgCodesSyncStatus(context);
        if (null != appOrgCodesSyncSuccess) {
            appOrgCodesSyncSuccess.remove(orgCode);
            LoginUserInfo.getInstance().setAppSyncStatus(context, appOrgCodesSyncSuccess);

        }

    }

    public void updateAppSyncStatusSuccess(String orgCode) {
        Context context = AtworkApplicationLike.baseContext;
        Set<String> appOrgCodesSyncSuccess = LoginUserInfo.getInstance().getAppOrgCodesSyncStatus(context);
        if (null != appOrgCodesSyncSuccess) {
            appOrgCodesSyncSuccess.add(orgCode);
            LoginUserInfo.getInstance().setAppSyncStatus(context, appOrgCodesSyncSuccess);
        }

    }

    public boolean isAppSyncSuccess(String orgCode) {

        Set<String> appOrgCodesSyncSuccess = LoginUserInfo.getInstance().getAppOrgCodesSyncStatus(AtworkApplicationLike.baseContext);

//        if(null != appOrgCodesSyncSuccess && appOrgCodesSyncSuccess.contains("defaultAny")) {
//            return true;
//        }

        if(null != appOrgCodesSyncSuccess && appOrgCodesSyncSuccess.contains(orgCode)) {
            return true;
        }

        return false;
    }

    public synchronized void updateAppList(List<App> newAppList, String orgCode) {
        if (PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext).equals(orgCode)) {
            clearAppData();
            for (App app : newAppList) {
                AppBundles.wrapAppBundleData(app);
                mAppBundleListData.addAll(app.mBundles);
            }

            mAppListData.addAll(newAppList);


        }

        AppCache.getInstance().setAppsCache(newAppList);
    }

    public List<App> getAppList() {
        return new CopyOnWriteArrayList<>(new HashSet<>(mAppListData));
    }

    public List<AppBundles> getAppBundleList() {
        return mAppBundleListData;
    }

    public void clear() {
        clearAppData();
        clearAdminQueryAppResultListData();
    }

    public void clearAppData() {
        mAppListData.clear();
        mAppBundleListData.clear();
    }

    @Nullable
    public QueryAppItemResultByAdmin queryAppItemResultCache(String appId) {
        for(QueryAppItemResultByAdmin queryAppItemResult : mAdminQueryAppResultListData) {
            if(appId.equals(queryAppItemResult.getAppKey().getAppId())) {
                return queryAppItemResult;
            }
        }

        return null;
    }

    public void clearAdminQueryAppResultListData() {
        mAdminQueryAppResultListData.clear();
    }

    public void remove(App app) {
        mAppListData.remove(app);
        mAppBundleListData.removeAll(app.mBundles);
    }

    public void removeBundle(AppBundles appBundles) {
        for (App app: mAppListData) {
            if (app.mAppId.equalsIgnoreCase(appBundles.appId)) {
                int i = 0;
                for(AppBundles bundle : app.mBundles) {
                    if (bundle.mBundleId.equalsIgnoreCase(appBundles.mBundleId)) {
                        app.mBundles.remove(i);
                        break;
                    }
                    i++;
                }
            }
        }
    }


    public boolean isEmpty() {
        return ListUtil.isEmpty(mAppListData);
    }



    public void checkClearAppNewVersionNotice(App appChecked) {
        if(!appChecked.mNewVersionNotice) {
            return;
        }

        appChecked.mNewVersionNotice = false;


        AppDaoService.getInstance().insertOrUpdateApp(appChecked, new AppDaoService.AddOrRemoveAppListener() {
            @Override
            public void addOrRemoveSuccess() {
                updateAppStatusInAppListData(appChecked);
                AppRefreshHelper.refreshApp();
            }

            @Override
            public void addOrRemoveFail() {

            }
        });
    }

    private void updateAppStatusInAppListData(App appChecked) {
        List<App> appListData = getAppList();
        for(App app : appListData) {
            if(app.equals(appChecked)) {
                app.mNewVersionNotice = false;
                break;
            }
        }
    }

    @NonNull
    public TreeMap<Integer, ArrayList<Shortcut>> getShortcutGroup() {
        TreeMap<Integer, ArrayList<Shortcut>> shortcutGroup = new TreeMap<>();
        if (!ListUtil.isEmpty(mAppListData)) {
            for (App app : mAppListData) {
                if (null != app.mShortcut) {
                    ArrayList<Shortcut> shortcutList = shortcutGroup.get(app.mShortcut.mGroupId);
                    if (null == shortcutList) {
                        shortcutList = new ArrayList<>();
                        shortcutGroup.put(app.mShortcut.mGroupId, shortcutList);
                    }

                    shortcutList.add(app.mShortcut);
                }
            }

            for (ArrayList<Shortcut> shortcuts : shortcutGroup.values()) {
                Collections.sort(shortcuts, (o1, o2) -> o1.mSortOrder - o2.mSortOrder);
            }
        }

        return shortcutGroup;
    }

    @SuppressLint("StaticFieldLeak")
    public void queryAppsByAdmin(Context context, QueryAppListByAdminRequest queryAppListByAdminRequest, BaseNetWorkListener<QueryAppListByAdminResponseResult> listener) {
        new AsyncTask<Void, Void, HttpResult>(){

            @Override
            protected HttpResult doInBackground(Void... voids) {
                HttpResult httpResult = AppSyncNetService.getInstance().queryAppsByAdminSync(context, queryAppListByAdminRequest);
                if(httpResult.isRequestSuccess()) {
                    QueryAppListByAdminResponse response = (QueryAppListByAdminResponse) httpResult.resultResponse;
                    assembleAppList(response);

                }
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    QueryAppListByAdminResponse response = (QueryAppListByAdminResponse) httpResult.resultResponse;
                    listener.onSuccess(response.getResult());
                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    private void assembleAppList(QueryAppListByAdminResponse response) {
        QueryAppListByAdminResponseResult result = response.getResult();
        if(null == result) {
            return;
        }



        List<JsonObject> appResults = result.getAppJsonResults();
        if(ListUtil.isEmpty(appResults)) {
            return;
        }


        List<QueryAppItemResultByAdmin> appResultList = CollectionsKt.mapNotNull(appResults, jsonObject -> {
            QueryAppItemResultByAdmin queryAppItemResultByAdmin = JsonUtil.fromJson(jsonObject.toString(), QueryAppItemResultByAdmin.class);


            if(null != queryAppItemResultByAdmin) {
                for(QueryAppItemResultEntry resultEntry : queryAppItemResultByAdmin.getEntries()) {
                    resultEntry.setAppId(queryAppItemResultByAdmin.getAppKey().getAppId());
                    resultEntry.setDomainId(queryAppItemResultByAdmin.getAppKey().getDomainId());
                }

                queryAppItemResultByAdmin.setRawResultData(jsonObject);
            }
            return queryAppItemResultByAdmin;
        });

        List<App> appList = CollectionsKt.mapNotNull(appResultList, QueryAppItemResultByAdmin::transfer);

        App.batchTransferKindAccessType(appList);

        result.setAppList(appList);
        result.setAppResults(appResultList);

        clearAdminQueryAppResultListData();
        mAdminQueryAppResultListData.addAll(appResultList);

    }


    /**
     * 获取应用列表(应用市场接口)
     * */
    @SuppressLint("StaticFieldLeak")
    public void queryAppListInAppStore(Context context, QueryAppListInAppStoreRequest queryAppListInAppStoreRequest, BaseNetWorkListener<QueryAppListInAppStoreResult> listener) {
        new AsyncTask<Void, Void, HttpResult>(){

            @Override
            protected HttpResult doInBackground(Void... voids) {
                HttpResult httpResult = AppSyncNetService.getInstance().queryAppListInAppStoreSync(context, queryAppListInAppStoreRequest);
                if(httpResult.isRequestSuccess()) {
                    QueryAppListInAppStoreResponse response = (QueryAppListInAppStoreResponse) httpResult.resultResponse;
                    App.batchTransferKindAccessType(response.getResult().getAppList());

                }
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    QueryAppListInAppStoreResponse response = (QueryAppListInAppStoreResponse) httpResult.resultResponse;
                    listener.onSuccess(response.getResult());
                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 更新组织里最新的 app 列表
     * */
    @SuppressLint("StaticFieldLeak")
    public void syncApp(Context context, String orgCode, OnSyncAppListListener onSyncAppListListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = AppSyncNetService.getInstance().queryUserAppsFromRemote(context, orgCode);

                if (httpResult.isRequestSuccess()) {
                    AppListResponseJson appListResponseJson = (AppListResponseJson) httpResult.resultResponse;
                    if (!ListUtil.isEmpty(appListResponseJson.result.mAccessList)) {

                        AppWrapHelper.wrapAppShortcutList(appListResponseJson.result.mAccessList, appListResponseJson.result.mShortcutList);

                        App.batchTransferKindAccessType(appListResponseJson.result.mAccessList);

                        AppRepository.getInstance().batchInsertOrUpdateAppCheckDb(appListResponseJson.result.mAccessList);

                        SessionAppHelper.handleSessions(appListResponseJson.result.mAccessList);
                    }

                }

                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    onSyncAppListListener.onSuccess();
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onSyncAppListListener);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @SuppressLint("StaticFieldLeak")
    public void queryApp(final Context context, final String identifier, String orgCode, final GetAppFromMultiListener listener) {

        if(Session.WORKPLUS_SUMMARY_HELPER.equals(identifier)) {
            return;
        }

        App appCache = AppCache.getInstance().getAppCache(identifier);
        if(null != appCache) {
            listener.onSuccess(appCache);
            return;
        }

        new AsyncTask<Void, Void, MultiResult>() {
            @Override
            protected MultiResult doInBackground(Void... params) {
                return queryAppMultiResultSync(context, identifier, orgCode);
            }

            @Override
            protected void onPostExecute(MultiResult multiResult) {
                App app = getAppFromMultiResult(multiResult);
                if (app != null) {
                    listener.onSuccess(app);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //获取服务器端最新app数据
    public void queryRemoteApp(final Context context, final String identifier, String orgCode, final GetAppFromMultiListener listener) {

        if(Session.WORKPLUS_SUMMARY_HELPER.equals(identifier)) {
            return;
        }

        new AsyncTask<Void, Void, MultiResult>() {
            @Override
            protected MultiResult doInBackground(Void... params) {
                HttpResult httpResult = AppSyncNetService.getInstance().queryAppInfoFromRemote(context, identifier, orgCode);
                App app = null;
                if (httpResult.isRequestSuccess()) {
                    QueryAppResponse queryAppResponse = (QueryAppResponse) httpResult.resultResponse;
                    app = queryAppResponse.result;

                    app.transferAccessType();
                    app = AppWrapHelper.transferApp(app);

                }

                if (app != null) {
                    AppRepository.getInstance().insertOrUpdateApp(app);
                    SessionAppHelper.handleSessions(ListUtil.makeSingleList(app));
                }
                return queryAppMultiResultSync(context, identifier, orgCode);
            }

            @Override
            protected void onPostExecute(MultiResult multiResult) {
                App app = getAppFromMultiResult(multiResult);
                if (app != null) {
                    listener.onSuccess(app);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @NonNull
    public List<AppBundles> getAppListInCustomStrategy(Context context, GetAppListRequest getAppListRequest,  List<AppBundles> appBundlesList) {
        List<String> customIdSortList = OrgPersonalShareInfo.getInstance().getCustomAppBundleIdSort(context, getAppListRequest.getOrgCode(context));
        List<AppBundles> bundleListNotFilter = new ArrayList<>();
        bundleListNotFilter.addAll(appBundlesList);

        List<AppBundles> appBundleListResult = queryListInAppBundleIds(bundleListNotFilter, customIdSortList, getAppListRequest.getLimit());

        int makeUpCount = getAppListRequest.getLimit() - appBundleListResult.size();

        if(0 >= makeUpCount) {
            return appBundleListResult;
        }

        //剩余逻辑补充接口要求返回的数量

        bundleListNotFilter.removeAll(appBundleListResult);

        if(ListUtil.isEmpty(bundleListNotFilter)) {
            return appBundleListResult;
        }


        //根据点击率补充数量
        List<ClickEvent> allClickEvents = ClickStatisticsManager.INSTANCE.getClickEvents(Type.APP);
        HashMap<String, List<ClickEvent>> clickEventsInAppsMapLegal = new HashMap<>();

        for(ClickEvent clickEvent : allClickEvents) {

            List<ClickEvent> clickEventsInApp = clickEventsInAppsMapLegal.get(clickEvent.getId());
            if(null == clickEventsInApp) {
                clickEventsInApp = new ArrayList<>();
                clickEventsInAppsMapLegal.put(clickEvent.getId(), clickEventsInApp);
            }

            for(AppBundles appbundle : bundleListNotFilter) {
                if(appbundle.mBundleId.equals(clickEvent.getId())) {
                    clickEventsInApp.add(clickEvent);
                    break;
                }
            }

        }

        List<Map.Entry<String,List<ClickEvent>>> clickEventsInAppsMapLegalEntrySet = new ArrayList<>(clickEventsInAppsMapLegal.entrySet());
        Collections.sort(clickEventsInAppsMapLegalEntrySet, (o1, o2) -> {

            int o1Size = o1.getValue().size();
            int o2Size = o2.getValue().size();

            return o2Size - o1Size;

        });

        List<String> ids = CollectionsKt.map(clickEventsInAppsMapLegalEntrySet, entry -> entry.getKey());
        List<AppBundles> appListResult2 = queryListInAppBundleIds(bundleListNotFilter, ids, makeUpCount);
        appBundleListResult.addAll(appListResult2);


        makeUpCount = getAppListRequest.getLimit() - appBundleListResult.size();

        if(0 >= makeUpCount) {
            return appBundleListResult;
        }

        bundleListNotFilter.removeAll(appBundleListResult);

        if(ListUtil.isEmpty(bundleListNotFilter)) {
            return appBundleListResult;
        }

        //剩余逻辑补充接口要求返回的数量

        if(-1 == getAppListRequest.getLimit() || bundleListNotFilter.size() <= makeUpCount) {
            appBundleListResult.addAll(bundleListNotFilter);

            return appBundleListResult;

        }

        appBundleListResult.addAll(bundleListNotFilter.subList(0, makeUpCount));
        return appBundleListResult;
    }


    public List<AppBundles> queryListInAppBundleIds(List<AppBundles> appBundleList, List<String> appBundleIdList, int limit) {
        List<AppBundles> resultList = new ArrayList<>();

        for(String customSortAppId : appBundleIdList) {

            AppBundles founded = null;
            for(AppBundles bundle : appBundleList) {
                if(customSortAppId.equals(bundle.mBundleId)) {
                    founded = bundle;
                    break;
                }
            }

            if(null != founded) {
                resultList.add(founded);
            }

            //满足数量要求, 即刻返回数据
            if(resultList.size() ==  limit) {
                break;
            }

        }

        return resultList;

    }




    public void batchUpdateShortcutInfoSync(List<Shortcut> oldShortcutList, List<Shortcut> newShortcutList) {

        List<Shortcut> updateDbShortcuts = new ArrayList<>();
        updateDbShortcuts.addAll(newShortcutList);

        handleShortcutData(oldShortcutList, updateDbShortcuts);

        AppRepository.getInstance().batchUpdateShortcutInfo(updateDbShortcuts);

    }

    /**
     * 对比旧的 shortcut 与新的 shortcut 数据, 并计算出用于更新数据库的数据,
     * 且注销相关红点关系
     * @param oldShortcutList
     * @param updateDbShortcuts
     * */
    public void handleShortcutData(List<Shortcut> oldShortcutList, List<Shortcut> updateDbShortcuts) {

        for (Shortcut oldShortcut : oldShortcutList) {

            boolean isExistInNewShortcutList = false;
            for (Shortcut newShortcut : updateDbShortcuts) {
                if (newShortcut.mAppId.equals(oldShortcut.mAppId)) {
                    isExistInNewShortcutList = true;
                    break;
                }
            }

            if (!isExistInNewShortcutList) {
                oldShortcut.mClear = true;
                updateDbShortcuts.add(oldShortcut);

                //unregister red dot relation
                TabNoticeManager.getInstance().unregisterLightNotice(TabHelper.getAboutMeFragmentId(), oldShortcut.mAppId);

            }
        }


    }


    @Nullable
    public App getAppFromMultiResult(MultiResult<App> multiResult) {
        if (multiResult == null) {
            return null;
        }
        App resultApp = null;
        if (null != multiResult.localResult) {
            resultApp = multiResult.localResult;
            return resultApp;
        }
        if (multiResult.httpResult.isRequestSuccess()) {
            resultApp = ((QueryAppResponse) multiResult.httpResult.resultResponse).result;
        }

        return resultApp;
    }

    public App queryAppSync(Context context, final String identifier, String orgId) {
        MultiResult<App> multiResult = queryAppMultiResultSync(context, identifier, orgId);
        return getAppFromMultiResult(multiResult);
    }

    public MultiResult<App> queryAppMultiResultSync(Context context, final String identifier, String orgId) {
        MultiResult<App> multiResult = new MultiResult<>();

        App app = queryAppLocalSync(identifier);
//        App app = null;

        if (app == null) {
            HttpResult httpResult = AppSyncNetService.getInstance().queryAppInfoFromRemote(context, identifier, orgId);

            if (httpResult.isRequestSuccess()) {
                QueryAppResponse queryAppResponse = (QueryAppResponse) httpResult.resultResponse;
                app = queryAppResponse.result;

                app.transferAccessType();
                app = AppWrapHelper.transferApp(app);

            }


            if (app != null) {
                AppRepository.getInstance().insertOrUpdateApp(app);
                SessionAppHelper.handleSessions(ListUtil.makeSingleList(app));
            }

            multiResult.httpResult = httpResult;

        } else {
            multiResult.localResult = app;
        }


        return multiResult;
    }

    @Nullable
    public App queryAppLocalSync(String identifier) {
        App app = AppCache.getInstance().getAppCache(identifier);
        if (app == null) {
            app = AppRepository.getInstance().queryApp(identifier);
            if (app != null) {
                AppCache.getInstance().setAppCache(app);
            }
        }
        return app;
    }


    public void queryServiceMenu(Context context, final String appId, final QueryServiceMenuListener queryServiceMenuListener) {
        new AsyncTask<Void, Void, List<ServiceApp.ServiceMenu>>() {
            @Override
            protected List<ServiceApp.ServiceMenu> doInBackground(Void... params) {
                return queryServiceMenuSync(context, appId);
            }

            @Override
            protected void onPostExecute(List<ServiceApp.ServiceMenu> serviceMenus) {
                queryServiceMenuListener.queryServiceMenuSuccess(serviceMenus);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    public List<ServiceApp.ServiceMenu> queryServiceMenuSync(Context context, final String appId) {
        String menuJson = AppRepository.getInstance().queryMenuJson(appId);
        List<ServiceApp.ServiceMenu> serviceMenus = new ArrayList<>();

        if (!StringUtils.isEmpty(menuJson)) {
            serviceMenus = new Gson().fromJson(menuJson, new TypeToken<List<ServiceApp>>() {
            }.getType());
        }

        if (0 == serviceMenus.size()) {
            serviceMenus = AppSyncNetService.getInstance().queryServiceAppMenu(context, appId);
            if (serviceMenus != null) {
                menuJson = new Gson().toJson(serviceMenus);
                AppRepository.getInstance().updateMenuJson(appId, menuJson);
            }
        }

        return serviceMenus;
    }


    public boolean useOfflinePackageNeedReLoad(final AppBundles appBundle) {
        if(!appBundle.useOfflinePackage()) {
            return false;
        }

        Context context = AtworkApplicationLike.baseContext;
        String releasePath = UrlHandleHelper.getReleasePath(context, appBundle);
        if(!FileUtil.isExist(releasePath)) {
            return true;
        }

        String currentDigest = getOfflinePackageDigest(releasePath);
        if(OrgCommonShareInfo.getLightappOfflineReleaseUnzipDigest(context, appBundle).equals(currentDigest)) {
            return false;
        }

        return true;
    }

    public String getOfflinePackageDigest(String releasePath) {
        int fileCount = FileUtil.getFileCount(releasePath);
        long fileSize = FileUtil.getFileDicSize(releasePath);

        LogUtil.e("fileCount -> " + fileCount + "  fileSize ->  " + fileSize );

        return fileCount + "_" + fileSize;
    }


    public interface CheckAppListUpdateListener extends NetWorkFailListener {
        void refresh(boolean needUpdate);
    }

    public interface QueryServiceMenuListener {
        void queryServiceMenuSuccess(List<ServiceApp.ServiceMenu> serviceMenus);
    }

    public interface GetAppFromMultiListener extends NetWorkFailListener {
        void onSuccess(@NonNull App app);
    }

    public interface OnSyncAppListListener extends NetWorkFailListener {
        void onSuccess();
    }

    public interface OnGetAppListListener {
        void success(List<App> appList);
    }

    public class AppCheckUpdateController {



        public void checkAppsUpdate(final String orgId, Map<String, LightNoticeMapping> lightNoticeMap, CheckAppListUpdateListener listener) {
            CheckUpdater updater = new CheckUpdater(orgId, lightNoticeMap, listener);
            updater.executeOnExecutor(AsyncTaskThreadPool.getInstance());
        }

        public void initAppsSyncData(final String orgId, Map<String, LightNoticeMapping> lightNoticeMap, CheckAppListUpdateListener listener) {
            CheckUpdater updater = new CheckUpdater(orgId, lightNoticeMap, listener);
            updater.setTryAppSync(true);

            updater.executeOnExecutor(AsyncTaskThreadPool.getInstance());
        }


        public MultiResult<Boolean> initAppsSyncDataSync(final String orgId, Map<String, LightNoticeMapping> lightNoticeMap) {
            CheckUpdater updater = new CheckUpdater(orgId, lightNoticeMap, null);
            updater.setTryAppSync(true);

            return updater.checkUpdateSync();
        }

        class CheckUpdater extends AsyncTask<Void, Void, MultiResult<Boolean>> {

            private String mOrgId;
            private Map<String, LightNoticeMapping> mLightNoticeMap;
            private CheckAppListUpdateListener mListener;
            private boolean mAppSyncStatusSuccess;
            private boolean mTryAppSync;

            public CheckUpdater(final String orgId, Map<String, LightNoticeMapping> lightNoticeMap, CheckAppListUpdateListener listener) {
                mOrgId = orgId;
                mLightNoticeMap = lightNoticeMap;
                mListener = listener;
                mAppSyncStatusSuccess = isAppSyncSuccess(orgId);
            }

            public void setTryAppSync(boolean initSync) {
                this.mTryAppSync = initSync;
            }

            @Override
            protected MultiResult<Boolean> doInBackground(Void... params) {
                return checkUpdateSync();

            }

            @NonNull
            public MultiResult<Boolean> checkUpdateSync() {

                List<App> accessList = new ArrayList<>();
                List<App> adminList = new ArrayList<>();
                //若传进来的 app list 为空, 则从数据库读取
                compateUpdateList(accessList, adminList);

                HttpResult httpResult = AppSyncNetService.getInstance().checkUserAppsUpdateFromRemote(BaseApplicationLike.baseContext, accessList, adminList, mOrgId);
                Boolean needUpdate = false;

                if (httpResult.isRequestSuccess() && httpResult.resultResponse instanceof CheckAppUpdateResponseJson) {
                    CommonShareInfo.appDataReset(AtworkApplicationLike.baseContext);
                    CheckAppUpdateResponseJson resultResponse = (CheckAppUpdateResponseJson) httpResult.resultResponse;

                    List<String> deletedIdList = new ArrayList<>();
                    List<App> newAppList = new ArrayList<>();
                    List<App> updateAppList = new ArrayList<>();
                    List<Shortcut> shortcutList = new ArrayList<>();

                    //先处理删除操作
                    if (!mTryAppSync) {
                        wrapDeleteAppList(resultResponse, deletedIdList);
                    }

                    //再处理新安装app操作
                    wrapNewAppList(mOrgId, accessList, resultResponse, newAppList);
                    //再处理更新app操作
                    wrapUpdateAppList(mOrgId, accessList, resultResponse, updateAppList);
                    //再处理快捷方式(shortcut)操作
                    wrapShortcutList(accessList, resultResponse, deletedIdList, newAppList, shortcutList);

                    needUpdate = updateDb(accessList, needUpdate, shortcutList, deletedIdList, newAppList, updateAppList);

                    refreshAppData(accessList, needUpdate, deletedIdList, newAppList, updateAppList, shortcutList);

                    refreshLightNoticeUnregisterHandle(accessList, needUpdate);

                    AppManager.getInstance().updateAppSyncStatusSuccess(mOrgId);
                }

                MultiResult<Boolean> multiResult = new MultiResult<>();
                multiResult.httpResult = httpResult;
                multiResult.localResult = needUpdate;

                //更新到数据库
                return multiResult;
            }

            private void wrapShortcutList(List<App> accessList, CheckAppUpdateResponseJson resultResponse, List<String> deletedIdList, List<App> newAppList, List<Shortcut> shortcutList) {
                if (!ListUtil.isEmpty(resultResponse.result.mShortcutList)) {
                    shortcutList.addAll(resultResponse.result.mShortcutList);

                    List<Shortcut> shortcutRemovedList = new ArrayList<>();

                    List<String> appInstalledIdList = App.toAppIdList(accessList);
                    appInstalledIdList.removeAll(deletedIdList);
                    appInstalledIdList.addAll(App.toAppIdList(newAppList));

                    //过滤掉不在用户安装列表的快捷方式(shortcut)
                    for (Shortcut shortcut : shortcutList) {
                        if (!appInstalledIdList.contains(shortcut.mAppId)) {
                            shortcutRemovedList.add(shortcut);
                        }
                    }

                    shortcutList.removeAll(shortcutRemovedList);


                }
            }


            @Override
            protected void onPostExecute(MultiResult<Boolean> multiResult) {
                boolean needUpdate = multiResult.localResult;
                HttpResult httpResult = multiResult.httpResult;

                if (httpResult.isRequestSuccess()) {
                    mListener.refresh(needUpdate);

                } else {

                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, mListener);
                }

            }


            @NonNull
            private Boolean updateDb(List<App> accessList, Boolean needUpdate, List<Shortcut> shortcutList, List<String> deletedIdList, List<App> newAppList, List<App> updateAppList) {
                needUpdate = doDeleteIfNeed(needUpdate, deletedIdList);
                needUpdate = doNewIfNeed(needUpdate, newAppList);
                needUpdate = doUpdateIfNeed(needUpdate, updateAppList);
                needUpdate = doUpdateShortcut(needUpdate, accessList, shortcutList);
                return needUpdate;
            }

            private boolean doUpdateShortcut(Boolean needUpdate, List<App> accessList, List<Shortcut> shortcutList) {
                List<Shortcut> oldShortcutList = new ArrayList<>();

                for (App app : accessList) {
                    if (null != app.mShortcut) {
                        oldShortcutList.add(app.mShortcut);
                    }
                }

                if (ListUtil.checkUpdate(oldShortcutList, shortcutList)) {

                    batchUpdateShortcutInfoSync(oldShortcutList, shortcutList);

                    needUpdate = true;
                }

                return needUpdate;
            }

            private Boolean doDeleteIfNeed(Boolean needUpdate, List<String> deletedIdList) {
                List<InstallOrDeleteAppJSON.AppEntrances> appEntrances = new ArrayList<>();
                for (String deleteId: deletedIdList) {
                    App app = AppRepository.getInstance().queryApp(deleteId);
                    if (app == null) {
                        continue;
                    }
                    InstallOrDeleteAppJSON.AppEntrances entrance = new InstallOrDeleteAppJSON.AppEntrances();
                    entrance.mAppId = app.mAppId;
                    for (AppBundles bundle : app.mBundles) {
                        entrance.mEntries.add(bundle.mBundleId);
                    }
                    appEntrances.add(entrance);
                }
                if (!ListUtil.isEmpty(deletedIdList)) {
                    AppSyncNetService.getInstance().installOrRemoveAppFromRemote(BaseApplicationLike.baseContext, InstallOrDeleteAppJSON.createInstance(appEntrances, mOrgId, false, false));
                        needUpdate = true;

                        AppRepository.getInstance().batchRemoveApp(deletedIdList);

                        for (String appId : deletedIdList) {
                            TabNoticeManager.getInstance().unregisterLightNotice(appId);
                            if (null != mLightNoticeMap) {
                                mLightNoticeMap.remove(appId);
                            }

                            ChatSessionDataWrap.getInstance().removeSessionSafely(appId);

                        }

                }
                return needUpdate;
            }

            private Boolean doUpdateIfNeed(Boolean needUpdate, List<App> updateAppList) {
                if (!ListUtil.isEmpty(updateAppList)) {
                    needUpdate = true;
                    subscribeUpdateBundle(updateAppList);
                    AppRepository.getInstance().batchInsertOrUpdateApp(updateAppList);
                    SessionAppHelper.handleSessions(updateAppList);
                }
                return needUpdate;
            }

            private void subscribeUpdateBundle(List<App> updateAppList) {
                List<InstallOrDeleteAppJSON.AppEntrances> installEntrances = new ArrayList<>();
                List<InstallOrDeleteAppJSON.AppEntrances> deleteEntrances = new ArrayList<>();
                String orgId = "";
                for(App remoteApp : updateAppList) {
                    if (remoteApp == null) {
                        continue;
                    }
                    App localApp = AppManager.getInstance().queryAppLocalSync(remoteApp.mAppId);
                    List<String> remoteBundleIds = new ArrayList<>();
                    List<String> localBundleIds = new ArrayList<>();

                    if (localApp == null) {
                        List<String> ids = new ArrayList<>();
                        for (AppBundles appBundle : remoteApp.mBundles) {
                            ids.add(appBundle.mBundleId);
                        }
                        orgId = remoteApp.mOrgId;
                        installEntrances.add(makeEntranceData(ids, remoteApp.mAppId));
                        continue;
                    }
                    for (AppBundles appBundles : remoteApp.mBundles) {
                        remoteBundleIds.add(appBundles.mBundleId);
                    }
                    for (AppBundles appBundles : localApp.mBundles) {
                        localBundleIds.add(appBundles.mBundleId);
                    }
                    if (localApp.mBundles.size() == remoteApp.mBundles.size()) {
                        if (remoteBundleIds.containsAll(localBundleIds)) {
                            continue;
                        }
                        List<String> copyRemoteList = new ArrayList<>();
                        copyRemoteList.addAll(remoteBundleIds);
                        copyRemoteList.removeAll(localBundleIds);
                        if (!copyRemoteList.isEmpty()) {
                            orgId = remoteApp.mOrgId;
                            installEntrances.add(makeEntranceData(copyRemoteList, remoteApp.mAppId));
                        }
                        List<String> copyLocalList = new ArrayList<>();
                        copyLocalList.addAll(localBundleIds);
                        copyLocalList.removeAll(remoteBundleIds);
                       if (!copyLocalList.isEmpty()) {
                           orgId = remoteApp.mOrgId;
                           deleteEntrances.add(makeEntranceData(copyLocalList, remoteApp.mAppId));
                       }
                        continue;
                    }
                    if (localApp.mBundles.size() < remoteApp.mBundles.size()) {
                        remoteBundleIds.removeAll(localBundleIds);
                        orgId = remoteApp.mOrgId;
                        installEntrances.add(makeEntranceData(localBundleIds, remoteApp.mAppId));
                        continue;
                    }
                    localBundleIds.removeAll(remoteBundleIds);
                    orgId = remoteApp.mOrgId;
                    deleteEntrances.add(makeEntranceData(localBundleIds, remoteApp.mAppId));
                }
                //暂时不关注是否follow 或unfollow成功
                if (!installEntrances.isEmpty()) {
                    InstallOrDeleteAppJSON request = InstallOrDeleteAppJSON.createInstance(installEntrances, orgId, true, false);
                    AppSyncNetService.getInstance().installOrRemoveAppFromRemote(BaseApplicationLike.baseContext, request);
                }
                if (!deleteEntrances.isEmpty()) {
                    InstallOrDeleteAppJSON request = InstallOrDeleteAppJSON.createInstance(deleteEntrances, orgId, false, false);
                    AppSyncNetService.getInstance().installOrRemoveAppFromRemote(BaseApplicationLike.baseContext, request);
                }
            }

            private InstallOrDeleteAppJSON.AppEntrances makeEntranceData(List<String> entranceIds, String appId) {
                InstallOrDeleteAppJSON.AppEntrances entrance = new InstallOrDeleteAppJSON.AppEntrances();
                entrance.mEntries.addAll(entranceIds);
                entrance.mAppId = appId;
                return entrance;
            }


            private Boolean doNewIfNeed(Boolean needUpdate, List<App> newAppList) {
                if (newAppList.isEmpty()) {
                    return needUpdate;
                }
                //new app id list
                List<InstallOrDeleteAppJSON.AppEntrances> appEntrances = new ArrayList<>();
                String orgId = "";
                for (App app : newAppList) {
                    InstallOrDeleteAppJSON.AppEntrances entrance = new InstallOrDeleteAppJSON.AppEntrances();
                    for (AppBundles appBundle : app.mBundles) {
                        entrance.mEntries.add(appBundle.mBundleId);
                    }
                    entrance.mAppId = app.mAppId;
                    orgId = app.mOrgId;
                    appEntrances.add(entrance);
                }
                InstallOrDeleteAppJSON request = InstallOrDeleteAppJSON.createInstance(appEntrances, orgId, true, false);
                InstallOrRemoveAppResponseJson newAppResponseJson = AppSyncNetService.getInstance().installOrRemoveAppFromRemote(BaseApplicationLike.baseContext, request);
                if (null != newAppResponseJson && 0 == newAppResponseJson.status) {
                    needUpdate = true;
                    AppRepository.getInstance().batchInsertOrUpdateApp(newAppList);
                    SessionAppHelper.handleSessions(newAppList);
                }
                return needUpdate;
            }


            private void refreshAppData(List<App> accessList, Boolean needUpdate, List<String> deletedIdList, List<App> newAppList, List<App> updateAppList, List<Shortcut> shortcutList) {
                if (needUpdate) {
                    List<String> removedIdList = new ArrayList<>();
                    List<App> removedAppList = new ArrayList<>();

                    removedIdList.addAll(deletedIdList);

                    for (App app : accessList) {
                        if (removedIdList.contains(app.getId())) {
                            removedAppList.add(app);
                        }
                        for (App updateApp: updateAppList) {
                            if (updateApp.mAppId.equalsIgnoreCase(app.mAppId)) {
                                removedAppList.add(app);
                            }
                        }
                    }

                    accessList.removeAll(removedAppList);
                    accessList.removeAll(updateAppList);
                    accessList.addAll(newAppList);
                    accessList.addAll(updateAppList);


                    refreshAppShortcutData(accessList, shortcutList);


                    //update app data
                    updateAppList(accessList, mOrgId);
                }
            }

            private void refreshLightNoticeUnregisterHandle(List<App> accessList, Boolean needUpdate) {
                if (needUpdate) {
                    for (App app : accessList) {
                        if (!app.isShowInMarket()) {
                            //unregister red dot relation
                            TabNoticeManager.getInstance().unregisterLightNotice(TabHelper.getAppFragmentId(), app.mAppId);
                        }
                    }
                }
            }

            private void refreshAppShortcutData(List<App> accessList, List<Shortcut> shortcutList) {
                for (App app : accessList) {
                    app.mShortcut = null;

                    if (!ListUtil.isEmpty(shortcutList)) {
                        for (Shortcut shortcut : shortcutList) {

                            if (app.getId().equals(shortcut.mAppId)) {
                                app.mShortcut = shortcut;
                                break;

                            }

                        }
                    }
                }
            }

            private void wrapDeleteAppList(CheckAppUpdateResponseJson resultResponse, List<String> deletedIdList) {
                if (!ListUtil.isEmpty(resultResponse.result.mDeleteAccessList)) {
                    deletedIdList.addAll(resultResponse.result.mDeleteAccessList);

                }

                if (!ListUtil.isEmpty(resultResponse.result.mDeleteAdminList)) {
                    deletedIdList.addAll(resultResponse.result.mDeleteAdminList);
                }
            }

            private void wrapNewAppList(String orgCode, List<App> originalAccessList, CheckAppUpdateResponseJson resultResponse, List<App> newAppList) {
                if (!ListUtil.isEmpty(resultResponse.result.mNewAccessList)) {
                    List<App> accessTypeList = App.batchTransferKindAccessType(resultResponse.result.mNewAccessList);

                    assembleAppsNewVersionNotice(originalAccessList, accessTypeList, true);


                    newAppList.addAll(accessTypeList);
                }

            }

            private void wrapUpdateAppList(String orgCode, List<App> originalAccessList, CheckAppUpdateResponseJson resultResponse, List<App> updateAppList) {
                if (!ListUtil.isEmpty(resultResponse.result.mUpdatedAccessList)) {
                    List<App> accessTypeList = App.batchTransferKindAccessType(resultResponse.result.mUpdatedAccessList);

                    assembleAppsNewVersionNotice(originalAccessList, accessTypeList, false);

                    updateAppList.addAll(accessTypeList);
                }


            }

            private void assembleAppsNewVersionNotice(List<App> originalAccessList, List<App> accessTypeList, boolean isNew) {
                if (!ListUtil.isEmpty(originalAccessList) && mAppSyncStatusSuccess) {
                    for(App app : accessTypeList) {
                        if (isNew) {
                            app.mNewVersionNotice = true;

                        } else {
                            assembleAppNewVersionNoticeVersionChanged(originalAccessList, app);
                        }
                    }
                }
            }

            private void assembleAppNewVersionNoticeVersionChanged(List<App> originalAccessList, App app) {

                for(App appOriginal: originalAccessList) {

                    if(appOriginal.equals(app)) {

                        //version changed
                        if(null != appOriginal.mVersion && !appOriginal.mVersion.equals(app.mVersion)) {
                            app.mNewVersionNotice = true;
                        }

                        break;
                    }
                }
            }

            private void compateUpdateList(List<App> accessList, List<App> adminList) {
                if (!CommonShareInfo.isAppDataReset(AtworkApplicationLike.baseContext)) {
                    return;
                }
                if (PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext).equals(mOrgId)
                        && !ListUtil.isEmpty(getAppList())) {

                    for (App app : getAppList()) {
                        if (AppType.Access.equals(app.mAppType)) {
                            accessList.add(app);
                        } else {
                            adminList.add(app);
                        }
                    }
                    return;

                }
                accessList.addAll(AppRepository.getInstance().queryAccessApps(mOrgId));
                adminList.addAll(AppRepository.getInstance().queryAdminApps(mOrgId));
            }

        }

    }
}
