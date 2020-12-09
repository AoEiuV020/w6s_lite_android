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
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrgApplyingCheckResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.api.sdk.organization.responseModel.QueryOrgResponseJson;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;

import java.util.List;

/**
 * Created by reyzhang22 on 16/4/6.
 */
public class OrganizationAsyncNetService {

    private static final String TAG = OrganizationAsyncNetService.class.getSimpleName();

    private static OrganizationAsyncNetService sInstance = new OrganizationAsyncNetService();

    public static final String TOP = "-1";

    public static final int TOP_LEVEL = 0;

    public static final int MAX_QUERY_NUM = 100;

    private OrganizationAsyncNetService() {

    }

    public static OrganizationAsyncNetService getInstance() {
        return sInstance;
    }


    /**
     * 查询组织
     * */
    public void queryOrgFromRemote(final Context context, final String orgCode, final OnQueryOrgListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return OrganizationSyncNetService.getInstance().queryOrgResult(context, orgCode);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    Organization organization = ((QueryOrgResponseJson)httpResult.resultResponse).organization;
                    listener.onSuccess(organization);


                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }





    public void checkApplyingOrgs(final Context context, final OnCheckOrgApplyingListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void ...parms) {
                HttpResult httpResult = OrganizationSyncNetService.getInstance().checkApplyingOrgs(context);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    OrgApplyingCheckResponseJson responseJson = (OrgApplyingCheckResponseJson) httpResult.resultResponse;
                    listener.onSuccess(responseJson.resultList);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 查询组织接口
     * */
    public interface OnQueryOrgListener extends NetWorkFailListener{
        void onSuccess(Organization org);
    }


    public interface OnCheckOrgApplyingListener extends NetWorkFailListener{
        void onSuccess(List<OrgApplyingCheckResponseJson.ApplyingOrg> applyingOrgList);
    }


    public interface OnQueryEmployeeListener extends NetWorkFailListener{

        void onSuccess(List<Employee> employeeList);

    }

    public interface OnQueryOrgListListener extends NetWorkFailListener{
        void onSuccess(List<Organization> organizationList);
    }


    public interface OnEmployeeTreeLoadListener extends NetWorkFailListener{
        /**
         * 加载成功
         *
         * @param organizationList
         */
        void onSuccess(int loadedStatus, List<OrganizationResult> organizationList);


    }
}
