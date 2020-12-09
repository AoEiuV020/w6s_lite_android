package com.foreveross.atwork.manager;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@OrganizationSettingsManager
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

import com.foreverht.cache.OrgCache;
import com.foreverht.db.service.repository.EmployeeRepository;
import com.foreverht.db.service.repository.OrgApplyRepository;
import com.foreverht.db.service.repository.OrgRelationShipRepository;
import com.foreverht.db.service.repository.OrganizationRepository;
import com.foreverht.db.service.repository.UnreadMessageRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.organization.OrganizationAsyncNetService;
import com.foreveross.atwork.api.sdk.organization.OrganizationSyncNetService;
import com.foreveross.atwork.api.sdk.organization.requstModel.CheckOrgsUpdatePostJson;
import com.foreveross.atwork.api.sdk.organization.requstModel.QueryOrganizationViewRequest;
import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.api.sdk.organization.requstModel.SearchDepartmentRequest;
import com.foreveross.atwork.api.sdk.organization.responseModel.CheckOrgsUpdateResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeesTreeResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationListResponse;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.api.sdk.organization.responseModel.QueryOrgResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.RecursionEmpListResponseJson;
import com.foreveross.atwork.api.sdk.organization.responseModel.SearchDepartmentResp;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.ContactModel;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.foreveross.atwork.infrastructure.model.orgization.OrgApply;
import com.foreveross.atwork.infrastructure.model.orgization.OrgRelationship;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.manager.model.ApplyingOrganization;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.aboutme.fragment.AboutMeFragment;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.modules.contact.fragment.ContactFragment;
import com.foreveross.atwork.modules.contact.util.EmpSeniorHelper;
import com.foreveross.atwork.modules.organization.manager.DeptManager;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 组织架构管理类
 * Created by reyzhang22 on 16/4/16.
 */
@SuppressLint("StaticFieldLeak")
public class OrganizationManager {

    private static final String TAG = OrganizationManager.class.getSimpleName();

    public static final String TOP = "-1";

    private static OrganizationManager sInstance = new OrganizationManager();

    private CopyOnWriteArraySet<String> mOrgCodeSet = null;

    private OrganizationManager() {

    }

    public static OrganizationManager getInstance() {
        return sInstance;
    }


    public void clear() {
        if(null != mOrgCodeSet) {
            mOrgCodeSet.clear();
            mOrgCodeSet = null;
        }
    }




    /**
     * 获取当前 org
     */
    @SuppressLint("StaticFieldLeak")
    public void getLocalCurrentOrg(Context context, OnGetOrgListener listener) {
        queryLocalOrg(context, PersonalShareInfo.getInstance().getCurrentOrg(context), listener);
    }

