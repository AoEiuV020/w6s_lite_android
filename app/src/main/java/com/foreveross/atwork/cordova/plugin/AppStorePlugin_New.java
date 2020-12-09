package com.foreveross.atwork.cordova.plugin;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.foreverht.db.service.repository.AppRepository;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.AppAsyncNetService;
import com.foreveross.atwork.api.sdk.app.model.InstallOrDeleteAppJSON;
import com.foreveross.atwork.api.sdk.app.model.InstallOrRemoveAppResponseJson;
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppItemResultByAdmin;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.AdminQueryAppRequest;
import com.foreveross.atwork.cordova.plugin.model.RouteActionRequest;
import com.foreveross.atwork.cordova.plugin.model.RouteRequest;
import com.foreveross.atwork.cordova.plugin.model.SetAppCustomSortRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppType;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.model.GetAppListRequest;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.main.activity.AppActivity;
import com.foreveross.atwork.modules.route.manager.RouteActionConsumer;
import com.foreveross.atwork.modules.route.model.RouteParams;
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminModifyAppEntryActivity;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2015/12/2.
 */
public class AppStorePlugin_New extends WorkPlusCordovaPlugin {
    private CallbackContext mContextCall;


    //安装应用接口
    private static final String APP_INSTALL_ACTION = "installApp";

    //卸载应用接口
    private static final String APP_REMOVE_ACTION = "removeApp";

    private static final String APP_ROUTE = "route";

    private static final String APP_ROUTE_ACTION = "routeAction";

    private static final String SHOW_APP_LIST_BY_ID = "showAppListById";

    private static final String ACTION_OPEN_APPSTORE = "openAppStore";

    private static final String ACTION_GET_APP_LIST = "getAppList";

    private static final String ACTION_SET_APP_CUSTOM_SORT = "setAppCustomSort";

    private static final String ACTION_INVITE_TO_THE_MEETING_BY_SHORT_MESSAGE = "inviteToTheMeetingByShortMessage";


    private static final String ACTION_ADMIN_QUERY_APP = "adminQueryApp";

    private static final String ACTION_ADMIN_REFRESH_APPS = "adminRefreshApps";


    @Override
    public boolean execute(String action, JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        mContextCall = callbackContext;

        if (APP_INSTALL_ACTION.equals(action)) {
            final JSONObject json = jsonArray.optJSONObject(0);
            cordova.getThreadPool().execute(() -> onAppInstallOrRemove(json, false));
            return true;
        }

        if (APP_REMOVE_ACTION.equals(action)) {
            final JSONObject json = jsonArray.optJSONObject(0);
            cordova.getThreadPool().execute(() -> onAppInstallOrRemove(json, true));
            return true;
        }

        if (APP_ROUTE.equals(action)) {
            onAppRoute(jsonArray, callbackContext);
            return true;
        }

        if (ACTION_INVITE_TO_THE_MEETING_BY_SHORT_MESSAGE.equals(action)) {
            inviteToTheMeetingByShortMessage(jsonArray, callbackContext);
            return true;
        }

        if(APP_ROUTE_ACTION.equals(action)) {
            onAppRouteAction(jsonArray, callbackContext);
            return true;
        }

        if (SHOW_APP_LIST_BY_ID.equalsIgnoreCase(action)) {
            showAppListById(jsonArray);
            return true;
        }

        if (ACTION_OPEN_APPSTORE.equalsIgnoreCase(action)) {
            openAppStore();
            return true;
        }


        if (ACTION_GET_APP_LIST.equalsIgnoreCase(action)) {
            getAppList(jsonArray, callbackContext);
            return true;
        }

        if (ACTION_SET_APP_CUSTOM_SORT.equalsIgnoreCase(action)) {
            setAppCustomSort(jsonArray, callbackContext);
            return true;
        }

        if(ACTION_ADMIN_QUERY_APP.equalsIgnoreCase(action)) {
            adminQueryApp(jsonArray, callbackContext);
            return true;
        }

        if(ACTION_ADMIN_REFRESH_APPS.equalsIgnoreCase(action)) {
            adminRefreshApps(callbackContext);
            return true;
        }

        return false;
    }

    private void adminRefreshApps(CallbackContext callbackContext) {
        WorkbenchAdminModifyAppEntryActivity.refreshAppBundleListTotally();
        callbackContext.success();
    }

    /**
     * 应用安装网络请求
     *
     * @param json
     * @param isDelete true 删除， false 安装
     */
    private void onAppInstallOrRemove(JSONObject json, final boolean isDelete) {

        InstallOrDeleteAppJSON request = new Gson().fromJson(json.toString(), InstallOrDeleteAppJSON.class);
        if (request != null) {
            installOrRemoveRemote(isDelete, request);
        } else {
            mContextCall.error();
        }

    }

