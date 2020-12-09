package com.foreveross.atwork.api.sdk.organization;/**
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


import android.content.Context;

import com.foreveross.atwork.api.sdk.Employee.requestModel.SearchEmployeePost;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryEmployeeListResponse;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.BaseSyncNetService;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.organization.requstModel.AutoJoinOrgRequest;
import com.foreveross.atwork.api.sdk.organization.requstModel.CheckOrgsUpdatePostJson;
import com.foreveross.atwork.api.sdk.organization.requstModel.QueryOrganizationViewRequest;
import com.foreveross.atwork.api.sdk.organization.requstModel.SearchDepartmentRequest;
import com.foreveross.atwork.api.sdk.organization.responseModel.CheckOrgsUpdateResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeesTreeResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrgApplyingCheckResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationListResponse;
import com.foreveross.atwork.api.sdk.organization.responseModel.QueryOrgResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.RecursionEmpListResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.SearchDepartmentResp;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.Contact;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.google.gson.Gson;

/**
 * Created by reyzhang22 on 16/4/6.
 */
public class OrganizationSyncNetService extends BaseSyncNetService {

    private static final String TAG = OrganizationSyncNetService.class.getSimpleName();

    private static OrganizationSyncNetService sInstance = new OrganizationSyncNetService();

    private OrganizationSyncNetService() {

    }

    public static OrganizationSyncNetService getInstance() {
        return sInstance;
    }


    /**
     * 获取用户的组织架构信息
     *
     * @param context
     * @return
     */
    public HttpResult queryUserOrganizationsFromRemote(Context context) {
        final String url = String.format(UrlConstantManager.getInstance().V2_fetchOrganizationsUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if (httpResult.isNetSuccess()) {

            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, OrganizationListResponse.class));

        }