    @SuppressLint("StaticFieldLeak")
    public void queryLocalOrg(Context context, String orgCode, OnGetOrgListener listener) {
        new AsyncTask<Void, Void, Organization>() {
            @Override
            protected Organization doInBackground(Void... params) {
                if (!StringUtils.isEmpty(orgCode)) {
                    Organization organization = OrganizationRepository.getInstance().queryOrganization(orgCode);
                    return organization;
                }


                return null;
            }

            @Override
            protected void onPostExecute(Organization organization) {
                if (null != organization) {
                    listener.success(organization);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }




    @SuppressLint("StaticFieldLeak")
    public void queryOrg(Context context, @Nullable String orgCode, BaseNetWorkListener<Organization> listener) {
        new AsyncTask<Void, Void, MultiResult<Organization>>() {
            @Override
            protected MultiResult<Organization> doInBackground(Void... params) {
                return queryOrgResultSync(context, orgCode);
            }

            @Override
            protected void onPostExecute(MultiResult<Organization> multiResult) {
                Organization organization = queryOrgFromMultiResultSync(multiResult);
                if (null != organization) {
                    listener.onSuccess(organization);
                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener);


            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }



    @Nullable
    public Organization queryOrgSync(Context context, @Nullable String orgCode) {
        MultiResult<Organization> multiResult = queryOrgResultSync(context, orgCode);
        return queryOrgFromMultiResultSync(multiResult);
    }

    @Nullable
    public Organization queryOrgFromMultiResultSync(MultiResult<Organization> multiResult) {
        if (multiResult == null) {
            return null;
        }
        if (null != multiResult.localResult) {
            return multiResult.localResult;
        }


        if (multiResult.httpResult.isRequestSuccess()) {
            return ((QueryOrgResponseJson) multiResult.httpResult.resultResponse).organization;


        }

        return null;
    }

    private MultiResult<Organization> queryOrgResultSync(Context context, @Nullable String orgCode) {

        MultiResult<Organization> multiResult = new MultiResult<>();

        String orgCodeHandled = orgCode;
        if (StringUtils.isEmpty(orgCodeHandled)) {
            orgCodeHandled = PersonalShareInfo.getInstance().getCurrentOrg(context);
        }

        Organization organization = null;

        //先找缓存
        organization = OrgCache.getInstance().getOrganization(orgCodeHandled);
        if(null != organization) {
            multiResult.localResult = organization;
            return multiResult;
        }

        //再找本地数据
        organization = OrganizationRepository.getInstance().queryOrganization(orgCodeHandled);
        if(null != organization) {
            OrgCache.getInstance().setOrganization(organization);
            multiResult.localResult = organization;
            return multiResult;
        }


        //最后找服务器
        HttpResult httpResult = OrganizationSyncNetService.getInstance().queryOrgResult(context, orgCodeHandled);

        multiResult.httpResult = httpResult;

        if(httpResult.isRequestSuccess()) {
            QueryOrgResponseJson queryOrgResponseJson = (QueryOrgResponseJson) httpResult.resultResponse;

            organization = queryOrgResponseJson.organization;
            if(null != organization) {
                boolean result = OrganizationRepository.getInstance().insertOrganization(organization);
                if(result) {
                    OrgCache.getInstance().setOrganization(organization);

                }

            }
        }

        return multiResult;
    }

    @Nullable
    public Organization queryOrgRemoteSync(Context context, String orgCode) {
        Organization organization = null;
        HttpResult httpResult = OrganizationSyncNetService.getInstance().queryOrgResult(context, orgCode);
        if (httpResult.isRequestSuccess() && httpResult.resultResponse instanceof  QueryOrgResponseJson) {
            organization = ((QueryOrgResponseJson) httpResult.resultResponse).organization;
            OrgCache.getInstance().setOrganization(organization);

        } else {
            if (null != httpResult.resultResponse) {
                ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);

            }
        }
        return organization;
    }


    /**
     * 从网络拿取最新的组织资料
     */
    public void queryOrgListByUserIdFromRemote(final Context context, final String userId, final OrganizationAsyncNetService.OnQueryOrgListListener listener) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, HttpResult> task = new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = OrganizationSyncNetService.getInstance().queryOrganizationsByUserIdFromRemote(context, userId);
                if (httpResult.isRequestSuccess()) {
                    OrganizationListResponse organizationListResponse = (OrganizationListResponse) httpResult.resultResponse;
                    List<Organization> organizationList = organizationListResponse.organizationList;
                    List<OrgRelationship> orgRelationshipList = OrgRelationship.produceOrgRelationshipListByOrgList(userId, OrgRelationship.Type.EMPLOYEE, organizationList);

                    OrgRelationShipRepository.getInstance().batchInsertOrgRelationAndClean(userId, OrgRelationship.Type.EMPLOYEE, orgRelationshipList);
                    //自己的组织信息, 更新相关缓存
                    if(User.isYou(context, userId)) {
                        OrganizationManager.getInstance().updateAndCleanOrgCodeListCache(OrgRelationship.getOrgCodeList(orgRelationshipList));
                    }
                }

                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    OrganizationListResponse organizationListResponse = (OrganizationListResponse) httpResult.resultResponse;

                    listener.onSuccess(organizationListResponse.organizationList);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        };
        task.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @Nullable
    public List<Organization> queryUserOrganizationsFromRemote(Context context) {
        List<Organization> orgList = null;
        HttpResult httpResult = OrganizationSyncNetService.getInstance().queryUserOrganizationsFromRemote(context);
        if (httpResult.isRequestSuccess()) {
            OrganizationListResponse listResponse = (OrganizationListResponse) httpResult.resultResponse;
            orgList = listResponse.organizationList;

        } else {
            if (null != httpResult.resultResponse) {
                ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);
            }
        }

        return orgList;
    }


    /**
     * 获取本地数据库里所有的组织架构信息
     *
     * @return
     */
    public void getLocalOrganizations(Context context, OrganizationRepository.OnLocalOrganizationListener listener) {
        if (listener == null) {
            return;
        }
        OrganizationDaoService.getInstance().queryLoginLocalOrganizations(context, listener);
    }

    /**
     * 批量插入组织架构
     *
     * @param organizationList
     */
    public void batchInsertOrganizationsToLocal(List<Organization> organizationList) {
        if (organizationList.isEmpty()) {
            return;
        }
        OrganizationDaoService.getInstance().batchInsertOrganization(organizationList);
    }

    @SuppressLint("StaticFieldLeak")
    public void checkOrganizationsUpdate(Context context, final List<Organization> userOrgLists, OnCheckOrgListUpdateListener listUpdateListener) {

        new AsyncTask<Void, Void, MultiResult<CheckOrgUpdateResult>>() {
            @Override
            protected MultiResult<CheckOrgUpdateResult> doInBackground(Void... params) {
                boolean needUpdate = false;

                MultiResult<CheckOrgUpdateResult> multiResult = new MultiResult<>();
                CheckOrgsUpdatePostJson postJson = CheckOrgsUpdatePostJson.createPostJson(userOrgLists);
                HttpResult httpResult = OrganizationSyncNetService.getInstance().checkOrganizationsUpdate(context, postJson);

                if (httpResult.isRequestSuccess()) {
                    CheckOrgsUpdateResponseJson responseJson = (CheckOrgsUpdateResponseJson) httpResult.resultResponse;


                    needUpdate = handleOrgRefresh(needUpdate, responseJson);

                    needUpdate = handleOrgAdmin(needUpdate, responseJson);

                    needUpdate = handleOrgRemove(needUpdate, responseJson);


                    multiResult.localResult = new CheckOrgUpdateResult(needUpdate, userOrgLists);

                    LoginUserInfo.getInstance().setOrganizationSyncStatus(BaseApplicationLike.baseContext, true);

                }

                multiResult.httpResult = httpResult;
                return multiResult;
            }

            private boolean handleOrgRemove(boolean needUpdate, CheckOrgsUpdateResponseJson responseJson) {
                if (!ListUtil.isEmpty(responseJson.result.remove)) {
                    needUpdate = true;

                    removeData(responseJson);

                    handleRemoveOrgListAction(context, responseJson.result.remove);
                }
                return needUpdate;
            }

            private boolean handleOrgRefresh(boolean needUpdate, CheckOrgsUpdateResponseJson responseJson) {
                if (!ListUtil.isEmpty(responseJson.result.refresh)) {
                    needUpdate = true;

                    OrganizationRepository.getInstance().batchInsertOrUpdateOrganizations(responseJson.result.refresh);

                    String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
                    List<OrgRelationship> orgRelationshipList = OrgRelationship.produceOrgRelationshipListByOrgList(meUserId, OrgRelationship.Type.EMPLOYEE, responseJson.result.refresh);
                    OrgRelationShipRepository.getInstance().batchInsertRelation(orgRelationshipList);
                    updateOrgCodeListCache(OrgRelationship.getOrgCodeList(orgRelationshipList));

                    refreshData(responseJson);


                    //当前没组织, 则更新为currentOrgCode
                    if (StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(context))) {
                        Collections.sort(responseJson.result.refresh);

                        Organization org = responseJson.result.refresh.get(0);

                        OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(context, org.mOrgCode, false);
                    }
                }
                return needUpdate;
            }

            private boolean handleOrgAdmin(boolean needUpdate, CheckOrgsUpdateResponseJson responseJson) {
                OrgCache.getInstance().clear();
                //拿旧的admin列表做对比, 看是否需要更新
                List<String> oldAdminOrgCodeList = new ArrayList<>();

                for (Organization org : userOrgLists) {
                    if (org.isMeAdmin(context)) {
                        oldAdminOrgCodeList.add(org.mOrgCode);
                        OrgCache.getInstance().setAdminOrg(org.mOrgCode);
                    }
                }

                //当旧的 admin 列表总数变化, 或者内容变化时, 说明有更新,这时候更新数据库
                if (oldAdminOrgCodeList.size() != responseJson.result.admins.size() || !oldAdminOrgCodeList.containsAll(responseJson.result.admins)) {
                    needUpdate = true;

                    String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
                    List<OrgRelationship> orgRelationshipList = new ArrayList<>();

                    for (String orgCode : responseJson.result.admins) {
                        OrgRelationship relationship = new OrgRelationship();
                        relationship.mOrgCode = orgCode;
                        relationship.mUserId = meUserId;
                        relationship.mType = OrgRelationship.Type.ADMIN;
                        orgRelationshipList.add(relationship);

                    }

                    OrgRelationShipRepository.getInstance().batchInsertOrgRelationAndClean(meUserId, OrgRelationship.Type.ADMIN, orgRelationshipList);

                    updateDataAdmin(responseJson.result.admins);

                    //更新相关 session
                    updateOrgSession(responseJson.result.admins);

                }
                return needUpdate;
            }


            @Override
            protected void onPostExecute(MultiResult<CheckOrgUpdateResult> multiResult) {
                if (listUpdateListener == null) {
                    return;
                }
                CheckOrgUpdateResult checkOrgUpdateResult = multiResult.localResult;
                if (null != checkOrgUpdateResult) {
                    listUpdateListener.onRefresh(checkOrgUpdateResult);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listUpdateListener);
                }
            }


            private void updateDataAdmin(List<String> orgCodeList) {
                OrgCache.getInstance().clear();
                for (Organization organization : userOrgLists) {
                    //先清空管理者关系
                    organization.mAdminUserId = "";

                    //再更新
                    for (String orgCode : orgCodeList) {
                        if (organization.mOrgCode.equalsIgnoreCase(orgCode)) {
                            organization.mAdminUserId = LoginUserInfo.getInstance().getLoginUserId(context);
                            OrgCache.getInstance().setAdminOrg(organization.mOrgCode);
                            break;
                        }

                    }

                }
            }

            private void refreshData(CheckOrgsUpdateResponseJson responseJson) {
                List<Organization> needRefreshList = new ArrayList<>();
                for (Organization org : userOrgLists) {

                    for (Organization refreshOrg : responseJson.result.refresh) {
                        if (org.mOrgCode.equals(refreshOrg.mOrgCode)) {
                            needRefreshList.add(org);
                            break;
                        }

                    }
                }

                userOrgLists.removeAll(needRefreshList);
                userOrgLists.addAll(responseJson.result.refresh);

                //重新排序
                Collections.sort(userOrgLists);
            }

            private void removeData(CheckOrgsUpdateResponseJson responseJson) {
                boolean needRemove;
                //先删除缓存数据
                Iterator<Organization> organizationIterator = userOrgLists.iterator();
                while (organizationIterator.hasNext()) {
                    needRemove = false;

                    Organization organization = organizationIterator.next();
                    if (organization == null) {
                        continue;
                    }
                    for (String remove : responseJson.result.remove) {
                        if (organization.mOrgCode.equalsIgnoreCase(remove)) {
                            needRemove = true;
                            break;
                        }
                    }
                    if (needRemove) {
                        organizationIterator.remove();
                    }
                }
            }


        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 处理单个 org code 的情况
     *
     * @see #handleRemoveOrgListAction(Context, List)
     */
    public void handleRemoveOrgAction(Context context, String orgCode) {
        List<String> singleList = new ArrayList<>();
        singleList.add(orgCode);

        handleRemoveOrgListAction(context, singleList);
    }

    //todo 优化删除 的 sqlite 操作
    //todo 该方法包含同步与异步操作, 待优化

    /**
     * 处理组织被删除的系列操作, 包括删除 session, app, 切换currentOrgCode 等
     */
    public void handleRemoveOrgListAction(Context context, List<String> orgCodeList) {
        OrganizationManager.getInstance().removeOrgCodeListCache(orgCodeList);
        OrganizationRepository.getInstance().batchRemoveOrgs(orgCodeList);

        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context);
        OrgRelationShipRepository.getInstance().removeOrgRelationList(meUserId, -1, orgCodeList);
        //发送回执
        Session session = ChatSessionDataWrap.getInstance().getSessionSafely(OrgNotifyMessage.FROM, null);
        if (session != null) {
            ChatService.sendSessionReceipts(context, session, new HashSet<>(OrgApplyManager.getInstance().findUnreadMessagesByOrgCode(orgCodeList)));
        }

        for (String remove : orgCodeList) {
            removeDiscussionByOrgCode(remove);
            UnreadMessageRepository.getInstance().removeUnreadOrg(remove);
            OrgApplyRepository.getInstance().removeOrgApply(remove);
            removeAppByOrgCode(remove);
            removeAppSessionByOrgCode(remove);
        }


        //update org list
        ContactFragment.orgDataChanged(context);

        //switch the currentOrgCode
        if (orgCodeList.contains(currentOrgCode)) {
            queryLoginOrgCodeList(codeList -> {

                if (!ListUtil.isEmpty(codeList)) {
                    String newCurrentCode = codeList.get(0);
                    OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(context, newCurrentCode, false);

                } else {
                    //已经不存在任何组织了, 清空 currentCode
                    OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(context, StringUtils.EMPTY, false);
                }

                AppRefreshHelper.refreshAppAbsolutely();
                AboutMeFragment.refreshUserMsg();
            });
        }
    }


    private void updateOrgSession(List<String> admins) {
        Session session = ChatSessionDataWrap.getInstance().getSessionSafely(OrgNotifyMessage.FROM, null);

        if(null != session) {
            List<OrgApply> orgApplyList = OrgApplyRepository.getInstance().getAllOrgApply();

            OrgApply orgApply = getOrgApplyInYourAdmin(admins, orgApplyList);
            if(null != orgApply) {
                session.lastMessageText = orgApply.mLastMsgText;
                session.lastTimestamp = orgApply.mLastMsgTime;

                session.refreshUnreadSetTotally(UnreadMessageRepository.getInstance().queryUnreadSet(session.identifier), true);
                ChatSessionDataWrap.getInstance().updateSession(session);

            } else {
                session.refreshUnreadSetTotally(UnreadMessageRepository.getInstance().queryUnreadSet(session.identifier), true);
                ChatSessionDataWrap.getInstance().removeSessionSafely(OrgNotifyMessage.FROM);
            }


        }


        SessionRefreshHelper.notifyRefreshSessionAndCount();

    }



    @Nullable
    private OrgApply getOrgApplyInYourAdmin(List<String> adminOrgList, List<OrgApply> applyList) {
        for(OrgApply orgApply : applyList) {

            if(adminOrgList.contains(orgApply.mOrgCode)) {
                return orgApply;
            }
        }

        return null;
    }


    private void removeDiscussionByOrgCode(String removeOrgCode) {
        DiscussionDaoService.getInstance().queryLocalDiscussionByOrgId(removeOrgCode, discussionList -> {
            if (discussionList.isEmpty()) {
                return;
            }
            for (Discussion discussion : discussionList) {
                DiscussionDaoService.getInstance().removeDiscussion(discussion.mDiscussionId);
            }
        });
    }

    private void removeAppSessionByOrgCode(String removeOrgCode) {
        Iterator<Session> iterator = ChatSessionDataWrap.getInstance().getSessions().iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            if (session == null) {
                continue;
            }
            if (session.isAppType() && removeOrgCode.equalsIgnoreCase(session.orgId)) {
                //todo batch remove
                ChatSessionDataWrap.getInstance().removeSession(session.identifier, true);
            }
        }
    }

