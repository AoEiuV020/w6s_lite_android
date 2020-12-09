package com.foreveross.atwork.api.sdk.app;

import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.model.InstallOrDeleteAppJSON;
import com.foreveross.atwork.api.sdk.app.model.InstallOrRemoveAppResponseJson;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;

import java.util.List;

/**
 * Created by lingen on 15/5/6.
 * Description:
 * APP网络请求
 */
public class AppAsyncNetService {

    private Context context;

    private UrlConstantManager mUrlConstantManager = UrlConstantManager.getInstance();


    public AppAsyncNetService(Context context) {
        this.context = context.getApplicationContext();
    }



    /***
     * 异步
     * 安装 或者 删除APP
     *
     * @param isInstall 是否安装
     */
    public void asyncInstallOrRemoveAppFromRemote(final Context context, final String orgId, final List<InstallOrDeleteAppJSON.AppEntrances> appEntrances, final boolean isInstall, final boolean manual, final AddOrRemoveAppListener addOrRemoveAppListener) {
        if (appEntrances.isEmpty()) {
            addOrRemoveAppListener.networkFail(-2000, "app list is empty");
            return;
        }
        new AsyncTask<Void, Void, InstallOrRemoveAppResponseJson>() {

            @Override
            protected InstallOrRemoveAppResponseJson doInBackground(Void... params) {
                InstallOrDeleteAppJSON request = InstallOrDeleteAppJSON.createInstance(appEntrances, orgId, isInstall, manual);
                return AppSyncNetService.getInstance().installOrRemoveAppFromRemote(context, request);
            }

            @Override
            protected void onPostExecute(InstallOrRemoveAppResponseJson responseJson) {

                if (responseJson != null && responseJson.status == 0) {
                    addOrRemoveAppListener.addOrRemoveSuccess(responseJson);
                    return;
                }
                if (responseJson == null) {
                    addOrRemoveAppListener.networkFail(-1,"");
                }


            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    public void autoPunchInWifi(final String url, final String requestParams, final OnWifiPunchListener listener) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return  AppSyncNetService.getInstance().autoPunchInWifi(url, requestParams);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (listener != null) {
                    listener.onWifiPunch(success);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void outFieldIntervalPunch(final String url, final String requestParams, final OnOutFieldIntervalListener listener) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return AppSyncNetService.getInstance().outFieldIntervalPunch(url, requestParams);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (listener == null) {
                    return;
                }
                listener.onOutFieldInterval(integer);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void shareWorkplusUrl(final Context context, final OnDataResultListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return AppSyncNetService.getInstance().shareAppUrl(context);
            }

            @Override
            protected void onPostExecute(String s) {
                if (listener == null) {
                    return;
                }
                listener.onDataResult(s);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }




    public interface GetServiceAppMenuListener extends NetWorkFailListener {

        void getMenuSuccess(List<ServiceApp.ServiceMenu> menus);

    }

    public interface AddOrRemoveAppListener extends NetWorkFailListener {

        void addOrRemoveSuccess(InstallOrRemoveAppResponseJson json);

    }

    public interface OnDataResultListener {
        void onDataResult(Object... objectses);
    }

    public interface OnOutFieldIntervalListener {
        void onOutFieldInterval(int outFieldInterval);
    }

    public interface OnWifiPunchListener {
        void onWifiPunch(boolean success);
    }
}
