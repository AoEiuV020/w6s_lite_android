package com.foreveross.atwork.modules.app.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.AppRepository;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;

import java.util.List;

import kotlin.collections.CollectionsKt;

/**
 * Created by lingen on 15/5/6.
 */
public class AppDaoService extends BaseDbService{

    private static AppDaoService appDaoService = new AppDaoService();

    private AppDaoService() {

    }

    public static AppDaoService getInstance() {
        return appDaoService;
    }


    /**
     * 查询对应 org 的 shortcut 列表
     * */
    @SuppressLint("StaticFieldLeak")
    public void queryShortcuts(String orgCode, OnQueryShortcutListListener netWorkListener) {
        new AsyncTask<Void, Void, List<Shortcut>>() {
            @Override
            protected List<Shortcut> doInBackground(Void... params) {
                return AppRepository.getInstance().queryShortcuts(orgCode);
            }

            @Override
            protected void onPostExecute(List<Shortcut> shortcutList) {
                netWorkListener.onSuccess(shortcutList);
            }

        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 批量插入应用
     * @param appList
     * @param batchInsertAppListener
     */
    @SuppressLint("StaticFieldLeak")
    public void batchInsertApps(final List<App> appList, final BatchInsertAppListener batchInsertAppListener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return AppRepository.getInstance().batchInsertOrUpdateApp(appList);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (batchInsertAppListener == null) {
                    return;
                }
                if (aBoolean) {
                    batchInsertAppListener.batchInsertSuccess();
                } else {
                    batchInsertAppListener.batchInsertFail();
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }



    /**
     * 批量插入应用(检查数据库状态)
     * @param appList
     * @param batchInsertAppListener
     */
    @SuppressLint("StaticFieldLeak")
    public void batchInsertAppsCheckDb(final List<App> appList, final BatchInsertAppListener batchInsertAppListener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return AppRepository.getInstance().batchInsertOrUpdateAppCheckDb(appList);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (batchInsertAppListener == null) {
                    return;
                }
                if (aBoolean) {
                    batchInsertAppListener.batchInsertSuccess();
                } else {
                    batchInsertAppListener.batchInsertFail();
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }


    /**
     * 查询组织架构下的app列表
     * @param orgCode
     */
    @SuppressLint("StaticFieldLeak")
    public void getLocalAppsByOrgCode(final String orgCode, final OnQueryLocalAppListListener listener) {
        new AsyncTask<Void, Void, List<App>>() {
            @Override
            protected List<App> doInBackground(Void... params) {
                return AppRepository.getInstance().queryAllAppByOrgId(orgCode);
            }

            @Override
            protected void onPostExecute(List<App> apps) {
                if (listener == null) {
                    return;
                }
                listener.onAppSuccess(apps);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 查询当前组织的 app 列表
     */
    @SuppressLint("StaticFieldLeak")
    public void getCurrentLocalAccessApps(Context context, final OnQueryLocalAppListListener listener) {
        new AsyncTask<Void, Void, List<App>>() {
            @Override
            protected List<App> doInBackground(Void... params) {
                return AppRepository.getInstance().queryAccessApps(PersonalShareInfo.getInstance().getCurrentOrg(context));
            }

            @Override
            protected void onPostExecute(List<App> apps) {
                if (listener == null) {
                    return;
                }
                listener.onAppSuccess(apps);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    @SuppressLint("StaticFieldLeak")
    public void insertOrUpdateApp(final App app, final AddOrRemoveAppListener addOrRemoveAppListener) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return AppRepository.getInstance().insertOrUpdateApp(app);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (null != addOrRemoveAppListener) {
                    if (aBoolean) {
                        addOrRemoveAppListener.addOrRemoveSuccess();
                    } else {
                        addOrRemoveAppListener.addOrRemoveFail();
                    }
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }

    @SuppressLint("StaticFieldLeak")
    public void getApp(final String identifier, final QueryAppListener queryAppListener) {
        new AsyncTask<Void, Void, App>() {
            @Override
            protected App doInBackground(Void... params) {
                return AppRepository.getInstance().queryApp(identifier);
            }

            @Override
            protected void onPostExecute(App app) {
                queryAppListener.querySuccess(app);

            }
        }.executeOnExecutor(mDbExecutor);
    }

    @SuppressLint("StaticFieldLeak")
    public void queryLocalAppsByOrgId(final String orgId, final SearchAppListener listener) {
        new AsyncTask<Void, Void, List<App>>() {
            @Override
            protected List<App> doInBackground(Void... params) {
                return AppRepository.getInstance().queryAppsByOrgId(orgId);
            }

            @Override
            protected void onPostExecute(List<App> apps) {
                listener.searchSuccess("", apps);

            }
        }.executeOnExecutor(mDbExecutor);
    }

    public void updateAppBundle(final String appId, final String deletedBundleId, final AddOrRemoveAppListener addOrRemoveAppListener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                TabNoticeManager.getInstance().unregisterLightNotice(deletedBundleId);
                App app = AppRepository.getInstance().queryApp(appId);
                if (app.mBundles.isEmpty()) {
                    return true;
                }
                int deleteIndex = 0;
                for (AppBundles bundle: app.mBundles) {
                    if (bundle.mBundleId.equalsIgnoreCase(deletedBundleId)) {
                        break;
                    }
                    deleteIndex++;
                }
                app.mBundles.remove(deleteIndex);
                return AppRepository.getInstance().insertOrUpdateApp(app);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if(null != addOrRemoveAppListener) {

                    if (success) {
                        addOrRemoveAppListener.addOrRemoveSuccess();
                    } else {
                        addOrRemoveAppListener.addOrRemoveFail();
                    }
                }


            }
        }.executeOnExecutor(mDbExecutor);
    }

    @SuppressLint("StaticFieldLeak")
    public void removeApp(final String identifier, final AddOrRemoveAppListener addOrRemoveAppListener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                TabNoticeManager.getInstance().unregisterLightNotice(identifier);
                return AppRepository.getInstance().removeApp(identifier);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if(null != addOrRemoveAppListener) {

                    if (success) {
                        addOrRemoveAppListener.addOrRemoveSuccess();
                    } else {
                        addOrRemoveAppListener.addOrRemoveFail();
                    }
                }


            }
        }.executeOnExecutor(mDbExecutor);
    }



    @SuppressLint("StaticFieldLeak")
    public void searchApps(final String searchKey, final String searchValue, @Nullable final String orgCode, final SearchAppListener searchAppListener) {
        new AsyncTask<Void, Void, List<App>>() {
            @Override
            protected List<App> doInBackground(Void... params) {
                List<App> apps = AppRepository.getInstance().searchApps(searchValue, orgCode);
                return CollectionsKt.filter(apps, app -> app.isShowInMarket());
            }

            @Override
            protected void onPostExecute(List<App> appList) {
                searchAppListener.searchSuccess(searchKey, appList);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    public void searchBundles(final String searchKey, final String searchValue, final String orgId, final SearchAppBundlesListener listener) {
        new AsyncTask<Void, Void, List<AppBundles>>() {
            @Override
            protected List<AppBundles> doInBackground(Void... voids) {
                return AppRepository.getInstance().searchAppBundles(searchValue, orgId);
            }

            @Override
            protected void onPostExecute(List<AppBundles> appBundles) {
                if (listener == null) {
                    return;
                }
                listener.searchSuccess(searchKey, appBundles);
            }
        }.executeOnExecutor(mDbExecutor);
    }


    @SuppressLint("StaticFieldLeak")
    public void getK9MailApp(final QueryAppListener listener) {
        new AsyncTask<Void, Void, App>() {
            @Override
            protected App doInBackground(Void... params) {
                return AppRepository.getInstance().queryK9EmailApp();
            }

            @Override
            protected void onPostExecute(App app) {
                super.onPostExecute(app);
                listener.querySuccess(app);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    public interface OnQueryShortcutListListener {
        void onSuccess(List<Shortcut> shortcutList);
    }

    public interface OnQueryLocalAppListListener {
        void onAppSuccess(List<App> localAppList);
    }

    public interface BatchInsertAppListener {

        void batchInsertSuccess();

        void batchInsertFail();
    }

    public interface AddOrRemoveAppListener {

        void addOrRemoveSuccess();

        void addOrRemoveFail();
    }

    public interface SearchAppListener {
        void searchSuccess(String searchKey, List<App> appList);
    }

    public interface SearchAppBundlesListener {
        void searchSuccess(String searchKey, List<AppBundles> bundles);
    }

    public interface QueryAppListener {
        void querySuccess(App app);
    }
}
