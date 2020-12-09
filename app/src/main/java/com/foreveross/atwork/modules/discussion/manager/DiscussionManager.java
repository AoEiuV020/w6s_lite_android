package com.foreveross.atwork.modules.discussion.manager;/**
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


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.db.service.repository.DiscussionMemberRepository;
import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreverht.db.service.repository.EmployeeRepository;
import com.foreverht.db.service.repository.UserRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.api.sdk.discussion.DiscussionSyncNetService;
import com.foreveross.atwork.api.sdk.discussion.requestJson.AddOrRemoveDiscussionJSON;
import com.foreveross.atwork.api.sdk.discussion.requestJson.DiscussionSettingsRequest;
import com.foreveross.atwork.api.sdk.discussion.requestJson.ModifyDiscussionJSON;
import com.foreveross.atwork.api.sdk.discussion.requestJson.TransferOrBecomeOwnerRequestJson;
import com.foreveross.atwork.api.sdk.discussion.responseJson.CreateDiscussionResponseJson;
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryDiscussionResponseJson;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.WorkplusQrCodeInfo;
import com.foreveross.atwork.api.sdk.users.UserSyncNetService;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionOwner;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;

/**
 * 群组管理类
 * Created by reyzhang22 on 16/3/25.
 */
public class DiscussionManager {

    private static final String TAG = DiscussionManager.class.getName();

    private static final Object sLock = new Object();

    private static DiscussionManager sInstance = new DiscussionManager();

    private List<Discussion> mDiscussionList;

    private DiscussionManager() {

    }

    public static DiscussionManager getInstance() {
        return sInstance;
    }

    public List<Discussion> getDiscussionListSync() {
        checkDiscussionListData();
        return mDiscussionList;
    }


    public List<Discussion> getInternalDiscussionListSync() {
        List<Discussion> discussionList = getDiscussionListSync();


        if (null != discussionList) {
            return CollectionsKt.filter(discussionList, Discussion::isInternalDiscussion);
        }

        return discussionList;
    }

    @Nullable
    public List<Discussion> getDiscussionListNotCheck() {
        return mDiscussionList;
    }

    public void setDiscussions(List<Discussion> discussionList) {
        synchronized (sLock) {
            if (null == mDiscussionList) {
                mDiscussionList = new ArrayList<>();
            }
        }

        mDiscussionList.clear();

        for (Discussion discussion : discussionList) {
            if (!mDiscussionList.contains(discussion)) {
                mDiscussionList.add(discussion);
            }
        }
    }


    public void addDiscussion(Discussion discussion) {
        checkDiscussionListData();

        if (!mDiscussionList.contains(discussion)) {
            mDiscussionList.add(discussion);
        }
    }


    public void removeDiscussion(String discussionId) {
        checkDiscussionListData();

        Discussion discussionRemoved = null;

        for (Discussion discussion : mDiscussionList) {
            if (discussionId.equals(discussion.mDiscussionId)) {
                discussionRemoved = discussion;
                break;
            }
        }

        if (null != discussionRemoved) {
            mDiscussionList.remove(discussionRemoved);
        }
    }

    public void clear() {
        if (null != mDiscussionList) {
            mDiscussionList.clear();
            mDiscussionList = null;
        }
    }


    private void checkDiscussionListData() {
        synchronized (sLock) {
            if (null == mDiscussionList) {
                mDiscussionList = new ArrayList<>();
                mDiscussionList.addAll(DiscussionRepository.getInstance().queryAllDiscussions());
            }
        }
    }

    public void queryDiscussion(Context context, final String discussionId, DiscussionAsyncNetService.OnQueryDiscussionListener listener) {
        queryDiscussion(context, discussionId, false, false, listener);
    }

