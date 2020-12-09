package com.foreveross.atwork.api.sdk.Employee;

import android.content.Context;

import com.foreveross.atwork.api.sdk.Employee.requestModel.CallerRequester;
import com.foreveross.atwork.api.sdk.Employee.requestModel.SearchEmployeePost;
import com.foreveross.atwork.api.sdk.Employee.responseModel.CallerResponser;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryEmployeeListResponse;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryEmployeeResponseJson;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryIntersectionOrgCodesResponse;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryOrgAndEmpListResponse;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.BaseSyncNetService;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.w6s.model.incomingCall.IncomingCaller;

import java.util.List;

/**
 * Created by shadow on 2016/4/21.
 */
public class EmployeeSyncNetService extends BaseSyncNetService {

    private static final String TAG = EmployeeSyncNetService.class.getSimpleName();

    private static EmployeeSyncNetService sInstance = new EmployeeSyncNetService();

    private EmployeeSyncNetService() {

    }

    public static EmployeeSyncNetService getInstance() {
        return sInstance;
    }

    /**
     * 查询用户间的组织交集(orgCode list)
     * */
    public HttpResult queryIntersectionOrgCodes(Context context, String userId) {
        String url = String.format(UrlConstantManager.getInstance().V2_queryIntersectionOrgCodes(), userId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, QueryIntersectionOrgCodesResponse.class));
        }
        return result;
    }

    /**
     * 查看雇员信息
     */
    public Employee queryEmployeeInfo(Context context, String code, String employeeId) {
        String url = String.format(UrlConstantManager.getInstance().V2_queryEmployeeInfoUrl(), code, employeeId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (isHttpError(result)) {
            return null;
        }
        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        return new Gson().fromJson(resultText, Employee.class);
    }


    /**
     * 查看雇员信息
     *
     * @param userId 雇员 user_id
     */
    public HttpResult queryUserEmployeeInfo(Context context, String code, String userId) {
        String url = String.format(UrlConstantManager.getInstance().V2_queryUserEmployeeInfoUrl(), code, userId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, QueryEmployeeResponseJson.class));
        }
        return result;
    }


    /**
     * 查询org_id下面的所有人员
     * 不包括子部门
     */
    public List<Employee> queryEmployeeByOrgId(Context context, String code, String orgId) {
        String url = String.format(UrlConstantManager.getInstance().V2_queryEmployeeByOrgIdUrl(), code, LoginUserInfo.getInstance().getLoginUserAccessToken(context), orgId);
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (isHttpError(result)) {
            return null;
        }
        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        return new Gson().fromJson(resultText, new TypeToken<List<Employee>>() {
        }.getType());
    }

    /**
     * 根据user_id 查询组织以及所属的雇员(根据同事关系而返回结果)
     *
     * @param context
     * @param userId
     * @param filterRank
     * @param rankView
     *
     * return httpResult
     * */
    public HttpResult queryOrgAndEmpList(Context context, String userId, boolean filterRank, boolean rankView) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().V2_queryOrgAndEmpList(), userId, filterRank, rankView, accessToken);
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, QueryOrgAndEmpListResponse.class));

        }

        return result;
    }

    /**
     * 根据条件搜索雇员
     *
     * @param context
     * @param searchEmployeePost
     */
    public HttpResult searchEmployees(Context context, SearchEmployeePost searchEmployeePost) {
        final String url = String.format(UrlConstantManager.getInstance().V2_searchEmployeeListMultiCodeUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(searchEmployeePost));
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryEmployeeListResponse.class));

        }

        return httpResult;
    }

    /**
     * 修改雇员
     */
    public HttpResult modifyEmployeeInfo(Context context, String orgCode, String employeeId, String postPrams) {
        String postUrl = String.format(UrlConstantManager.getInstance().V2_modifyEmployee(), orgCode, employeeId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(postUrl, postPrams);

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }

    /**
     * 批量查询雇员信息
     * */
    public HttpResult queryEmpListFromRemote(Context context, List<String> unExistsUserIds, String orgCode) {
        StringBuilder sb = new StringBuilder();
        for(String id : unExistsUserIds) {
            sb.append(id).append(",");
        }

        final String url = String.format(UrlConstantManager.getInstance().V2_fetchEmpListUrl(), orgCode, sb.toString(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryEmployeeListResponse.class));
        }

        return httpResult;
    }

    public HttpResult fetchEmpIncomingCallRemote(Context context, CallerRequester requester) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_fetchEmpIncomingCallUrl(), requester.mUserId, requester.mSkip, requester.mLimit, requester.mRefreshTime,accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CallerResponser.class));
        }

        return httpResult;
    }



}