    private void installOrRemoveRemote(final boolean isDelete, InstallOrDeleteAppJSON request) {
        AppAsyncNetService service = new AppAsyncNetService(BaseApplicationLike.baseContext);
        String currentOrg = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);

        service.asyncInstallOrRemoveAppFromRemote(BaseApplicationLike.baseContext, currentOrg, request.mAppEntrances,
                !isDelete, true, new AppAsyncNetService.AddOrRemoveAppListener() {
                    @Override
                    public void addOrRemoveSuccess(InstallOrRemoveAppResponseJson json) {
                        onAddOrRemoveSuccess(json, currentOrg, isDelete);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        boolean result = ErrorHandleUtil.handleBaseError(errorCode, errorMsg);

                        if (!result) {
                            doError(isDelete);
                        }
                    }
                });
    }

    private void onAddOrRemoveSuccess(InstallOrRemoveAppResponseJson json, String currentOrg, boolean isDelete) {
        try {

            if (null != json.result) {
                AppManager.getInstance().queryRemoteApp(cordova.getActivity(), json.result.get(0).appId, currentOrg, new AppManager.GetAppFromMultiListener() {
                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        boolean result = ErrorHandleUtil.handleBaseError(errorCode, errorMsg);

                        if (!result) {
                            doError(isDelete);
                        }
                    }

                    @Override
                    public void onSuccess(@NonNull App app) {
                        app.mAppType = AppType.Access;
                        app.mNewVersionNotice = true;
                        AppRepository.getInstance().insertOrUpdateApp(app);
                        doSuccess(isDelete);
                        AppRefreshHelper.refreshAppAbsolutely();
                    }
                });

            }


        } catch (Exception e) {
            e.printStackTrace();

            doError(isDelete);
        }
    }


    private void onAppRouteAction(JSONArray jsonArray, CallbackContext callbackContext) {
        RouteActionRequest routeRequest = NetGsonHelper.fromCordovaJson(jsonArray, RouteActionRequest.class);
        if(null == routeRequest || null == routeRequest.getRouteAction()) {
            callbackContext.error();
            return;
        }

        Intent intent = new Intent(routeRequest.getRouteAction().getAction());
        cordova.getActivity().startActivity(intent);

    }

    private void onAppRoute(JSONArray jsonArray, CallbackContext callbackContext) {
        RouteRequest routeRequest = NetGsonHelper.fromCordovaJson(jsonArray, RouteRequest.class);
        if (null != routeRequest) {
            try {
                if (null != routeRequest.mExplicitIntent) {

                    if (StringUtils.isEmpty(routeRequest.mSchemeUrl)) {
                        onAppRouteIntent(routeRequest);
                        callbackContext.success();

                        return;

                    }

                    onAppRouteSchemeUrlAndCheckAppRouteIntent(routeRequest, callbackContext);

                    return;

                }

                onAppRouteSchemeUrl(routeRequest);
                callbackContext.success();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        callbackContext.error();
    }
    private void inviteToTheMeetingByShortMessage(JSONArray jsonArray, CallbackContext callbackContext) {
        RouteRequest routeRequest = NetGsonHelper.fromCordovaJson(jsonArray, RouteRequest.class);

            try {
                jumpToShoreMessage(routeRequest.mContent);
                callbackContext.success();

            } catch (Exception e) {
                e.printStackTrace();
            }

        callbackContext.error();
    }

    private void onAppRouteSchemeUrlAndCheckAppRouteIntent(RouteRequest routeRequest, CallbackContext callbackContext) {
        try {
            onAppRouteSchemeUrl(routeRequest);
            callbackContext.success();

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

            onAppRouteIntent(routeRequest);
            callbackContext.success();
        }
    }

    private void onAppRouteSchemeUrl(RouteRequest routeRequest) throws android.content.ActivityNotFoundException {
        Uri weburi = Uri.parse(routeRequest.mSchemeUrl);
        RouteParams routeParams = RouteParams.Companion.newRouteParams().uri(weburi).build();

        boolean result = RouteActionConsumer.INSTANCE.route(cordova.getActivity(), routeParams);

        if(!result) {
            Uri uri = Uri.parse(routeRequest.mSchemeUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (null != routeRequest.mExplicitIntent && !StringUtils.isEmpty(routeRequest.mExplicitIntent.mPackage)) {
                intent.setPackage(routeRequest.mExplicitIntent.mPackage);
            }
            cordova.getActivity().startActivity(intent);
        }

    }

    /**
     * 发送短信
     * @param content
     * @throws android.content.ActivityNotFoundException
     */
    private void jumpToShoreMessage(String  content) throws android.content.ActivityNotFoundException {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:"));
        sendIntent.putExtra("sms_body", content);

        cordova.getActivity().startActivity(sendIntent);

    }

    private void onAppRouteIntent(RouteRequest routeRequest) throws android.content.ActivityNotFoundException {
        Intent intent;

        if (StringUtils.isEmpty(routeRequest.mExplicitIntent.mInitUrl)) {
            PackageManager packageManager = BaseApplicationLike.baseContext.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(routeRequest.mExplicitIntent.mPackage);

        } else {
            intent = new Intent();
            ComponentName componentName = new ComponentName(routeRequest.mExplicitIntent.mPackage, routeRequest.mExplicitIntent.mInitUrl);
            intent.setComponent(componentName);
        }


        if (!MapUtil.isEmpty(routeRequest.mExplicitIntent.mParams)) {
            for (Map.Entry<String, String> entry : routeRequest.mExplicitIntent.mParams.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        cordova.getActivity().startActivity(intent);

    }

    private void showAppListById(JSONArray jsonArray) {
        String orgId = jsonArray.optJSONObject(0).optString("org_id");
        if (TextUtils.isEmpty(orgId)) {
            orgId = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());
        }
        if (TextUtils.isEmpty(orgId)) {
            mContextCall.error();
            return;
        }

        cordova.getActivity().startActivity(AppActivity.getIntent(cordova.getActivity(), orgId));
    }


    public void doSuccess(boolean isDelete) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (isDelete) {
                jsonObject.put("message", "应用删除成功");
            } else {
                jsonObject.put("message", "安装成功");
            }

            jsonObject.put("status", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mContextCall.success(jsonObject);
    }

    public void doError(boolean isDelete) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (isDelete) {
                jsonObject.put("message", "应用删除失败");
            } else {
                jsonObject.put("message", "安装失败");
            }
            jsonObject.put("status", -1);

            mContextCall.error(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAppStore() {
        String orgId = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());
        // 可以认为它没有任何组织
        if (TextUtils.isEmpty(orgId)) {
            AtworkAlertDialog alert = new AtworkAlertDialog(cordova.getActivity());
            alert.setContent(cordova.getActivity().getString(R.string.please_create_org))
                    .hideDeadBtn()
                    .setBrightBtnText(cordova.getActivity().getString(R.string.ok))
                    .setTitleText(cordova.getActivity().getString(R.string.tip))
                    .setClickBrightColorListener(dialog -> alert.dismiss())
                    .setCanceledOnTouchOutside(false);
            alert.show();
            return;
        }
        String url = String.format(UrlConstantManager.getInstance().getAppStoreUrl(), PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity()), LoginUserInfo.getInstance().getLoginUserId(cordova.getActivity()), false);

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url);
        Intent intent = WebViewActivity.getIntent(cordova.getActivity(), webViewControlAction);
        cordova.getActivity().startActivity(intent);
    }

    private void getAppList(JSONArray jsonArray, CallbackContext callbackContext) {
        GetAppListRequest getAppListRequest = NetGsonHelper.fromCordovaJson(jsonArray, GetAppListRequest.class);
        if (null == getAppListRequest) {
            getAppListRequest = new GetAppListRequest();
        }


    }

    private void setAppCustomSort(JSONArray jsonArray, CallbackContext callbackContext) {
        SetAppCustomSortRequest setAppCustomSortRequest = NetGsonHelper.fromCordovaJson(jsonArray, SetAppCustomSortRequest.class);

    }

    private void adminQueryApp(JSONArray jsonArray, CallbackContext callbackContext) {
        AdminQueryAppRequest adminQueryAppRequest =  NetGsonHelper.fromCordovaJson(jsonArray, AdminQueryAppRequest.class);

        if(null == adminQueryAppRequest) {
            callbackContext.error();
            return;
        }

        QueryAppItemResultByAdmin appItemResult = AppManager.getInstance().queryAppItemResultCache(adminQueryAppRequest.getAppId());
        if(null == appItemResult) {
            callbackContext.error();
            return;
        }

        if(null == appItemResult.getRawResultData()) {
            callbackContext.error();
            return;
        }

        try {
            callbackContext.success(new JSONObject(appItemResult.getRawResultData().toString()));
        } catch (Exception e) {
            e.printStackTrace();

            callbackContext.error();
            return;
        }

    }


}