    private void removeAppByOrgCode(String removeOrgCode) {

        AppManager.getInstance().removeAppSyncStatus(removeOrgCode);

        AppDaoService.getInstance().queryLocalAppsByOrgId(removeOrgCode, (searchKey, appList) -> {
            if (!appList.isEmpty()) {
                Iterator<App> iterator = appList.iterator();

                while (iterator.hasNext()) {
                    AppDaoService.getInstance().removeApp(iterator.next().mAppId, null);
                }
            }

        });
    }

    /**
     * 查询用户和登录用户之间交集集合
     */
    @SuppressLint("StaticFieldLeak")
    public void queryOrgIntersectionInLoginUser(final String userId, final OnQueryOrgListListener listener) {
        new AsyncTask<Void, Void, List<Organization>>() {
            @Override
            protected List<Organization> doInBackground(Void... params) {

                return queryOrgIntersectionInLoginUserSync(userId);
            }

            @Override
            protected void onPostExecute(List<Organization> result) {
                if (listener == null) {
                    return;
                }
                listener.onSuccess(result);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @NonNull
    public List<Organization> queryOrgIntersectionInLoginUserSync(String userId) {
        List<Organization> orgIntersectionList = new ArrayList<>();

        List<String> myOrgList = getLoginOrgCodeListSync();
        //如果我没有任何组织架构雇员信息，也不必比较处理;
        if (myOrgList.isEmpty()) {
            return orgIntersectionList;
        }
        //他人组织架构
        List<String> userOrgList = OrgRelationShipRepository.getInstance().queryOrgCodeList(userId);
        //如果他没有任何雇员信息，不必往下比较处理
        if (userOrgList.isEmpty()) {
            return orgIntersectionList;
        }

        //取得两个列表交集
        userOrgList.retainAll(myOrgList);

        orgIntersectionList = OrganizationRepository.getInstance().queryOrganizationList(userOrgList);

        return orgIntersectionList;
    }


    /**
     * 获取登录用户 org list
     */
    @SuppressLint("StaticFieldLeak")
    public void queryLoginOrgList(final OnQueryOrgListListener listener) {
        new AsyncTask<Void, Void, List<Organization>>() {
            @Override
            protected List<Organization> doInBackground(Void... params) {
                List<String> myOrgCodeList = getLoginOrgCodeListSync();

                return OrganizationRepository.getInstance().queryOrganizationList(myOrgCodeList);
            }

            @Override
            protected void onPostExecute(List<Organization> organizationList) {
                listener.onSuccess(organizationList);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    @SuppressLint("StaticFieldLeak")
    public void queryLoginOrgCodeListTryCache(final BaseQueryListener<CopyOnWriteArraySet<String>> listener) {
        new AsyncTask<Void, Void, CopyOnWriteArraySet<String>>() {
            @Override
            protected CopyOnWriteArraySet<String> doInBackground(Void... params) {
                return queryLoginOrgCodeListTryCacheSync();
            }

            @Override
            protected void onPostExecute(CopyOnWriteArraySet<String> result) {
                if (listener == null) {
                    return;
                }
                listener.onSuccess(result);
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    @NonNull
    public CopyOnWriteArraySet<String> queryLoginOrgCodeListTryCacheSync() {
        if(ListUtil.isEmpty(mOrgCodeSet)) {
            if (null == mOrgCodeSet) {
                mOrgCodeSet = new CopyOnWriteArraySet<>();
            }

            List<String> myOrgCodeList = getLoginOrgCodeListSync();
            mOrgCodeSet.addAll(myOrgCodeList);
        }


        return mOrgCodeSet;
    }


    public void removeOrgCodeCache(String orgCode) {
        removeOrgCodeListCache(ListUtil.makeSingleList(orgCode));
    }

    public void removeOrgCodeListCache(List<String> orgCodeList) {
        if(null != mOrgCodeSet) {
            mOrgCodeSet.removeAll(orgCodeList);
        }
    }


    public void updateOrgCodeCache(String orgCode) {
        updateOrgCodeListCache(ListUtil.makeSingleList(orgCode));
    }

    public void updateOrgCodeListCache(List<String> orgCodeList) {
        if(null != mOrgCodeSet) {
            mOrgCodeSet.addAll(orgCodeList);
        }
    }


    public void updateAndCleanOrgCodeListCache(List<String> orgCodeList) {
        if(null != mOrgCodeSet) {
            mOrgCodeSet.clear();
        }

        updateOrgCodeListCache(orgCodeList);
    }


    /**
     * 获取登录用户org code列表
     *
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryLoginOrgCodeList(final OnQueryOrgCodeListListener listener) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                List<String> myOrgCodeList = getLoginOrgCodeListSync();
                return myOrgCodeList;
            }

            @Override
            protected void onPostExecute(List<String> result) {
                if (listener == null) {
                    return;
                }
                listener.onSuccess(result);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @NonNull
    public List<String> getLoginOrgCodeListSync() {
        return OrgRelationShipRepository.getInstance().queryOrgCodeList(LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext));
    }


    /**
     * 删除组织
     * 包括数据库
     */
    public void removeOrganizationRemote(final Context context, final String orgCode, final OnRemoveOrgListener listener) {

        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = OrganizationSyncNetService.getInstance().removeOrganization(context, orgCode);
                if (httpResult.isRequestSuccess()) {
                    OrganizationRepository.getInstance().removeOrg(orgCode);

                    String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
                    List<String> singleList = new ArrayList<>();
                    singleList.add(orgCode);
                    OrgRelationShipRepository.getInstance().removeOrgRelationList(meUserId, OrgRelationship.Type.EMPLOYEE, singleList);
                    EmployeeRepository.getInstance().removeEmployee(orgCode, LoginUserInfo.getInstance().getLoginUserId(context));


                    OrganizationManager.getInstance().handleRemoveOrgAction(context, orgCode);
                }

                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    listener.onSuccess();
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    public void receiveOrgNotify(OrgNotifyMessage orgNotifyMessage, boolean isCameFromOnline) {
        Context context = BaseApplicationLike.baseContext;
        final LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);


        if (OrgNotifyMessage.Operation.APPLYING.equals(orgNotifyMessage.mOperation)) {
            //1. DATABASE ORGLIST
            //2. LIST NOT CONTANIS ORGMESSAGE.ORCODE
            //TODO:性能待优化
            if (!isCameFromOnline) {
                List<String> orgList = OrganizationRepository.getInstance().queryAllOrganization();
                if (!orgList.contains(orgNotifyMessage.mOrgCode)) {
                    return;
                }
            }


            if (me.mUserId.equals(orgNotifyMessage.mAddresser.mUserId)) {
                return;
            }
            //TODO: 16/6/14 抽取方法

            OrgRelationship relationship = new OrgRelationship();
            relationship.mOrgCode = orgNotifyMessage.mOrgCode;
            relationship.mUserId = orgNotifyMessage.mAddresser.mUserId;
            relationship.mType = OrgRelationship.Type.APPLYING;
            relationship.mTime = orgNotifyMessage.deliveryTime;
            List<OrgRelationship> singleList = new ArrayList<>();
            singleList.add(relationship);

            OrganizationDaoService.getInstance().batchInsertRelation(singleList);

//            String sessionName = context.getString(R.string.org_applying);
            String sessionName = StringConstants.SESSION_NAME_ORG_APPLY;

            EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                    .setChatType(SessionType.Notice)
                    .setName(sessionName)
                    .setIdentifier(orgNotifyMessage.from)
                    .setDomainId(orgNotifyMessage.mFromDomain)
                    .setOrgId(orgNotifyMessage.mOrgCode);

            //创建会话
            Session session = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);
//            Session session = ChatSessionDataWrap.getInstance().getSession(orgNotifyMessage.from, null);
            ChatSessionDataWrap.getInstance().updateSessionForNotify(session, orgNotifyMessage, isCameFromOnline);

            if (isCameFromOnline && DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
                MessageNoticeManager.getInstance().showNotifyNotification(context, session);
            }

        } else if (OrgNotifyMessage.Operation.REJECTED.equals(orgNotifyMessage.mOperation)) {
            String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);

            if (meUserId.equals(orgNotifyMessage.mAuditor.mUserId)) {
                return;
            }
            ContactFragment.orgDataChanged(context);

            String content = "";
            if (!meUserId.equalsIgnoreCase(orgNotifyMessage.mAddresser.mUserId)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(context.getString(R.string.tip_rejected_someone_to_join_org, orgNotifyMessage.mOrgName, orgNotifyMessage.mAuditor.mName, orgNotifyMessage.mAddresser.mName));
                stringBuilder.append(" ");
                stringBuilder.append(context.getString(R.string.tip_org_end_key));
                content = stringBuilder.toString();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(context.getString(R.string.tip_rejected_to_join_org, orgNotifyMessage.mOrgName, orgNotifyMessage.mAuditor.mName));
                stringBuilder.append(" ");
                stringBuilder.append(context.getString(R.string.tip_rejected_to_join_org_end_key));
                content = stringBuilder.toString();
            }
            SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByOrgNoticeMessage(content, orgNotifyMessage);
            ChatSessionDataWrap.getInstance().asyncReceiveMessage(systemChatMessage, isCameFromOnline);
            ChatMessageHelper.notifyMessageReceived(systemChatMessage);
            SessionRefreshHelper.notifyRefreshSessionAndCount();

        } else if (OrgNotifyMessage.Operation.APPROVED.equals(orgNotifyMessage.mOperation)) {

            String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);


            if (!meUserId.equals(orgNotifyMessage.mAuditor.mUserId)) {
                Organization org = queryOrgRemoteSync(context, orgNotifyMessage.mOrgCode);

                if (null == org) {
                    org = orgNotifyMessage.getBasicOrg();
                }

                List<Organization> singList = new ArrayList<>();
                singList.add(org);

                List<OrgRelationship> orgRelationshipList = OrgRelationship.produceOrgRelationshipListByOrgList(meUserId, OrgRelationship.Type.EMPLOYEE, singList);
                OrganizationDaoService.getInstance().batchInsertRelation(orgRelationshipList);

                //更新数据库
                OrganizationDaoService.getInstance().batchInsertOrganization(singList);

                updateOrgCodeListCache(OrgRelationship.getOrgCodeList(orgRelationshipList));

                //当前没组织, 则更新为currentOrgCode
                if (StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(context))) {
                    OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(context, org.mOrgCode, false);

                }

                ContactFragment.orgDataChanged(context);

                //同步该组织最新的 app
                final Organization finalOrg = org;

                AppManager.getInstance().getAppCheckUpdateController().initAppsSyncData(finalOrg.mOrgCode, null, new AppManager.CheckAppListUpdateListener() {

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                    }

                    @Override
                    public void refresh(boolean needUpdate) {
                        if(needUpdate) {
                            AppRefreshHelper.refreshApp();
                        }

                    }
                });

//                AppManager.getInstance().syncApp(context, finalOrg.mOrgCode, new AppManager.OnSyncAppListListener() {
//                    @Override
//                    public void onSuccess() {
//                        //新加入的组织为为当前组织, 需要刷新 app 界面
//                        if(PersonalShareInfo.getInstance().getCurrentOrg(context).equals(finalOrg.mOrgCode)) {
//                            AppRefreshHelper.refreshAppAbsolutely();
//                        }
//                    }
//
//                    @Override
//                    public void networkFail(int errorCode, String errorMsg) {
//                        ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
//                    }
//                });

                String content = "";
                if (!meUserId.equalsIgnoreCase(orgNotifyMessage.mAddresser.mUserId)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(context.getString(R.string.tip_approved_to_join_org_by_admin, orgNotifyMessage.mOrgName, orgNotifyMessage.mAuditor.mName, orgNotifyMessage.mAddresser.mName));
                    stringBuilder.append(" ");
                    stringBuilder.append(context.getString(R.string.tip_org_end_key));
                    content = stringBuilder.toString();

                } else {
                    content = context.getString(R.string.tip_approved_to_join_org, orgNotifyMessage.mOrgName);
                }


                SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByOrgNoticeMessage(content, orgNotifyMessage);
                ChatSessionDataWrap.getInstance().asyncReceiveMessage(systemChatMessage, isCameFromOnline);
                ChatMessageHelper.notifyMessageReceived(systemChatMessage);
                SessionRefreshHelper.notifyRefreshSessionAndCount();
                //如果组织有更新，批量更新一下组织设置
                OrganizationSettingsHelper.getInstance().checkOrgSettingsUpdate(context, -1);
            }


        }
    }

    public Organization getOrganizationSyncByOrgCode(Context context, String orgCode) {
       return queryOrgSync(context, orgCode);
    }






    public List<ApplyingOrganization> queryApplyingListBySystemMsgSync(Context context) {
        List<OrgApply> allOrgApply = OrgApplyRepository.getInstance().getAllOrgApply();
        List<ApplyingOrganization> organizations = new ArrayList<>();
        for (OrgApply orgApply : allOrgApply) {
            Organization organization = OrganizationRepository.getInstance().queryOrganization(orgApply.mOrgCode);
            if (organization == null) {
                continue;
            }
            ApplyingOrganization applyingOrganization = new ApplyingOrganization();
            applyingOrganization.mContent = orgApply.mLastMsgText;
            applyingOrganization.mOrgCode = orgApply.mOrgCode;
            applyingOrganization.mOrgLogo = organization.mLogo;
            applyingOrganization.mOrgName = organization.mName;
            applyingOrganization.mTwOrgName = organization.mTwName;
            applyingOrganization.mEnOrgName = organization.mEnName;
            applyingOrganization.mAppliedTime = orgApply.mLastMsgTime;
            applyingOrganization.mUnreadMsgIdList = UnreadMessageRepository.getInstance().queryUnreadOrgApplyCountByOrgCode(orgApply.mOrgCode);
            organizations.add(applyingOrganization);

        }

        return organizations;

    }

    @SuppressLint("StaticFieldLeak")
    public void isCurrentOrgYouOwner(Context context, BaseQueryListener<Boolean> listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return isCurrentOrgYouOwnerSync(context);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    public boolean isCurrentOrgYouOwnerSync(Context context) {

        Organization organization = OrganizationRepository.getInstance().queryOrganization(PersonalShareInfo.getInstance().getCurrentOrg(context));
        if(null != organization) {
            return organization.isMeOwner(context);
        }
        return false;
    }

    public boolean isCurrentOrgYouAdminSync(Context context) {
       return isLoginAdminOrgSync(context, PersonalShareInfo.getInstance().getCurrentOrg(context));
    }

    @SuppressLint("StaticFieldLeak")
    public void queryLoginAdminOrg(Context context, OnQueryLoginAdminOrgSyncListener onQueryLoginAdminOrgSyncListener) {
        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {
                return OrganizationRepository.getInstance().queryLoginAdminOrgSync(context);
            }

            @Override
            protected void onPostExecute(List<String> adminOrgCodeList) {
                onQueryLoginAdminOrgSyncListener.onSuccess(adminOrgCodeList);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 判断组织是否是登录用户管理着的
     * @param context
     * @param orgCode
     *
     * */
    public boolean isLoginAdminOrgSync(Context context, String orgCode) {
        return OrganizationRepository.getInstance().queryLoginAdminOrgSync(context).contains(orgCode);
    }


    /**
     * 获取用户当前本地的 org setting list
     */
    public void queryOrgLocalHavingCircle(Context context, OnQueryOrgListListener listener) {
        new AsyncTask<Void, Void, List<Organization>>() {
            @Override
            protected List<Organization> doInBackground(Void... params) {
                return queryOrgLocalHavingCircleSync(context);
            }

            @Override
            protected void onPostExecute(List<Organization> orgList) {
                listener.onSuccess(orgList);

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * 读取本地有"同事圈"的组织
     *
     * @param context
     * @return orgHavingCircle
     */
    @NonNull
    private List<Organization> queryOrgLocalHavingCircleSync(Context context) {
        List<Organization> orgList = OrganizationDaoService.getInstance().queryLoginOrganizationsSync(context);
        List<Organization> orgHavingCircle = new ArrayList<>();

        for (Organization org : orgList) {
            if (org == null) {
                continue;
            }
            if (OrganizationSettingsManager.getInstance().handleMyCircleFeature(org.mOrgCode)) {
                orgHavingCircle.add(org);
            }

        }
        return orgHavingCircle;
    }

    /**
     * 根据部门ID，获取下级部门信息
     *
     * @param orgId
     * @param employeeTreeLoadListener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryUserOrgAndEmployeeFromRemote(final Context context, final String orgCode, final String orgId, final int level, final QureyOrganizationViewRange range, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final OrganizationAsyncNetService.OnEmployeeTreeLoadListener employeeTreeLoadListener) {
        new AsyncTask<String, Double, HttpResult>() {
            @Override
            protected HttpResult doInBackground(String... params) {

                Boolean filterSenior = EmpSeniorHelper.getFilterSeniorAction(context, expandEmpTreeAction, orgCode);

                if(null == filterSenior) {
                    return HttpResult.getInstance().netException(null);
                }

                boolean filterRank = true;
                //选人模式时, 需要启动聊天视图过滤
                boolean rankView = !expandEmpTreeAction.mIsSelectMode;

                QueryOrganizationViewRequest queryOrganizationViewRequest = new QueryOrganizationViewRequest();
                queryOrganizationViewRequest.setOrgCode(orgCode);
                queryOrganizationViewRequest.setOrgId(orgId);
                queryOrganizationViewRequest.setFilterSenior(filterSenior);
                queryOrganizationViewRequest.setFilterRank(filterRank);
                queryOrganizationViewRequest.setRankView(rankView);
                queryOrganizationViewRequest.setQueryOrgViewRange(range);

                HttpResult httpResult;
                List<OrganizationResult> organizationList;
                if (TOP.equals(orgId)) {
                    httpResult = OrganizationSyncNetService.getInstance().queryOrganizationViewByCode(context, queryOrganizationViewRequest);

                } else {
                    httpResult = OrganizationSyncNetService.getInstance().queryOrganizationViewByOrgId(context, queryOrganizationViewRequest);

                }

                if(httpResult.isRequestSuccess()) {



                    EmployeesTreeResponseJson employeesTreeResponseJson = (EmployeesTreeResponseJson) httpResult.resultResponse;
                    organizationList = employeesTreeResponseJson.result;


                    checkClearSubChildren(organizationList);

                    //update local dept trees in single thread
                    if (range.havingLocalFeature()) {
                        DbThreadPoolExecutor.getInstance().execute(() -> DeptManager.INSTANCE.updateDeptsDbSync(orgId, filterSenior, rankView, organizationList));
                    }


                    assembleOrganizationList(organizationList, level, range, ContactModel.LOAD_STATUS_LOADED_REMOTE);

                }


                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    EmployeesTreeResponseJson employeesTreeResponseJson = (EmployeesTreeResponseJson) httpResult.resultResponse;
                    List<OrganizationResult> organizationList = employeesTreeResponseJson.result;
                    employeeTreeLoadListener.onSuccess(ContactModel.LOAD_STATUS_LOADED_REMOTE, organizationList);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, employeeTreeLoadListener);
                }



            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public  void assembleOrganizationList(List<OrganizationResult> organizationList, int level, QureyOrganizationViewRange range, int loadedStatus) {
        for (ContactModel contactModel : organizationList) {
            if (contactModel == null) {
                continue;
            }
            contactModel.addLoadedStatus(loadedStatus);
            contactModel.expand = true;

            contactModel.level = level;
            if (contactModel instanceof OrganizationResult) {
                OrganizationResult organization = (OrganizationResult)contactModel;
                if (!ListUtil.isEmpty(organization.employeeResults) && organization.employeeResults.size() < range.getEmployeeLimit()) {
                    organization.employeeResults.get(organization.employeeResults.size() -1).isLoadCompleted = true;
                }
                if (!ListUtil.isEmpty(organization.children) && organization.children.size() < range.getOrgLimit()) {
                    organization.children.get(organization.children.size() -1).isLoadCompleted = true;
                }
            }

            for (ContactModel subContactModel : contactModel.subContactModel()) {
                subContactModel.level = level + 1;
                subContactModel.selected = contactModel.selected;
                subContactModel.parentOrgId = contactModel.id;
            }
        }
    }

    private void checkClearSubChildren(List<OrganizationResult> organizationList) {
        if (!ListUtil.isEmpty(organizationList)) {
            List<OrganizationResult> children = organizationList.get(0).children;

            for(OrganizationResult child : children) {
                child.children = new ArrayList<>();  //make sub children empty
                child.employeeResults = new ArrayList<>(); //make sub children empty
            }
        }
    }


    /**
     * 查询org_id下面的所有人员
     * 包括子部门
     */
    @SuppressLint("StaticFieldLeak")
    public void queryRecursionEmployeeByOrgId(final Context context, final String orgCode, final String orgId, final QureyOrganizationViewRange range, @NonNull ExpandEmpTreeAction expandEmpTreeAction, final OrganizationAsyncNetService.OnQueryEmployeeListener listener) {

        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                Boolean filterSenior = EmpSeniorHelper.getFilterSeniorAction(context, expandEmpTreeAction, orgCode);

                if(null == filterSenior) {
                    return HttpResult.getInstance().netException(null);
                }

                boolean filterRank = true;
                //选人模式时, 需要启动聊天视图过滤
                boolean rankView = !expandEmpTreeAction.mIsSelectMode;


                QueryOrganizationViewRequest queryOrganizationViewRequest = new QueryOrganizationViewRequest();
                queryOrganizationViewRequest.setOrgCode(orgCode);
                queryOrganizationViewRequest.setOrgId(orgId);
                queryOrganizationViewRequest.setFilterSenior(filterSenior);
                queryOrganizationViewRequest.setFilterRank(filterRank);
                queryOrganizationViewRequest.setRankView(rankView);
                queryOrganizationViewRequest.setQueryOrgViewRange(range);

                return OrganizationSyncNetService.getInstance().queryRecursionEmployeeByOrgId(context, queryOrganizationViewRequest);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    RecursionEmpListResponseJson responseJson = (RecursionEmpListResponseJson) httpResult.resultResponse;
                    listener.onSuccess(responseJson.empList);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * 检索部门数据
     * @param context
     * @param searchId
     * @param searchKeyValue
     * @param listener
     */
    public void searchDepartmentRemote(final Context context, final String searchId, final String searchKeyValue, List<String>listRange, RemoteSearchDepartmentsListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                SearchDepartmentRequest request = new SearchDepartmentRequest();
                request.setOrgCodes(ListUtil.isEmpty(listRange) ? OrganizationRepository.getInstance().queryLoginOrgCodeListSync(context) : listRange);
                request.setFullNamePath(true);
                request.setQuery(searchKeyValue);
                return OrganizationSyncNetService.getInstance().searchDepartments(context, request);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if(httpResult.isRequestSuccess()) {
                    SearchDepartmentResp resp = (SearchDepartmentResp)httpResult.resultResponse;
                    if (resp == null) {
                        listener.networkFail(-1, "error");
                    } else {
                        listener.onSuccess(searchId, resp.mResult);
                    }
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void autoJoinOrg(final Context context,final OnAutoJoinOrgListener onAutoJoinOrgListener){
        new AsyncTask<Void, Void, Organization>() {

            @Override
            protected Organization doInBackground(Void... voids) {
               HttpResult httpResult = OrganizationSyncNetService.getInstance().fetchAutoJoinOrg(context);
               if(httpResult.isRequestSuccess()) {
                   OrganizationListResponse listResponse = (OrganizationListResponse) httpResult.resultResponse;
                   if(!listResponse.organizationList.isEmpty()){
                       Organization organization = listResponse.organizationList.get(0);
                       OrganizationSyncNetService.getInstance().autoJoinOrg(context,organization);
                       return organization;
                   }
               }
                return null;
            }

            @Override
            protected void onPostExecute(Organization organization) {
                onAutoJoinOrgListener.onSuccess(organization);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }



    // TODO: 16/6/14 使用 join 查询优化 ??

    /**
     * 获取申请组织列表(不包含自己)
     *
     * @param context
     * @return applyingOrganizationsList
     */
    @NonNull
    public List<ApplyingOrganization> queryApplyingListSync(Context context) {
        List<OrgRelationship> applyingList = OrgRelationShipRepository.getInstance().queryRelationList(OrgRelationship.Type.APPLYING);
        OrgRelationship removeOrg = null;
        //remove self
        for (OrgRelationship orgRelationship : applyingList) {
            String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);

            if (orgRelationship.mUserId.equals(meUserId)) {
                removeOrg = orgRelationship;
                break;
            }
        }

        if (null != removeOrg) {
            applyingList.remove(removeOrg);
        }


        List<Organization> organizationList = OrganizationRepository.getInstance().queryOrganizationList(OrgRelationship.getOrgCodeList(applyingList));

        List<ApplyingOrganization> applyingOrganizationsList = new ArrayList<>();
        for (OrgRelationship orgRelationship : applyingList) {
            ApplyingOrganization applyingOrganization = new ApplyingOrganization();
            applyingOrganization.mApplicantId = orgRelationship.mUserId;
            applyingOrganization.mAppliedTime = orgRelationship.mTime;

            for (Organization org : organizationList) {

                if (org.mOrgCode.equals(orgRelationship.mOrgCode)) {
                    applyingOrganization.mOrg = org;
                    break;
                }
            }

            applyingOrganizationsList.add(applyingOrganization);
        }

        return applyingOrganizationsList;
    }


    public class CheckOrgUpdateResult {
        public CheckOrgUpdateResult(boolean shouldUpdate, List<Organization> orgList) {
            this.shouldUpdate = shouldUpdate;
            this.orgList = orgList;
        }

        public boolean shouldUpdate;
        public List<Organization> orgList;
    }

    public interface OnRemoveOrgListener extends NetWorkFailListener {
        void onSuccess();
    }

    public interface OnGetOrgListener {
        void success(@NonNull Organization org);
    }

    public interface OnCheckOrgListUpdateListener extends NetWorkFailListener {
        void onRefresh(CheckOrgUpdateResult checkOrgUpdateResult);
    }

    /**
     * 返回组织 code list 的监听器
     */
    public interface OnQueryOrgCodeListListener {
        void onSuccess(List<String> orgCodeList);
    }

    /**
     * 查询用户与登录用户之间机构集合监听器
     */
    public interface OnQueryOrgListListener {
        void onSuccess(List<Organization> orgList);
    }

    /**
     *  返回组织 code list 的监听器
     * */
    public interface OnQueryLoginAdminOrgSyncListener {
        void onSuccess(List<String> orgCodeList);
    }

    public interface OnGetOrgSettingsListListener {
        void onSuccess(List<OrganizationSettings> settingList);
    }

    public interface RemoteSearchDepartmentsListener extends NetWorkFailListener {
        void onSuccess(String searchKey, List<Department> departments);
    }

    public interface OnAutoJoinOrgListener {
        void onSuccess(Organization organization);
    }



}
