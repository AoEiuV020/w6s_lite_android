package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.cache.EmployeeCache;
import com.foreverht.db.service.repository.EmployeeRepository;
import com.foreverht.db.service.repository.OrganizationRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService;
import com.foreveross.atwork.api.sdk.Employee.EmployeeSyncNetService;
import com.foreveross.atwork.api.sdk.Employee.requestModel.SearchEmployeePost;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryEmployeeListResponse;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryEmployeeResponseJson;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryIntersectionOrgCodesResponse;
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryOrgAndEmpListResponse;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.db.daoService.EmployeeDaoService;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.model.orgization.OrgRelationship;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.plugin.employee.IEmployeeManager;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.ArrayUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.manager.model.CheckTalkAuthResult;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.contact.data.StarUserListDataWrap;
import com.foreveross.atwork.modules.contact.util.EmpSeniorHelper;
import com.foreveross.atwork.utils.EmployeeHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;
import com.w6s.model.incomingCall.IncomingCaller;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * Created by shadow on 2016/4/25.
 */
public class EmployeeManager implements IEmployeeManager {


    private static final String TAG = EmployeeManager.class.getSimpleName();

    private static EmployeeManager sInstance = new EmployeeManager();

    private EmployeeManager() {
    }

    public static EmployeeManager getInstance() {
        return sInstance;
    }