        return httpResult;
    }

    /**
     * 根据组织架构code获取该组织视图接口
     *
     * @return
     */
    public HttpResult queryOrganizationViewByCode(Context context, QueryOrganizationViewRequest queryOrganizationViewRequest) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String orgCode = queryOrganizationViewRequest.getOrgCode();
        boolean filterSenior = queryOrganizationViewRequest.getFilterSenior();
        boolean filterRank = queryOrganizationViewRequest.getFilterRank();
        boolean rankView = queryOrganizationViewRequest.getRankView();
        int orgLimit = queryOrganizationViewRequest.getQueryOrgViewRange().getOrgLimit();
        int orgSkip = queryOrganizationViewRequest.getQueryOrgViewRange().getOrgSkip();
        int employeeLimit = queryOrganizationViewRequest.getQueryOrgViewRange().getEmployeeLimit();
        int employeeSkip = queryOrganizationViewRequest.getQueryOrgViewRange().getEmployeeSkip();

        final String url = String.format(UrlConstantManager.getInstance().V2_fetchOrganizationViewByCodeUrl(), orgCode, filterSenior, filterRank, rankView, orgLimit, orgSkip, employeeLimit, employeeSkip, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, EmployeesTreeResponseJson.class));
        }

        return httpResult;
    }

    /**
     * 根据组织架构code和机构id获取下级视图
     *
     * @return
     */
    public HttpResult queryOrganizationViewByOrgId(Context context, QueryOrganizationViewRequest queryOrganizationViewRequest) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String orgCode = queryOrganizationViewRequest.getOrgCode();
        String orgId = queryOrganizationViewRequest.getOrgId();
        boolean filterSenior = queryOrganizationViewRequest.getFilterSenior();
        boolean filterRank = queryOrganizationViewRequest.getFilterRank();
        boolean rankView = queryOrganizationViewRequest.getRankView();
        int orgLimit = queryOrganizationViewRequest.getQueryOrgViewRange().getOrgLimit();
        int orgSkip = queryOrganizationViewRequest.getQueryOrgViewRange().getOrgSkip();
        int employeeLimit = queryOrganizationViewRequest.getQueryOrgViewRange().getEmployeeLimit();
        int employeeSkip = queryOrganizationViewRequest.getQueryOrgViewRange().getEmployeeSkip();

        final String url = String.format(UrlConstantManager.getInstance().V2_fetchOrganizationViewByOrgIdUrl(), orgCode, orgId, filterSenior, filterRank, rankView,  orgLimit, orgSkip, employeeLimit, employeeSkip, accessToken);
        final String urlWithDuplicateRemoval = UrlConstantManager.getInstance().appendApiWithduplicate_removal(url, true);
        HttpResult httpResult;
        if(AtworkConfig.DUPLICATE_REMOVAL){
            httpResult = HttpURLConnectionComponent.getInstance().getHttp(urlWithDuplicateRemoval);
        }else{
            httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        }

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, EmployeesTreeResponseJson.class));

        }

        return httpResult;
    }

    /**
     * 查询org_id下面的所有人员(包括子部门)
     * 包括子部门
     */
    public HttpResult queryRecursionEmployeeByOrgId(Context context, QueryOrganizationViewRequest queryOrganizationViewRequest) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String orgCode = queryOrganizationViewRequest.getOrgCode();
        String orgId = queryOrganizationViewRequest.getOrgId();
        boolean filterSenior = queryOrganizationViewRequest.getFilterSenior();
        boolean filterRank = queryOrganizationViewRequest.getFilterRank();
        boolean rankView = queryOrganizationViewRequest.getRankView();
        int orgLimit = queryOrganizationViewRequest.getQueryOrgViewRange().getOrgLimit();
        int orgSkip = queryOrganizationViewRequest.getQueryOrgViewRange().getOrgSkip();
        int employeeLimit = queryOrganizationViewRequest.getQueryOrgViewRange().getEmployeeLimit();
        int employeeSkip = queryOrganizationViewRequest.getQueryOrgViewRange().getEmployeeSkip();

        String url = String.format(UrlConstantManager.getInstance().V2_queryRecursionEmployeeByOrgIdUrl(), orgCode, orgId, filterSenior, filterRank, rankView, orgLimit, orgSkip, employeeLimit, employeeSkip, accessToken);
        final String urlWithDuplicateRemoval = UrlConstantManager.getInstance().appendApiWithduplicate_removal(url, true);

        HttpResult result;
        if(AtworkConfig.DUPLICATE_REMOVAL){
            result = HttpURLConnectionComponent.getInstance().getHttp(urlWithDuplicateRemoval);
        }else{
            result = HttpURLConnectionComponent.getInstance().getHttp(url);
        }

        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, RecursionEmpListResponseJson.class));
        }

        return result;
    }


    /**
     * 获取用户的组织架构信息
     *
     * @param context
     * @return
     */
    public HttpResult queryOrganizationsByUserIdFromRemote(Context context, String userId) {
        final String url = String.format(UrlConstantManager.getInstance().V2_fetchOrganizationsByUserIdUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context), userId);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, OrganizationListResponse.class));
        }

        return httpResult;
    }


    /**
     * 检查用户组织架构更新
     *
     * @param context
     * @param postJson
     * @return
     */
    public HttpResult checkOrganizationsUpdate(Context context, CheckOrgsUpdatePostJson postJson) {
        final String url = String.format(UrlConstantManager.getInstance().V2_checkOrgsUpdate(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(postJson));
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CheckOrgsUpdateResponseJson.class));
        }

        return httpResult;
    }



    /**
     * 删除组织
     *
     * @return
     */
    public HttpResult removeOrganization(Context context, String orgCode) {
        final String url = String.format(UrlConstantManager.getInstance().V2_removeOrgUrl(), orgCode, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().deleteHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }


    /**
     * 检查当前用户组织申请列表
     * */
    public HttpResult checkApplyingOrgs(Context context) {
        final String url = String.format(UrlConstantManager.getInstance().V2_checkApplyingOrgUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, OrgApplyingCheckResponseJson.class));
        }
        return httpResult;
    }

    /**
     * 查询组织
     * */
    public HttpResult queryOrgResult(Context context, String orgCode) {
        final String url = String.format(UrlConstantManager.getInstance().V2_queryOrgByCode(), orgCode, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryOrgResponseJson.class));
        }

        return httpResult;
    }

    public HttpResult searchDepartments(Context context, SearchDepartmentRequest searchDepartmentRequest) {
        final String url = String.format(UrlConstantManager.getInstance().queryDepartmentRemote(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(searchDepartmentRequest));
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, SearchDepartmentResp.class));
        }

        return httpResult;
    }

    public HttpResult fetchAutoJoinOrg(Context context){
        final String url = String.format(UrlConstantManager.getInstance().fetchAutoJoinOrg() , LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, OrganizationListResponse.class));
        }
        return httpResult;
    }

    public HttpResult autoJoinOrg(Context context, Organization organization){
        final String url = String.format(UrlConstantManager.getInstance().autoJoinOrg(),organization.mOrgCode,LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        LoginUserBasic loginUserBasic = LoginUserInfo.getInstance().getLoginUserBasic(context);
        final AutoJoinOrgRequest autoJoinOrgRequest = new AutoJoinOrgRequest(loginUserBasic.mName,"",loginUserBasic.mAvatar);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url,new Gson().toJson(autoJoinOrgRequest));
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }



}
