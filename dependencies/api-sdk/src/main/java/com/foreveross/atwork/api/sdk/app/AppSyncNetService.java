package com.foreveross.atwork.api.sdk.app;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.model.InstallOrDeleteAppJSON;
import com.foreveross.atwork.api.sdk.app.model.InstallOrRemoveAppResponseJson;
import com.foreveross.atwork.api.sdk.app.model.ServiceAppMenuResponseJSON;
import com.foreveross.atwork.api.sdk.app.requestJson.AppRequestJson;
import com.foreveross.atwork.api.sdk.app.requestJson.QueryAppListByAdminRequest;
import com.foreveross.atwork.api.sdk.app.requestJson.QueryAppListInAppStoreRequest;
import com.foreveross.atwork.api.sdk.app.responseJson.AppListResponseJson;
import com.foreveross.atwork.api.sdk.app.responseJson.CheckAppUpdateResponseJson;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppListByAdminResponse;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppListInAppStoreResponse;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 15/8/26.
 */
public class AppSyncNetService {

    private static final String TAG = AppSyncNetService.class.getSimpleName();

    private static AppSyncNetService sInstance = new AppSyncNetService();

    private AppSyncNetService() {

    }

    public static AppSyncNetService getInstance() {
        return sInstance;
    }


    /**
     * 同步
     * 安装 或者 删除APP
     * */
    public InstallOrRemoveAppResponseJson installOrRemoveAppFromRemote(Context context, InstallOrDeleteAppJSON appJSON) {
        String appUrl;
        if (appJSON.mInstall){
            appUrl = UrlConstantManager.getInstance().V2_installAppUrl();
        }else {
            appUrl = UrlConstantManager.getInstance().V2_removeAppUrl();
        }

        final String url = String.format(appUrl,
                LoginUserInfo.getInstance().getLoginUserId(context),
                LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        String postJson = new Gson().toJson(appJSON);
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, postJson);
        if (null == result || result.isNetError() || result.isNetFail()) {
            return null;
        }
//        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        return InstallOrRemoveAppResponseJson.toResponseJson(result.result);
    }



    /**
     * 获取组织下所有应用(组织管理员身份)
     * */
    public HttpResult queryAppsByAdminSync(Context context, QueryAppListByAdminRequest queryAppListByAdminRequest) {
        final String url = String.format(UrlConstantManager.getInstance().getAdminQueryAppsUrl(),
                queryAppListByAdminRequest.getOrgCode(),
                queryAppListByAdminRequest.getSkip(),
                queryAppListByAdminRequest.getLimit(),
                LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, true, QueryAppListByAdminResponse.class));
        }

        return result;
    }
    /**
     * 获取应用列表(应用市场接口)
     * */
    public HttpResult queryAppListInAppStoreSync(Context context, QueryAppListInAppStoreRequest queryAppListInAppStoreRequest) {
        final String url = String.format(UrlConstantManager.getInstance().getQueryAppListInAppStoreUrl(),
                LoginUserInfo.getInstance().getLoginUserId(context),
                queryAppListInAppStoreRequest.getOrgCode(),
                queryAppListInAppStoreRequest.getCategoryId(),
                queryAppListInAppStoreRequest.getKw(),
                queryAppListInAppStoreRequest.getSkip(),
                queryAppListInAppStoreRequest.getLimit(),
                LoginUserInfo.getInstance().getLoginUserAccessToken(context));


        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, true, QueryAppListInAppStoreResponse.class));
        }

        return result;

    }

    /**
     * 获取用户组织架构下的应用
     *
     * @param context
     * @param orgCode
     * @return
     */
    public HttpResult queryUserAppsFromRemote(Context context, String orgCode) {
        final String url = String.format(UrlConstantManager.getInstance().V2_fetchUserAppsByOrgId(),
                LoginUserInfo.getInstance().getLoginUserId(context),
                orgCode,
                LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, true, AppListResponseJson.class));
        }


        return result;
    }

    /**
     * 批量检查应用更新
     *
     * @param context
     * @param accessList
     * @param adminList
     * @param orgId
     * @return httpResult
     */
    public HttpResult checkUserAppsUpdateFromRemote(Context context, List<App> accessList, List<App> adminList, String orgId) {
        final String url = String.format(UrlConstantManager.getInstance().V2_checkUserAppUpdateList(),
                LoginUserInfo.getInstance().getLoginUserId(context),
                orgId,
                LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, AppRequestJson.makeAppRequest(orgId, accessList, adminList).toString());
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, true, CheckAppUpdateResponseJson.class));
        }

        return httpResult;
    }


    public HttpResult queryAppInfoFromRemote(Context context, String appId, String orgId) {
        if(StringUtils.isEmpty(orgId)) {
            orgId = PersonalShareInfo.getInstance().getCurrentOrg(context);
        }

        String url = String.format(UrlConstantManager.getInstance().getGetAppInfoFromRemote(), LoginUserInfo.getInstance().getLoginUserId(context),
                appId, LoginUserInfo.getInstance().getLoginUserAccessToken(context), orgId);

        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, true, QueryAppResponse.class));
        }

        try {
            Logger.e("app", "app request -> " + url);
            Logger.e("app", "app response -> " + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    /**
     * 注意，这个是返回一个url地址
     * @param context
     * @return
     */
    public String shareAppUrl(Context context) {
        String url = String.format(UrlConstantManager.getInstance().V2_shareWorkplus(), "URL", LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (null == result || result.isNetError() || result.isNetFail()) {
            return "";
        }
        int resultCode = NetWorkHttpResultHelper.getResultStatus(result.result);
        if (resultCode != 0) {
            return "";
        }
        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        try {
            JSONObject jsonObject = new JSONObject(resultText);
            return jsonObject.optString("content");
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return "";
    }

    public List<ServiceApp.ServiceMenu> queryServiceAppMenu(Context context, final String appId) {

        final String getMenuUrl = String.format(UrlConstantManager.getInstance().V2_getGetMenuUrl(), LoginUserInfo.getInstance().getLoginUserId(context),
                appId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        List<ServiceApp.ServiceMenu> serviceMenuList = new ArrayList<>();

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(getMenuUrl);
        if (httpResult.isNetSuccess()) {
            ServiceAppMenuResponseJSON serviceAppMenuResponseJSON = JsonUtil.fromJson(httpResult.result, ServiceAppMenuResponseJSON.class);
            if (serviceAppMenuResponseJSON != null && serviceAppMenuResponseJSON.status == 0) {
                serviceMenuList = serviceAppMenuResponseJSON.toServiceMenuList();
            }
        }

        return serviceMenuList;
    }

    public boolean autoPunchInWifi(String url, String params) {
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url , params);
        try {
            JSONObject jsonObject = new JSONObject(httpResult.result);
            String status = jsonObject.optString("status");
            if ("ok".equalsIgnoreCase(status)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public int outFieldIntervalPunch(String url, String params) {
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url , params);
        try {
            JSONObject jsonObject = new JSONObject(httpResult.result);
            String status = jsonObject.optString("status");
            if (!"ok".equalsIgnoreCase(status)) {
                return -1;
            }
            JSONObject resultObject = jsonObject.optJSONObject("results");
            return resultObject.optInt("pollingTimeOfMinutes", -1);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


}