    /**
     * 查询用户间的组织交集(orgCode list)
     * */
    @SuppressLint("StaticFieldLeak")
    public void queryIntersectionOrgCodes(Context context, String userId, BaseNetWorkListener<List<String>> callBackNetWorkListener) {

        new AsyncTask<Void, Void, HttpResult>(){

            @Override
            protected HttpResult doInBackground(Void... voids) {
                return EmployeeSyncNetService.getInstance().queryIntersectionOrgCodes(context, userId);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    QueryIntersectionOrgCodesResponse orgCodesResponse = (QueryIntersectionOrgCodesResponse) httpResult.resultResponse;
                    callBackNetWorkListener.onSuccess(orgCodesResponse.orgCodes);
                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, callBackNetWorkListener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    public CheckTalkAuthResult canChatByEmpSeniorPermissionSync(Context context, @NonNull Employee targetEmp, boolean checkStar) {
        CheckTalkAuthResult result;
        if (fastCheckEmpSeniorCanTalkSync(context, targetEmp)) {
            result = CheckTalkAuthResult.MAY_TALK;

        }  else {
            result = checkEmpSeniorRelationshipTalkAuthSync(context, targetEmp, checkStar);

        }

        return result;

    }



    @Nullable
    public CheckTalkAuthResult checkEmpSeniorRelationshipTalkAuthSync(Context context, @NonNull Employee targetEmp, boolean checkStar) {


        //星标关系, 不用检查
        if(checkStar && StarUserListDataWrap.getInstance().containsKey(targetEmp.userId)) {
            return CheckTalkAuthResult.MAY_TALK;
        }


        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        Employee loginSelectedEmp = EmployeeManager.getInstance().queryEmpInSync(context, userId, targetEmp.orgCode);
        if(null == loginSelectedEmp) {
            return CheckTalkAuthResult.NETWORK_FAILED;
        }

        //当前自己的雇员是高管
        if(loginSelectedEmp.senior) {
            return CheckTalkAuthResult.MAY_TALK;
        }

        String[] chatAuthScope = OrganizationSettingsManager.getInstance().getChatAuthScope(context, targetEmp.orgCode);
        if(ArrayUtil.isEmpty(chatAuthScope)) {
            return CheckTalkAuthResult.MAY_NOT_TALK;
        }


        //判断雇员层级关系
        boolean canTalk = checkChatAuth(targetEmp, loginSelectedEmp, chatAuthScope);
        if(canTalk) {
            return CheckTalkAuthResult.MAY_TALK;
        }

        return CheckTalkAuthResult.MAY_NOT_TALK;

    }

    private boolean fastCheckEmpSeniorCanTalkSync(Context context, @NonNull Employee targetEmp) {
        //先判断组织设置
        if(!OrganizationSettingsManager.getInstance().isSeniorShowOpen(context, targetEmp.orgCode)) {
            return true;
        }


        //对方不是高管, 不用检查
        if(!targetEmp.senior) {
            return true;
        }
        return false;
    }


    /**
     * 遍历雇员间的所有职位, 作交叉对比, 只要有一组复核层级关系, 则认为他们可以聊天
     * */
    private boolean checkChatAuth(Employee targetEmp, Employee loginSelectedEmp, String[] chatAuthScope) {
        for(Position targetPosition : targetEmp.positions) {
            for(Position mePosition : loginSelectedEmp.positions) {

                for(String level : chatAuthScope) {
                    boolean isLegal = countLegalLevelRelationship(targetPosition.path, mePosition.path, Integer.valueOf(level));
                    if(isLegal){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 计算雇员间职位的层级关系,
     * 例如 targetPath: /1/2/3/  loginPath: /1/2/ level: 1  此时符合条件
     *      targetPath: /1/2/3/  loginPath: /1/2/3/4 level : -1  符合条件
     *      targetPath: /1/2/3/  loginPath: /1/2/3  level : 0  符合条件
     *
     * @param targetPath
     * @param loginPath
     * @param level
     * return isLegal
     * */
    private boolean countLegalLevelRelationship(String targetPath, String loginPath, int level) {
        if(0 == level) {
            return targetPath.equals(loginPath);
        }

        //除了 level 为0, 其余情况不能平级关系
        if(targetPath.equals(loginPath)) {
            return false;
        }

        String shortPath;
        String longPath;
        if(0 < level) {
            shortPath = loginPath;
            longPath = targetPath;

        } else {
            shortPath = targetPath;
            longPath = loginPath;
        }

        if (longPath.startsWith(shortPath)) {
            int longPathLevel = longPath.split("/").length;
            int shortPathLevel = shortPath.split("/").length;
            if (Math.abs(level) >= longPathLevel - shortPathLevel) {
                return true;
            }

        }

        return false;
    }



    /**
     * 批量查询本地雇员信息(缓存 -> 本地)
     * */
    public List<Employee> batchQueryLocalEmpList(List<String> userIdList, String orgCode) {
        List<Employee> localEmpList = new ArrayList<>();
        List<String> userIdNotInCache = new ArrayList<>();

        for(String userId : userIdList) {
            Employee empCache = EmployeeCache.getInstance().getEmpCache(userId, orgCode);
            if(null != empCache) {
                localEmpList.add(empCache);

            } else {
                userIdNotInCache.add(userId);
            }
        }

        if (!ListUtil.isEmpty(userIdNotInCache)) {
            List<Employee> userInDbInList = EmployeeRepository.getInstance().queryEmpListByIdsWithNotExist(userIdNotInCache, orgCode);
            localEmpList.addAll(userInDbInList);
        }

        return localEmpList;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void queryLoginUserEmailList(@NotNull Function1<? super List<String>, Unit> getEmailList) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                List<Employee> employeeList = queryUserEmployeeListSync(LoginUserInfo.getInstance().getLoginUserId(AtworkApplicationLike.baseContext));
                if(!ListUtil.isEmpty(employeeList)) {
                    HashMap<String, DataSchema> emailDataMap = EmployeeHelper.getEmailDataSchemaStringHashMap(employeeList);
                    Set<String> emailDataSet = emailDataMap.keySet();

                    if(ListUtil.isEmpty(emailDataSet)) {
                        User loginUserSync = AtworkApplicationLike.getLoginUserSync();
                        if (null != loginUserSync && !StringUtils.isEmpty(loginUserSync.mEmail)) {
                            emailDataSet.add(loginUserSync.mEmail);
                        }
                    }

                    return CollectionsKt.filter(emailDataSet, it -> !StringUtils.isEmpty(it));

                }


                User loginUserSync = AtworkApplicationLike.getLoginUserSync();
                if (null != loginUserSync && !StringUtils.isEmpty(loginUserSync.mEmail)) {
                    return ListUtil.makeSingleList(loginUserSync.mEmail);
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<String> allMobileMap) {
                getEmailList.invoke(allMobileMap);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }



    public void queryLoginUserEmailList(BaseQueryListener<List<String>> queryListener) {

    }

    @SuppressLint("StaticFieldLeak")
    public void queryUserEmployeeMobileList(final String userId, BaseQueryListener<HashMap<DataSchema, String>> queryListener) {
        new AsyncTask<Void, Void, HashMap<DataSchema, String>>() {
            @Override
            protected HashMap<DataSchema, String> doInBackground(Void... voids) {
                List<Employee> employeeList = queryUserEmployeeListSync(userId);
                HashMap<DataSchema, String> allMobileMap = new HashMap<>();
                for(Employee employee : employeeList) {
                    allMobileMap.putAll(EmployeeHelper.getShowMobileTypeSchemaMap(employee));
                }
                return allMobileMap;
            }

            @Override
            protected void onPostExecute(HashMap<DataSchema, String> allMobileMap) {
                queryListener.onSuccess(allMobileMap);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    @SuppressLint("StaticFieldLeak")
    public void queryUserEmployeeList(final String userId, BaseQueryListener<List<Employee>> queryListener) {

        new AsyncTask<Void, Void, List<Employee>>() {
            @Override
            protected List<Employee> doInBackground(Void... voids) {
                return queryUserEmployeeListSync(userId);
            }

            @Override
            protected void onPostExecute(List<Employee> employees) {
                queryListener.onSuccess(employees);
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    public List<Employee> queryUserEmployeeListSync(final String userId) {

        List<Organization> intersectionOrgList = OrganizationManager.getInstance().queryOrgIntersectionInLoginUserSync(userId);

        if(ListUtil.isEmpty(intersectionOrgList)) {
            return Collections.EMPTY_LIST;
        }

        List<Employee> employeeList = EmployeeRepository.getInstance().queryEmployeeList(userId, Organization.getOrgCodeList(intersectionOrgList));

        if(!ListUtil.isEmpty(employeeList)) {
            assembleEmployeeWithOrgData(employeeList, intersectionOrgList);
            return employeeList;

        }

        QueryOrgAndEmpListMultiResult queryOrgAndEmpListMultiResult = queryOrgAndEmpListRemoteSync(BaseApplicationLike.baseContext, userId);
        if(!ListUtil.isEmpty(queryOrgAndEmpListMultiResult.empList)) {

            assembleEmployeeWithOrgData(queryOrgAndEmpListMultiResult.empList, queryOrgAndEmpListMultiResult.orgList);

            employeeList.addAll(queryOrgAndEmpListMultiResult.empList);
        }



        return employeeList;
    }


    private void assembleEmployeeWithOrgData(List<Employee> employeeList, List<Organization> intersectionOrgList) {
        if(ListUtil.isEmpty(employeeList) || ListUtil.isEmpty(intersectionOrgList)) {
            return;
        }


        for (Employee employee : employeeList) {

            for (Organization organization : intersectionOrgList) {
                if (organization.mOrgCode.equalsIgnoreCase(employee.orgCode)) {
                    employee.setOrgInfo(organization.mName, organization.mCreated, organization.getSortOrder());
                    break;
                }
            }
        }

        Collections.sort(employeeList);

    }




    /**
     * 根据 userId 查询组织与雇员信息
     * */
    @SuppressLint("StaticFieldLeak")
    public void queryOrgAndEmpListRemote(final Context context, final String userId, @Nullable final QueryOrgAndEmpListListener listener) {
        new AsyncTask<Void, Void, QueryOrgAndEmpListMultiResult>() {
            @Override
            protected QueryOrgAndEmpListMultiResult doInBackground(Void... params) {
                return queryOrgAndEmpListRemoteSync(context, userId);
            }

            @Override
            protected void onPostExecute(QueryOrgAndEmpListMultiResult queryOrgAndEmpListMultiResult) {
                if(queryOrgAndEmpListMultiResult.httpResult.isRequestSuccess()) {

                    if (null != listener) {
                        listener.onSuccess(queryOrgAndEmpListMultiResult.orgList, queryOrgAndEmpListMultiResult.empList);
                    }

                    return;

                }

                if (null != listener) {
                    listener.onFail();
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @NonNull
    private QueryOrgAndEmpListMultiResult queryOrgAndEmpListRemoteSync(Context context, String userId) {
        QueryOrgAndEmpListMultiResult queryOrgAndEmpListMultiResult = new QueryOrgAndEmpListMultiResult();

        HttpResult httpResult = EmployeeSyncNetService.getInstance().queryOrgAndEmpList(context, userId, true, true);
        queryOrgAndEmpListMultiResult.httpResult = httpResult;

        if(httpResult.isRequestSuccess() && httpResult.resultResponse instanceof QueryOrgAndEmpListResponse) {
            QueryOrgAndEmpListResponse queryOrgAndEmpListResponse = (QueryOrgAndEmpListResponse) httpResult.resultResponse;

            List<Organization> orgList = new ArrayList<>();
            List<Employee> empList = new ArrayList<>();

            queryOrgAndEmpListMultiResult.orgList = orgList;
            queryOrgAndEmpListMultiResult.empList = empList;

            if (!ListUtil.isEmpty(queryOrgAndEmpListResponse.mResultList)) {
                for(QueryOrgAndEmpListResponse.Result result : queryOrgAndEmpListResponse.mResultList) {
                    if(result.org == null || result.emp == null) {
                        continue;
                    }
                    orgList.add(result.org);

                    result.emp.setOrgInfo(result.org.getNameI18n(context), result.org.mCreated, result.org.getSortOrder());
                    empList.add(result.emp);
                }

                Collections.sort(empList);

                //更新组织表
                OrganizationDaoService.getInstance().batchInsertOrganization(orgList);
                //更新雇员表
                EmployeeDaoService.getInstance().batchInsertEmployee(empList);
                //更新雇员关系表
                List<OrgRelationship> orgRelationshipList = OrgRelationship.produceOrgRelationshipListByOrgList(userId, OrgRelationship.Type.EMPLOYEE, orgList);
                OrganizationDaoService.getInstance().batchInsertRelationAndClean(userId, OrgRelationship.Type.EMPLOYEE, orgRelationshipList);

                //自己的组织信息, 更新相关缓存
                if(User.isYou(context, userId)) {
                    OrganizationManager.getInstance().updateAndCleanOrgCodeListCache(OrgRelationship.getOrgCodeList(orgRelationshipList));
                }
            }



        }


        return queryOrgAndEmpListMultiResult;
    }

    public String getChangeInfoJson(Employee employee, DataSchema dataSchema, String newValue) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (EmployeeHelper.havingFieldByProperty(dataSchema.mProperty)) {
                if("birthday".equalsIgnoreCase(dataSchema.mProperty)) {
                    jsonObject.put(dataSchema.mProperty, Long.valueOf(newValue));

                } else {
                    jsonObject.put(dataSchema.mProperty, newValue);

                }


            } else {
                List<EmployeePropertyRecord> reqList = new ArrayList<>();
                boolean havingPropertyValue = false;

                if (!ListUtil.isEmpty(employee.properties)) {
                    reqList.addAll(employee.properties);

                    for (EmployeePropertyRecord record : reqList) {
                        if (null == record) {
                            continue;
                        }

                        if (dataSchema.mId.equalsIgnoreCase(record.mDataSchemaId)|| dataSchema.mProperty.equalsIgnoreCase(record.mProperty)) {
                            havingPropertyValue = true;

                            record.mValues = new String[]{ newValue };
                            record.mValue = newValue;
                            break;
                        }
                    }

                }

                if(!havingPropertyValue) {
                    EmployeePropertyRecord record = new EmployeePropertyRecord();
                    record.mDataSchemaId = String.valueOf(dataSchema.mId);
                    record.mValues = new String[]{ newValue };
                    record.mValue = newValue;

                    reqList.add(record);
                }


                String jsonArray = new Gson().toJson(reqList);
                jsonObject.put("properties", new JSONArray(jsonArray));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    /**
     * 按条件搜索雇员(单个组织)
     * @see #searchEmployeesRemote(Context, List, String, String, ExpandEmpTreeAction, RemoteSearchEmployeeListListener)
     *
     * @param searchKeyValue  查询条件
     */
    public void searchEmployeesRemote(final Context context, final String orgCode, final String searchId, final String searchKeyValue, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final RemoteSearchEmployeeListListener listener) {
        List<String> singleList = new ArrayList<>();
        singleList.add(orgCode);
        searchEmployeesRemote(context, singleList, searchId, searchKeyValue, expandEmpTreeAction, listener);
    }

    /**
     * 按条件搜索雇员(本地所有组织)
     * @see #searchEmployeesRemote(Context, List, String, String, ExpandEmpTreeAction, RemoteSearchEmployeeListListener)
     *
     * @param searchKeyValue  查询条件
     */
    public void searchEmployeesRemote(final Context context, final String searchId, final String searchKeyValue, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final RemoteSearchEmployeeListListener listener) {
        List<String> emptyList = new ArrayList<>();
        searchEmployeesRemote(context, emptyList, searchId, searchKeyValue, expandEmpTreeAction, listener);
    }


    /**
     * 按条件搜索雇员(多组织)
     *
     * @param orgCodeList 若为空, 则查本地数据库的所有 orgCodeList
     * @param searchKeyValue  查询条件
     * @param expandEmpTreeAction 展开雇员树的条件(暂时包含是否去除高管, 视图权限是否需要)
     */
    @SuppressLint("StaticFieldLeak")
    public void searchEmployeesRemote(final Context context, final List<String> orgCodeList, final String searchId, final String searchKeyValue, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final RemoteSearchEmployeeListListener listener) {

        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                List<String> searchCodeList = new ArrayList<>();
                if (ListUtil.isEmpty(orgCodeList)) {
                    searchCodeList = OrganizationRepository.getInstance().queryLoginOrgCodeListSync(context);

                } else {
                    searchCodeList.addAll(orgCodeList);
                }

                String filterSeniorOrgCode;
                if(1 == searchCodeList.size()) {
                    filterSeniorOrgCode = searchCodeList.get(0);
                } else {
                    filterSeniorOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context);
                }

                Boolean filterSenior = EmpSeniorHelper.getFilterSeniorAction(context, expandEmpTreeAction, filterSeniorOrgCode);

                if(null == filterSenior) {
                    return HttpResult.getInstance().netException(null);
                }

                boolean filterRank = true;
                //选人模式时, 需要启动聊天视图过滤
                boolean rankView = !expandEmpTreeAction.mIsSelectMode;


                SearchEmployeePost post = new SearchEmployeePost();
                post.mOrgCode = searchCodeList;
                post.query = searchKeyValue;
                post.filterSenior = filterSenior;
                post.mViewAcl = expandEmpTreeAction.mViewAcl;
                post.mFilterRank = filterRank;
                post.mRankView = rankView;

                HttpResult httpResult = EmployeeSyncNetService.getInstance().searchEmployees(context, post);

                long startTime = System.currentTimeMillis();
                filterResult(httpResult, expandEmpTreeAction);

                LogUtil.e("filterResult duration -> " + (System.currentTimeMillis() - startTime));

                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    QueryEmployeeListResponse response = (QueryEmployeeListResponse) httpResult.resultResponse;

                    updateOnlineStatus(response);

                    listener.onSuccess(searchId, response.employeeList);

                    //更新雇员表
//                    EmployeeDaoService.getInstance().batchInsertEmployeeCheckDataSchema(response.employeeList);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }


            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    private void filterResult(HttpResult httpResult, @NonNull ExpandEmpTreeAction expandEmpTreeAction) {
//        if(!expandEmpTreeAction.mViewAcl) {
//            return;
//        }
//
//        if(!httpResult.isRequestSuccess()) {
//            return;
//        }
//
//        QueryEmployeeListResponse response = (QueryEmployeeListResponse) httpResult.resultResponse;
//
//        List<Employee> filterEmployeeList = new ArrayList<>();
//        for(Employee employee : response.employeeList) {
//
//
//            CheckTalkAuthResult checkTalkAuthResult = canChatByEmpSeniorPermissionSync(AtworkApplicationLike.baseContext, employee, false);
//            if(CheckTalkAuthResult.NETWORK_FAILED == checkTalkAuthResult) {
//                response.status = -1;
//                response.employeeList.clear();
//                break;
//            }
//
//            if(CheckTalkAuthResult.MAY_TALK == checkTalkAuthResult
//                    || CheckTalkAuthResult.CAN_TALK == checkTalkAuthResult) {
//                filterEmployeeList.add(employee);
//            }
//        }
//
//        response.employeeList = filterEmployeeList;
    }




    private void updateOnlineStatus(QueryEmployeeListResponse response) {
        if(!ListUtil.isEmpty(response.employeeList)) {
            for(Employee employee : response.employeeList) {
                OnlineManager.getInstance().setOnlineStatus(employee.userId, employee.isOnline());
            }
        }
    }

    /**
     * 根据单个 orgCode 搜索雇员
     * */
    public void searchEmployeesLocal(Context context, final String orgCode, final String searchId, final String searchKeyValue, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final LocalSearchEmployeeListListener listener) {
        List<String> singleList = new ArrayList<>();
        singleList.add(orgCode);
        searchEmployeesLocal(context, singleList, searchId, searchKeyValue, expandEmpTreeAction, listener);
    }

    /**
     * 根据用户数据库的 orgCodeList 搜索雇员
     * */
    public void searchEmployeesLocal(Context context, final String searchId, final String searchKeyValue, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final LocalSearchEmployeeListListener listener) {
        List<String> emptyList = new ArrayList<>();

        searchEmployeesLocal(context, emptyList, searchId, searchKeyValue, expandEmpTreeAction, listener);
    }


    /**
     * 根据传进来的 orgCodeList 搜索本地雇员
     * */
    @SuppressLint("StaticFieldLeak")
    public void searchEmployeesLocal(Context context, final List<String> orgCodeList, final String searchId, final String searchKeyValue, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final LocalSearchEmployeeListListener listener) {
        new AsyncTask<Void, Void, List<Employee>>() {
            @Override
            protected List<Employee> doInBackground(Void... params) {
                List<Employee> employeeList = EmployeeRepository.getInstance().searchEmployees(context, searchKeyValue, orgCodeList);


                return employeeList;
            }

            @Override
            protected void onPostExecute(List<Employee> employeeList) {
                listener.onSuccess(searchId, employeeList);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    @SuppressLint("StaticFieldLeak")
    public void queryLoginCurrentOrgEmp(Context context, EmployeeAsyncNetService.QueryEmployeeInfoListener listener) {
        new AsyncTask<Void, Void, MultiResult<Employee>>() {
            @Override
            protected MultiResult<Employee> doInBackground(Void... params) {

                return queryEmpResultInSync(context, LoginUserInfo.getInstance().getLoginUserId(context), PersonalShareInfo.getInstance().getCurrentOrg(context));
            }

            @Override
            protected void onPostExecute(MultiResult<Employee> employeeMultiResult) {
                Employee employee = getUserFromMultiResult(employeeMultiResult);
                if(null != employee) {
                    listener.onSuccess(employee);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(employeeMultiResult.httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    @SuppressLint("StaticFieldLeak")
    public void queryEmp(Context context, String userId, String orgCode, EmployeeAsyncNetService.QueryEmployeeInfoListener listener) {

        Employee employeeCache = EmployeeCache.getInstance().getEmpCache(userId, orgCode);
        if(null != employeeCache) {
            listener.onSuccess(employeeCache);
            return;
        }

        new AsyncTask<Void, Void, MultiResult<Employee>>() {
            @Override
            protected MultiResult<Employee> doInBackground(Void... params) {

                return queryEmpResultInSync(context, userId, orgCode);
            }

            @Override
            protected void onPostExecute(MultiResult<Employee> employeeMultiResult) {
                Employee employee = getUserFromMultiResult(employeeMultiResult);
                if(null != employee) {
                    listener.onSuccess(employee);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(employeeMultiResult.httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @Nullable
    @Override
    public Employee queryEmpInSync(Context context, String userId, String orgCode) {
        MultiResult<Employee> multiResult = queryEmpResultInSync(context, userId, orgCode);
        return getUserFromMultiResult(multiResult);
    }

    @Nullable
    public Employee getUserFromMultiResult(MultiResult<Employee> multiResult) {
        if (multiResult == null) {
            return null;
        }
        Employee resultEmployee = null;
        if(null != multiResult.localResult) {
            resultEmployee = multiResult.localResult;
            return resultEmployee;
        }
        if(null != multiResult.httpResult && multiResult.httpResult.isRequestSuccess()) {
            resultEmployee = ((QueryEmployeeResponseJson)multiResult.httpResult.resultResponse).mEmployee;
        }
        return resultEmployee;
    }

    /**
     * 本地->网络 查询雇员
     * */
    public MultiResult<Employee> queryEmpResultInSync(Context context, String userId, String orgCode) {
        MultiResult<Employee> multiResult = new MultiResult<>();

        Employee employee = EmployeeCache.getInstance().getEmpCache(userId, orgCode);
        if(null == employee) {
            employee = EmployeeRepository.getInstance().queryEmployeeByUserIdAndOrgCode(userId, orgCode);

        }

        boolean needRequestRemote = false;

        if(null == employee) {
            needRequestRemote = true;
        }

        //预防旧版本本地的 emp 没有新增的字段, 导致无法返回新字段
        if(null != employee && !employee.checkPositionLegal()) {

            String requestId = "empInfo_" + userId + "_" + orgCode;
            if(RequestRemoteInterceptor.INSTANCE.checkLegal(requestId)) {
                needRequestRemote = true;

            }

            RequestRemoteInterceptor.INSTANCE.addInterceptRequestId(requestId);
        }

        if (needRequestRemote) {
            HttpResult httpResult = EmployeeSyncNetService.getInstance().queryUserEmployeeInfo(context, orgCode, userId);

            if(httpResult.isRequestSuccess()) {
                employee = ((QueryEmployeeResponseJson)httpResult.resultResponse).mEmployee;


                if(null != employee) {
                    EmployeeRepository.getInstance().insertEmployee(employee);
                }

                multiResult.httpResult = httpResult;

            }
        } else {
            multiResult.localResult = employee;
        }

        return multiResult;

    }

    /**
     * 分享消息 查询组织下的雇员名字
     */
    public void setEmployeeNameForShareMsg(ShareChatMessage shareChatMessage, TextView textView) {

        String inviterName;
        if(!StringUtils.isEmpty(shareChatMessage.getContent().mOrgInviterName)) {
            inviterName = shareChatMessage.getContent().mOrgInviterName;

        } else {
            inviterName = shareChatMessage.mMyName;
        }


        if(!StringUtils.isEmpty(inviterName)) {
            String orgOwner = shareChatMessage.getContent().mOrgOwner;
            String orgName = shareChatMessage.getContent().mOrgName;
            String inviterUserId = shareChatMessage.from;
            textView.setText(Html.fromHtml(getOrgInviteDescription(textView.getContext(), orgOwner, orgName, inviterUserId, inviterName)));

        } else {

            if(!StringUtils.isEmpty(shareChatMessage.getContent().mDescription)) {
                textView.setText(shareChatMessage.getContent().mDescription);
            }
        }

    }

    public void syncNotExistEmpList(Context context, List<String> userIdList, String orgCode) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncNotExistEmpListSync(context, userIdList, orgCode);
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 同步本地数据库不存在的雇员信息
     * */
    public void syncNotExistEmpListSync(Context context, List<String> userIdList, String orgCode) {
        List<String> unExistsUserIds = EmployeeRepository.getInstance().queryUnExistUserIds(userIdList, orgCode);
        if (!ListUtil.isEmpty(unExistsUserIds)) {

            //分批查询, 防止 http 414
            int loop = unExistsUserIds.size() / 50;
            for(int i = 0; i <= loop; i++) {
                int endIndex = (i + 1) * 50;
                int startIndex = i * 50;
                if(endIndex > unExistsUserIds.size()) {
                    endIndex = unExistsUserIds.size();
                }

                List<String> subUnExistsUserIds = unExistsUserIds.subList(startIndex, endIndex);

                doSyncNotExistEmpListSync(context, orgCode, subUnExistsUserIds);

            }

        }

    }


    private void doSyncNotExistEmpListSync(Context context, String orgCode, List<String> unExistsUserIds) {
        HttpResult httpResult = EmployeeSyncNetService.getInstance().queryEmpListFromRemote(context, unExistsUserIds, orgCode);
        if (httpResult.isRequestSuccess()) {
            QueryEmployeeListResponse empListResponseJson = (QueryEmployeeListResponse) httpResult.resultResponse;
            List<Employee> unExistEmpList = empListResponseJson.employeeList;

            if (!ListUtil.isEmpty(unExistEmpList)) {
                EmployeeRepository.getInstance().batchInsertEmployee(unExistEmpList);

            }

        } else {
            if (null != httpResult.resultResponse) {
                ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);

            }
        }
    }




    /**
     * 聊天界面里, 组织分享的描述内容
     * */
    public String getOrgInviteDescription(Context context, String orgOwner, String orgName, String inviterUserId, String inviterName) {
        //组织是自己创建的
        if (inviterUserId.equals(orgOwner)) {
            return context.getString(R.string.org_owner_invite, inviterName, context.getString(R.string.app_name), orgName);

        } else {
            return context.getString(R.string.org_invite, inviterName, context.getString(R.string.app_name), orgName);
        }
    }




    public interface OnResultListener {
        void onResult(CheckTalkAuthResult result);
        void onLongLoading();
    }

    public interface LocalSearchEmployeeListListener {
        void onSuccess(String searchKey, List<Employee> employeeList);
        void onFail();
    }

    public interface RemoteSearchEmployeeListListener extends NetWorkFailListener{
        void onSuccess(String searchKey, List<Employee> employeeList);
    }

    public interface QueryOrgAndEmpListListener {
        void onSuccess(List<Organization> organizationList, List<Employee> employeeList);
        void onFail();
    }

    public interface OnEmpIncomingCallerListener {
        void onSuccess();
        void onFailure();
    }

    public interface OnQueryEmpIncomingCallerListener {
        void onFinish(IncomingCaller caller);
    }

    class QueryOrgAndEmpListMultiResult {

        public HttpResult httpResult;
        public List<Organization> orgList;
        public List<Employee> empList;


    }

}
