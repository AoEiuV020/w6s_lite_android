package com.foreveross.atwork.cordova.plugin;


import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.organization.OrganizationSyncNetService;
import com.foreveross.atwork.api.sdk.organization.requstModel.QueryOrganizationViewRequest;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeesTreeResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.component.OrgSwitchDialog;
import com.foreveross.atwork.cordova.plugin.model.GetCurrentOrgCodeResponse;
import com.foreveross.atwork.cordova.plugin.model.SearchDiscussionResponse;
import com.foreveross.atwork.cordova.plugin.model.SelectOrganizationRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.utils.CordovaHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2016/10/31.
 */

public class OrgPlugin extends WorkPlusCordovaPlugin {
    /**
     * 获取当前组织 code
     */
    private static final String GET_CURRENT_ORG_CODE = "getCurrentOrgCode";

    private static final String GET_CURRENT_ORG = "getCurrentOrg";

    private static final String POP_SWITCH_ORG = "popSwitchOrg";

    /**
     * 获取个人组织架构
     */
    private static final String GET_ORG_STRUCTURE = "getOrgStructure";
    /**
     * 获取组织列表
     */
    private static final String GET_ORG_LIST = "getOrganizationList";
    /**
     * 雇员搜索
     */
    private static final String SEARCH_EMPLOYEE = "searchEmployee";
    /**
     * 获取登录用户 org list
     */
    private static final String QUERY_LOGIN_ORG_LIST = "queryLoginOrgList";




    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        if(GET_CURRENT_ORG_CODE.equals(action)) {
            GetCurrentOrgCodeResponse getCurrentOrgCodeResponse = new GetCurrentOrgCodeResponse();
            getCurrentOrgCodeResponse.mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());
            CordovaHelper.doSuccess(getCurrentOrgCodeResponse, callbackContext);
            return true;
        }


        if(GET_CURRENT_ORG.equals(action)) {
           OrganizationManager.getInstance().queryOrg(AtworkApplicationLike.baseContext, null, new BaseNetWorkListener<Organization>() {
               @Override
               public void onSuccess(Organization organization) {
                   callbackContext.success(organization);

               }

               @Override
               public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                    callbackContext.error();
               }
           });
           return true;
        }

        if(POP_SWITCH_ORG.equals(action)) {
            OrganizationManager manager = OrganizationManager.getInstance();
            manager.getLocalOrganizations(cordova.getActivity(), localData -> {
                if (localData == null) {
                    return;
                }
                List<Organization> orgList = (List<Organization>) localData[0];
                if (ListUtil.isEmpty(orgList)) {
                    return;
                }

                OrgSwitchDialog orgSwitchDialog = new OrgSwitchDialog();
                orgSwitchDialog.setData(OrgSwitchDialog.Type.DEFAULT, AtworkApplicationLike.getResourceString(R.string.switch_orgs));
                orgSwitchDialog.setOrgData(orgList);

                orgSwitchDialog.setItemOnClickListener(organization -> {
                    orgSwitchDialog.dismiss();
                    OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(BaseApplicationLike.baseContext, organization.mOrgCode, true);

                    callbackContext.success(organization);

                });
                if(cordova.getActivity() instanceof FragmentActivity) {
                    orgSwitchDialog.show(((FragmentActivity)cordova.getActivity()).getSupportFragmentManager(), "org_switch");

                }
            });

            return true;
        }


        //获取个人组织架构
        if(GET_ORG_STRUCTURE.equals(action)) {
            cordova.getThreadPool().execute(() -> {
                SelectOrganizationRequest selectOrganizationRequest = NetGsonHelper.fromCordovaJson(rawArgs, SelectOrganizationRequest.class);
                QueryOrganizationViewRequest queryOrganizationViewRequest = new QueryOrganizationViewRequest();
                if (checkIllegal(selectOrganizationRequest)){
                   callbackContext.error();
                   return;
                }
                queryOrganizationViewRequest.setOrgCode(selectOrganizationRequest.orgCode);
                queryOrganizationViewRequest.setOrgId(selectOrganizationRequest.orgId);
                HttpResult httpResult =  OrganizationSyncNetService.getInstance().queryOrganizationViewByOrgId(cordova.getActivity(), queryOrganizationViewRequest);
                getOrgStructure(callbackContext, httpResult, true);
            });

            return true;
        }

        //获取组织列表
        if(GET_ORG_LIST.equals(action)) {
            OrganizationManager.getInstance().getLocalOrganizations(cordova.getActivity(), localData -> {
                List<Organization> organizations = (List<Organization>) localData[0];
                getOrganizationList(callbackContext,organizations,true);
            });
            return true;
        }

        //雇员搜索
        if(SEARCH_EMPLOYEE.equals(action)) {
            SearchDiscussionResponse searchDiscussionResponse = NetGsonHelper.fromCordovaJson(rawArgs, SearchDiscussionResponse.class);
            if(searchDiscussionResponse != null && !TextUtils.isEmpty(searchDiscussionResponse.getSearchValue())) {
                searchEmployeesRemote(callbackContext,new ArrayList<>(),searchDiscussionResponse.getSearchValue());
            }
            return true;
        }

        return false;
    }

    private boolean checkIllegal(SelectOrganizationRequest selectOrganizationRequest) {
        if(null == selectOrganizationRequest) {
            return true;
        }

        if(StringUtils.isEmpty(selectOrganizationRequest.orgCode)) {
            return true;
        }

        if(StringUtils.isEmpty(selectOrganizationRequest.orgId)) {
            return true;
        }

        return false;
    }

    /**
     * 获取个人组织架构
     */
    public void getOrgStructure(CallbackContext callbackContext, HttpResult httpResult ,Boolean success){
        AtworkApplicationLike.runOnMainThread(() -> {
            if (!httpResult.isRequestSuccess()) {
                callbackContext.error();
                return;
            }

            try {
                if (success) {
                    EmployeesTreeResponseJson employeesTreeResponseJson = (EmployeesTreeResponseJson) httpResult.resultResponse;
                    List<OrganizationResult> organizationList;
                    organizationList = employeesTreeResponseJson.result;

                    JSONObject jsonObject = new JSONObject();
                    Gson gson = new Gson();
                    JSONArray jsonArray = new JSONArray(gson.toJson(organizationList));
                    jsonObject.put("code", 0);
                    jsonObject.put("message", "获取成功");
                    jsonObject.put("data", jsonArray);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", -1);
                    jsonObject.put("message", "获取失败");
                    jsonObject.put("data", "");
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 获取组织列表
     */
    private void getOrganizationList(CallbackContext callbackContext, List<Organization>  organizationList ,Boolean success) {
        AtworkApplicationLike.runOnMainThread(() -> {
            Gson gson = new Gson();
            try {
                if(success) {
                    JSONArray jsonArray = new JSONArray(gson.toJson(organizationList));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", 0);
                    jsonObject.put("message", "搜索成功");
                    jsonObject.put("data", jsonArray);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", -1);
                    jsonObject.put("message", "搜索失败");
                    jsonObject.put("data", "");
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    //搜索雇员
    public void searchEmployeesRemote(CallbackContext callbackContext,List<ShowListItem> localResultList,String searchValue) {

        //全局搜索入口, 需要根据配置设置是否需要视图权限控制
        ExpandEmpTreeAction expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction()
                .setSelectMode(false)
                .setViewAcl(AtworkConfig.SEARCH_CONFIG.isEmployeeViewAcl());


        //根据搜索key搜索雇员列表
        EmployeeManager.getInstance().searchEmployeesRemote(AtworkApplicationLike.baseContext, "", searchValue, expandEmpTreeAction, new EmployeeManager.RemoteSearchEmployeeListListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                    if(ListUtil.isEmpty(localResultList)) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("code", -1);
                            jsonObject.put("message", "搜索失败");
                            jsonObject.put("data", "");
                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                            callbackContext.sendPluginResult(pluginResult);
                            callbackContext.success();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onSuccess(String searchKeyCallBack, List<Employee> employeeList) {
                List<ShowListItem> itemList = new ArrayList<>();
                itemList.addAll(localResultList);
                itemList.addAll(employeeList);
                Gson gson = new Gson();
                try {
                    JSONArray jsonArray = new JSONArray(gson.toJson(itemList));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", 0);
                    jsonObject.put("message", "搜索成功");
                    jsonObject.put("data", jsonArray);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        });
    }

}