    /**
     * @see #queryDiscussionSync(Context, String, boolean)
     */
    @SuppressLint("StaticFieldLeak")
    public void queryDiscussion(Context context, final String discussionId, boolean takeMemberDetailInfo, boolean forcedNeedEmpInfo, DiscussionAsyncNetService.OnQueryDiscussionListener listener) {
        Discussion discussionCache = DiscussionCache.getInstance().getDiscussionCache(discussionId);
        if(!takeMemberDetailInfo && null != discussionCache && !ListUtil.isEmpty(discussionCache.mMemberList)) {
            listener.onSuccess(discussionCache);
            return;
        }


        new AsyncTask<Void, Void, MultiResult<Discussion>>() {
            @Override
            protected MultiResult<Discussion> doInBackground(Void... params) {

                return queryDiscussionResultSync(context, discussionId, takeMemberDetailInfo, forcedNeedEmpInfo);
            }

            @Override
            protected void onPostExecute(MultiResult<Discussion> multiResult) {
                Discussion discussion = getDiscussionFromMultiResult(multiResult);
                if (null != discussion) {
                    listener.onSuccess(discussion);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener);

                }
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    @Nullable
    public Discussion queryDiscussionSync(Context context, String discussionId) {
        return queryDiscussionSync(context, discussionId, false);
    }

    /**
     * 遵循缓存->数据库->网络的顺序去获取群组信息, 同步的方法(不含有详细的 user 信息), 返回 discussion 对象
     */
    @Nullable
    public Discussion queryDiscussionSync(Context context, String discussionId, boolean takeMemberDetailInfo) {
        MultiResult<Discussion> multiResult = queryDiscussionResultSync(context, discussionId, takeMemberDetailInfo, false);
        return getDiscussionFromMultiResult(multiResult);
    }

    @Nullable
    public Discussion getDiscussionFromMultiResult(MultiResult<Discussion> multiResult) {
        Discussion discussion = null;
        if (null != multiResult.localResult) {
            discussion = multiResult.localResult;

        } else {
            if (multiResult.httpResult.isRequestSuccess()) {
                discussion = ((QueryDiscussionResponseJson) multiResult.httpResult.resultResponse).discussion;
            }

        }
        return discussion;
    }


    /**
     * 遵循缓存->数据库->网络的顺序去获取群组信息, 同步的方法(不含有详细的 user 信息), 返回混合结果
     */
    public MultiResult<Discussion> queryDiscussionResultSync(Context context, String discussionId, boolean takeMemberDetailInfo, boolean forcedNeedEmpInfo) {
        MultiResult<Discussion> multiResult = new MultiResult<>();

        Discussion discussion = DiscussionCache.getInstance().getDiscussionCache(discussionId);
        if (null == discussion || ListUtil.isEmpty(discussion.mMemberList)) {
            discussion = DiscussionRepository.getInstance().queryDiscussionDetailInfo(discussionId);

            if (null != discussion) {
                DiscussionCache.getInstance().setDiscussionCache(discussion.mDiscussionId, discussion);
            }
        }
        //save local result
        multiResult.localResult = discussion;


        if (null == discussion || ListUtil.isEmpty(discussion.mMemberList)) {
            HttpResult httpResult = DiscussionSyncNetService.getInstance().queryDiscussionDetailInfo(context, discussionId);

            //save http result
            multiResult.httpResult = httpResult;

            if (httpResult.isRequestSuccess()) {
                QueryDiscussionResponseJson queryDiscussionResponseJson = (QueryDiscussionResponseJson) httpResult.resultResponse;
                discussion = queryDiscussionResponseJson.discussion;

            }
            if (null != discussion) {
                DiscussionRepository.getInstance().insertDiscussion(discussion);
                DiscussionManager.getInstance().addDiscussion(discussion);

            }
        }

        if (takeMemberDetailInfo && null != discussion && !ListUtil.isEmpty(discussion.mMemberList)) {
            List<String> userIdList = discussion.toMemberIdList();
            if (discussion.showEmployeeInfo() || forcedNeedEmpInfo) {

                String discussionOrg = discussion.getOrgCodeCompatible();

                if(StringUtils.isEmpty(discussionOrg)) {
                    discussionOrg = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);
                }

                EmployeeManager.getInstance().syncNotExistEmpListSync(context, userIdList, discussionOrg);
                List<Employee> memberDetailList = EmployeeRepository.getInstance().queryEmpListByIdsWithNotExist(userIdList, discussionOrg);
                //使用雇员身份
                for (Employee employee : memberDetailList) {
                    employee.setEmpParticipant();
                }
                discussion.refreshMemberContactsDetailInfo(memberDetailList);

            } else {
                UserManager.getInstance().syncNotExistUsersSync(context, userIdList);
                List<User> memberDetailList = UserRepository.getInstance().queryUsersByIdsWithNotExist(userIdList);

                discussion.refreshMemberContactsDetailInfo(memberDetailList);
            }
        }

        return multiResult;
    }

    /**
     * 网络拿去最新的群组信息, 若 token 失效, 会做踢出操作
     */
    @Nullable
    public Discussion queryDiscussionDetailInfoRemote(final Context context, final String discussionId) {
        Discussion discussion = null;

        HttpResult httpResult = DiscussionSyncNetService.getInstance().queryDiscussionDetailInfo(context, discussionId);
        if (httpResult.isRequestSuccess()) {
            QueryDiscussionResponseJson queryDiscussionResponseJson = (QueryDiscussionResponseJson) httpResult.resultResponse;
            discussion = queryDiscussionResponseJson.discussion;
        } else {
            //做 token 认证
            if (null != httpResult.resultResponse) {
                ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);
            }

        }

        return discussion;
    }

    @SuppressLint("StaticFieldLeak")
    public void touchDiscussionReadFeatureThreshold(final Context context, final String discussionId, BaseQueryListener<Boolean> listener) {
        Discussion discussionCache = DiscussionCache.getInstance().getDiscussionCache(discussionId);
        if(null != discussionCache) {
            listener.onSuccess(touchDiscussionReadFeatureThresholdSync(discussionCache));
            return;
        }

        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                return touchDiscussionReadFeatureThresholdSync(context, discussionId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                listener.onSuccess(result);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * 是否进行群已读 forward 的特性, 并且群聊天也显示双勾
     * */
    public boolean touchDiscussionReadFeatureThresholdSync(final Context context, final String discussionId) {
        Discussion discussion = queryDiscussionSync(context, discussionId);
        return touchDiscussionReadFeatureThresholdSync(discussion);
    }

    private boolean touchDiscussionReadFeatureThresholdSync(Discussion discussion) {
        if(null != discussion && discussion.mMemberList.size() > DomainSettingsManager.getInstance().getDiscussionReadFeatureThreshold()) {
            return true;
        }
        return false;
    }


    @SuppressLint("StaticFieldLeak")
    public void createDiscussion(Context context, List<ShowListItem> contactList, @Nullable String name, @Nullable String avatar , @Nullable String orgCode, @Nullable String templateId, boolean needEvent, final DiscussionAsyncNetService.OnCreateDiscussionListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                User loginUser = AtworkApplicationLike.getLoginUserSync();

                HttpResult result = DiscussionSyncNetService.getInstance().createDiscussion(context, loginUser, name, avatar, orgCode, templateId, contactList);
                if (result.isRequestSuccess()) {
                    Discussion discussion = ((CreateDiscussionResponseJson) result.resultResponse).discussion;
                    DiscussionRepository.getInstance().insertDiscussion(discussion);
                    DiscussionManager.getInstance().addDiscussion(discussion);


                    //创建会话
                    EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Discussion, discussion);

                    ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

                    //赋予 member 加上 discussionId
                    if (!ListUtil.isEmpty(discussion.mMemberList)) {
                        for (DiscussionMember member : discussion.mMemberList) {
                            member.discussionId = discussion.mDiscussionId;
                        }
                    }

                    if (needEvent) {
//                        createDiscussionEvent(context, discussion);

                    }

                }

                return result;

            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    Discussion discussion = ((CreateDiscussionResponseJson) result.resultResponse).discussion;

                    listener.onDiscussionSuccess(discussion);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 修改群组信息
     *
     * @param context
     * @param discussion
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void modifyDiscussion(Context context, Discussion discussion, final DiscussionAsyncNetService.HandledResultListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                final ModifyDiscussionJSON json = ModifyDiscussionJSON.createModifyJSON(discussion);
                HttpResult result = DiscussionSyncNetService.getInstance().modifyDiscussion(context, discussion.mDiscussionId, new Gson().toJson(json));
                if (result.isRequestSuccess()) {
                    DiscussionRepository.getInstance().modifyDiscussion(discussion);

                    Discussion cacheDiscussion = DiscussionCache.getInstance().getDiscussionCache(discussion.mDiscussionId);
                    if (null != cacheDiscussion) {
                        cacheDiscussion.mName = discussion.mName;
                        cacheDiscussion.mIntro = discussion.mIntro;
                        cacheDiscussion.mAvatar = discussion.mAvatar;
                    }

                }
                return result;
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    listener.success();
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    public void setDiscussionSettings(Context context, String discussionId, DiscussionSettingsRequest request,
                                      UserSyncNetService.OnUserConversationsListener listener) {
        DiscussionAsyncNetService.getInstance().setDiscussionSettings(context, discussionId, request, listener);
    }

    @Deprecated
    public void getDiscussionSettings(Context context, String discussionId, UserSyncNetService.GetUserConversationsListener listener) {
        DiscussionAsyncNetService.getInstance().getDiscussionSettings(context, discussionId, listener);
    }

    @SuppressLint("StaticFieldLeak")
    public void becomeOwner(final Context context, Discussion discussion, final BaseCallBackNetWorkListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                TransferOrBecomeOwnerRequestJson transferOrBecomeOwnerRequestJson = new TransferOrBecomeOwnerRequestJson();
                transferOrBecomeOwnerRequestJson.mOps = TransferOrBecomeOwnerRequestJson.Options.OWNER;

                HttpResult httpResult = DiscussionSyncNetService.getInstance().transferOrBecomeOwner(context, discussion.mDiscussionId, JsonUtil.toJson(transferOrBecomeOwnerRequestJson));

                if (httpResult.isRequestSuccess()) {
                    UserHandleInfo meUser = AtworkApplicationLike.getLoginUserHandleInfo(context);
                    discussion.mOwner = new DiscussionOwner();
                    discussion.mOwner.refresh(meUser);

                    DiscussionRepository.getInstance().updateDiscussionOwner(discussion.mDiscussionId, discussion.mOwner);
                }
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    listener.onSuccess();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    /**
     * 转移群猪
     *
     * @param context
     * @param discussion
     * @param newOwnerSelected
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void transferOwner(final Context context, Discussion discussion, ShowListItem newOwnerSelected, final BaseCallBackNetWorkListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                UserHandleInfo newOwner = ContactHelper.toUserHandleInfo(newOwnerSelected);
                TransferOrBecomeOwnerRequestJson transferOrBecomeOwnerRequestJson = new TransferOrBecomeOwnerRequestJson(newOwner);

                HttpResult httpResult = DiscussionSyncNetService.getInstance().transferOrBecomeOwner(context, discussion.mDiscussionId, JsonUtil.toJson(transferOrBecomeOwnerRequestJson));

                if (httpResult.isRequestSuccess()) {
                    discussion.mOwner.refresh(newOwnerSelected);
                    DiscussionRepository.getInstance().updateDiscussionOwner(discussion.mDiscussionId, discussion.mOwner);
                }
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    listener.onSuccess();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 邀请群员进群组
     *
     * @param act
     * @param groupJoinInfo
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void inviteToDiscussion(final Activity act, final WorkplusQrCodeInfo groupJoinInfo, final DiscussionAsyncNetService.HandledResultListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                User handleUser = new User();
                handleUser.mUserId = groupJoinInfo.getUserId();
                handleUser.mDomainId = groupJoinInfo.getDomainId();

                UserHandleInfo loginUserInfo = AtworkApplicationLike.getLoginUserHandleInfo(act);

                final AddOrRemoveDiscussionJSON inviteJson = AddOrRemoveDiscussionJSON.createInvite(handleUser, loginUserInfo, groupJoinInfo.getPinCode());
                HttpResult result = DiscussionSyncNetService.getInstance().addOrRemoveDiscussionMember(act, groupJoinInfo.getDiscussionId(), new Gson().toJson(inviteJson));
                if (result.isRequestSuccess()) {
//                    inviteGroupEvent(act, groupJoinInfo);
                }
                return result;
            }

            @Override
            protected void onPostExecute(HttpResult result) {

                if (result.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    @SuppressLint("StaticFieldLeak")
    public void addMember(final Context context, final String discussionId, final List<UserHandleInfo> memberInfoList, final DiscussionAsyncNetService.HandledResultListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                final AddOrRemoveDiscussionJSON groupAddOrRemoveJSON = AddOrRemoveDiscussionJSON.createAdd(memberInfoList);
                HttpResult result = DiscussionSyncNetService.getInstance().addOrRemoveDiscussionMember(context, discussionId, new Gson().toJson(groupAddOrRemoveJSON));
                if (result.isRequestSuccess()) {
                    DiscussionMemberRepository.getInstance().batchInsertDiscussionMembers(toMemberList(discussionId, memberInfoList));
                }

                return result;
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    public void removeMember(final Context context, final String discussionId, final ShowListItem discussionMember, final DiscussionAsyncNetService.HandledResultListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                final AddOrRemoveDiscussionJSON groupAddOrRemoveJSON = AddOrRemoveDiscussionJSON.createRemove(discussionMember);
                HttpResult result = DiscussionSyncNetService.getInstance().addOrRemoveDiscussionMember(context, discussionId, new Gson().toJson(groupAddOrRemoveJSON));
                if (result.isRequestSuccess()) {
                    DiscussionMemberRepository.getInstance().removeDiscussionMember(discussionId, discussionMember.getId());
                }
                return result;
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }




    @SuppressLint("StaticFieldLeak")
    public void quitDiscussion(final Context context, String userId, String discussionId, final DiscussionAsyncNetService.HandledResultListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult result = DiscussionSyncNetService.getInstance().deleteDiscussion(context, userId, discussionId);

                if (result.isRequestSuccess()) {
                    ChatDetailExposeBroadcastSender.doNotCheckSession(BaseApplicationLike.baseContext, discussionId);

                    DiscussionDaoService.getInstance().removeDiscussionSync(discussionId);
                    ChatSessionDataWrap.getInstance().removeSessionSafely(discussionId);


                }


                return result;
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    @SuppressLint("StaticFieldLeak")
    public void dismissDiscussion(final Context context, String discussionId, final DiscussionAsyncNetService.HandledResultListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult result = DiscussionSyncNetService.getInstance().dismissDiscussion(context, discussionId);

                if (result.isRequestSuccess()) {
                    ChatDetailExposeBroadcastSender.doNotCheckSession(BaseApplicationLike.baseContext, discussionId);

                    DiscussionDaoService.getInstance().removeDiscussionSync(discussionId);
                    ChatSessionDataWrap.getInstance().removeSessionSafely(discussionId);


                }


                return result;
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }



    @SuppressLint("StaticFieldLeak")
    public void syncInsertDiscussions(Context context, List<Discussion> discussions) {
        if (!ListUtil.isEmpty(discussions)) {
            new AsyncTask<Void, Void, Boolean>() {


                @Override
                protected Boolean doInBackground(Void... params) {
                    DiscussionRepository.getInstance().removeAllDiscussions();
                    for (Discussion discussion : discussions) {
                        DiscussionCache.getInstance().removeDiscussionCache(discussion.mDiscussionId);
                    }

                    return DiscussionRepository.getInstance().batchInsertDiscussions(discussions);
                }
            }.executeOnExecutor(DbThreadPoolExecutor.getInstance());

        }

    }


    public List<DiscussionMember> toMemberList(String discussionId, List<UserHandleInfo> handleInfoList) {
        List<DiscussionMember> memberList = new ArrayList<>();
        for (UserHandleInfo handleInfo : handleInfoList) {
            DiscussionMember groupMember = new DiscussionMember(discussionId, handleInfo.mUserId, handleInfo.mDomainId);
            memberList.add(groupMember);
        }

        return memberList;
    }


    public void goChatDetailAct(Activity act, Discussion discussion) {
        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Discussion, discussion);

        getChatDetailAct(act, entrySessionRequest);
    }

    public void getChatDetailAct(Activity act, EntrySessionRequest entrySessionRequest) {


        ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

        Intent intent = ChatDetailActivity.getIntent(act, entrySessionRequest.mIdentifier);
        act.startActivity(intent);
        //界面切换效果
        act.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Nullable
    public Discussion updateDiscussionByRemoteSync(Context ctx, final String discussionId) {
        Discussion discussion = queryDiscussionDetailInfoRemote(ctx, discussionId);

        if (discussion != null) {
            DiscussionRepository.getInstance().insertDiscussion(discussion);
            DiscussionManager.getInstance().addDiscussion(discussion);
        }
        return discussion;
    }

}
