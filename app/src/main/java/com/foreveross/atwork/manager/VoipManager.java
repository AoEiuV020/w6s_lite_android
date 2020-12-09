package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.db.service.repository.VoipMeetingRecordRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.api.sdk.voip.VoipMeetingSyncService;
import com.foreveross.atwork.api.sdk.voip.requestJson.ZoomHandleInfo;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.api.sdk.voip.responseJson.InviteMembersResponseJson;
import com.foreveross.atwork.api.sdk.voip.responseJson.JoinConfResponseJson;
import com.foreveross.atwork.api.sdk.voip.responseJson.MeetingMember;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.VoipControllerStrategy;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberSettingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dasunsy on 16/8/17.
 */
public class VoipManager {

    private static Object sLock = new Object();

    private static VoipManager sInstance = null;

    public HashMap<String, VoipMeetingGroup> mVoipMeetingMap = new HashMap<>();

    //存储会议是否开启过视频过状态
    public Set<String> mMeetingHasOpenVideoStatusMap = new HashSet<>();

    public TimeController mTimeController = new TimeController();

    public OfflineController mOfflineController = new OfflineController();

    public VoipControllerStrategy mVoipMeetingController;

    private CallState mCallState = CallState.CallState_Idle;

    private ScheduledExecutorService mKeepAliveService = Executors.newScheduledThreadPool(1);

    private ScheduledFuture mKeepAliveFuture;


    private VoipManager() {
        mVoipMeetingController = VoipMeetingController.getInstance();
    }

    public static VoipManager getInstance() {
        /**
         * double check
         * */
        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new VoipManager();
                }

            }
        }

        return sInstance;

    }

    public TimeController getTimeController() {
        return mTimeController;
    }

    public OfflineController getOfflineController() {
        return mOfflineController;
    }


    public VoipControllerStrategy getVoipMeetingController() {
        return mVoipMeetingController;
    }

    public void setCallState(CallState callState) {
        this.mCallState = callState;
    }

    public CallState getCallState() {
        return this.mCallState;
    }

    public void voipStartHeartBeat(Context context, String meetingId) {

        voipStopHeartBeat();

        mKeepAliveFuture = mKeepAliveService.scheduleAtFixedRate(() -> {
            if(VoipHelper.isMeetingOpening(meetingId)) {
                refreshMeeting(context, meetingId, null);

            }

        }, 0, 15, TimeUnit.SECONDS);
    }

    public void voipStopHeartBeat() {
        if(null != mKeepAliveFuture) {
            mKeepAliveFuture.cancel(true);
            mKeepAliveFuture = null;
        }
    }

    public void insertVoipMeetingGroupSync(VoipMeetingGroup voipMeetingGroup) {
        mVoipMeetingMap.put(voipMeetingGroup.mMeetingId, voipMeetingGroup);
        VoipMeetingRecordRepository.getInstance().insertVoipMeeting(voipMeetingGroup);
    }

    public void setMeetingHasOpenVideo(String meetingId) {
        mMeetingHasOpenVideoStatusMap.add(meetingId);
    }

    public boolean isMeetingHasOpenVideo(String meetingId) {
        return mMeetingHasOpenVideoStatusMap.contains(meetingId);
    }


    @Nullable
    public VoipMeetingGroup queryVoipMeetingGroup(String meetingId) {
        if (mVoipMeetingMap.containsKey(meetingId)) {
            return mVoipMeetingMap.get(meetingId);

        } else {
            VoipMeetingGroup voipMeetingGroup = VoipMeetingRecordRepository.getInstance().queryVoipMeeting(meetingId);
            if (null != voipMeetingGroup) {
                mVoipMeetingMap.put(meetingId, voipMeetingGroup);
            }

            return voipMeetingGroup;
        }
    }


    /**
     * 发起 voip 会议
     */
    @SuppressLint("StaticFieldLeak")
    public void createMeeting(final Context context, @Nullable final MeetingInfo meetingInfo, final VoipType voipType, final List<UserHandleInfo> memberList, @NonNull final OnCreateAndQueryVoipMeetingListener onCreateAndQueryVoipMeetingListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                UserHandleInfo userHandleInfo = UserHandleInfo.findLoginUserHandleInfo(context, memberList);
                HttpResult httpResult = VoipMeetingSyncService.createMeeting(context, meetingInfo, userHandleInfo, voipType, memberList, null);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {


                    CreateOrQueryMeetingResponseJson resultResponse = (CreateOrQueryMeetingResponseJson) httpResult.resultResponse;

                    List<VoipMeetingMember> participantList = resultResponse.toParticipantList();

                    if (VoipHelper.isGroupType(meetingInfo)) {
                        mTimeController.monitorVoipMembers(context, participantList);
                    }

                    if (VoipSdkType.QSY != AtworkConfig.VOIP_SDK_TYPE) {
                        VoipMeetingController.getInstance().refreshCurrentMeetingMembersUid(participantList);
                    }

                    onCreateAndQueryVoipMeetingListener.onSuccess(resultResponse);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onCreateAndQueryVoipMeetingListener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 查询 voip 会议
     */
    public static void queryMeetingRemote(final Context context, final String meetingId, final OnCreateAndQueryVoipMeetingListener onCreateAndQueryVoipMeetingListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = VoipMeetingSyncService.queryMeeting(context, meetingId);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    onCreateAndQueryVoipMeetingListener.onSuccess((CreateOrQueryMeetingResponseJson) httpResult.resultResponse);
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onCreateAndQueryVoipMeetingListener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 邀请加入 voip 会议
     */
    @SuppressLint("StaticFieldLeak")
    public void inviteMeeting(final Context context, final String meetingId, @Nullable final MeetingInfo meetingInfo, final VoipType voipType, final List<UserHandleInfo> memberList, final OnInviteVoipMeetingListener onHandledResultListener) {

        if(ListUtil.isEmpty(memberList)) {
            return;
        }

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = VoipMeetingSyncService.inviteMeeting(context, meetingId, meetingInfo, voipType, memberList, null);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    InviteMembersResponseJson inviteMembersResponseJson = (InviteMembersResponseJson) httpResult.resultResponse;

                    //后台不返回来的用户表示后台里尚在会议, 属于重新邀请的用户
                    checkMembersStillMeeting(inviteMembersResponseJson, memberList);

                    onHandledResultListener.onSuccess(inviteMembersResponseJson.toParticipantList(meetingId));

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onHandledResultListener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private void checkMembersStillMeeting(InviteMembersResponseJson inviteMembersResponseJson, List<UserHandleInfo> memberList) {
        if(inviteMembersResponseJson.mMemberList.size() != memberList.size()) {
            for(UserHandleInfo userHandleInfo : memberList) {
                boolean hasFound = false;
                for(MeetingMember member : inviteMembersResponseJson.mMemberList) {

                    if(member.mUser.mUserId.equals(userHandleInfo.mUserId)){
                        hasFound = true;
                        break;
                    }

                }

                if(!hasFound) {
                    VoipMeetingMember voipMeetingMember = VoipMeetingController.getInstance().findMember(userHandleInfo.mUserId);
                    if (null != voipMeetingMember) {
                        voipMeetingMember.setUserStatus(UserStatus.UserStatus_NotJoined);
                    }
                }
            }
        }
    }

    /**
     * 主动取消会议
     * @param context
     * @param meetingInfo
     * @param currentVoipMeeting 当不为空时, 表示需要维护网络发生错误的情况时, 语音消息以及历史纪录的数据
     * @param meetingId
     * @param operator
     * @param zoomInfo
     * @param onHandleVoipMeetingListener
     */
    public static void leaveMeeting(final Context context, @Nullable final MeetingInfo meetingInfo, @Nullable CurrentVoipMeeting currentVoipMeeting, final String meetingId, final UserHandleInfo operator, @Nullable ZoomHandleInfo zoomInfo, final OnHandleVoipMeetingListener onHandleVoipMeetingListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = VoipMeetingSyncService.leaveMeeting(context, meetingId, meetingInfo, operator, zoomInfo);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    onHandleVoipMeetingListener.onSuccess();
                } else {
                    if(null != currentVoipMeeting) {
                        VoipHelper.handleUpdateMeetingRecord(context, currentVoipMeeting, MeetingStatus.SUCCESS);
                    }

                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onHandleVoipMeetingListener);


                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * 加入会议
     */
    public static void joinMeeting(final Context context, @Nullable final MeetingInfo meetingInfo, final String meetingId, final VoipType voipType, @Nullable ZoomHandleInfo zoomInfo, final OnGetJoinTokenListener onGetJoinTokenListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {

                HttpResult httpResult = VoipMeetingSyncService.joinMeeting(context, meetingId, meetingInfo, voipType, zoomInfo);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    JoinConfResponseJson responseJson = (JoinConfResponseJson) httpResult.resultResponse;
                    if (!StringUtils.isEmpty(responseJson.mResult.mToken)) {
                        Logger.e("qsy", "get token time -> " + System.currentTimeMillis() + "   and token -> " + responseJson.mResult.mToken);

                        onGetJoinTokenListener.onSuccess(responseJson.mResult.mToken);

                    }
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onGetJoinTokenListener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    /**
     * 拒绝电话
     * @param context
     * @param meetingInfo
     * @param currentVoipMeeting 当不为空时, 表示需要维护网络发生错误的情况时, 语音消息以及历史纪录的数据
     * @param meetingId
     * @param onHandleVoipMeetingListener
     */
    @SuppressLint("StaticFieldLeak")
    public void rejectMeeting(final Context context, @Nullable final MeetingInfo meetingInfo, @Nullable CurrentVoipMeeting currentVoipMeeting, final String meetingId, @Nullable ZoomHandleInfo zoomInfo, final OnHandleVoipMeetingListener onHandleVoipMeetingListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                UserHandleInfo userHandleInfo = AtworkApplicationLike.getLoginUserHandleInfo(context);

                HttpResult httpResult = VoipMeetingSyncService.rejectConf(context, meetingId, meetingInfo, userHandleInfo, zoomInfo);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    onHandleVoipMeetingListener.onSuccess();
                } else {

                    if(null != currentVoipMeeting) {
                        VoipHelper.handleUpdateMeetingRecord(context, currentVoipMeeting, MeetingStatus.FAILED);
                    }

                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onHandleVoipMeetingListener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * 正在繁忙, 不处理其他电话
     */
    public static void busyMeeting(final Context context, @Nullable final MeetingInfo meetingInfo, final String meetingId, final OnHandleVoipMeetingListener onHandleVoipMeetingListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = VoipMeetingSyncService.busyMeeting(context, meetingId, meetingInfo);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    onHandleVoipMeetingListener.onSuccess();
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onHandleVoipMeetingListener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * 定期请求, 通知后台服务当前成员仍然在会议中, 后台也用此接口来调整语音会议时长, 避免因意外断开会议导致无法统计的情况
     * */
    public static void refreshMeeting(final Context context, final String meetingId, @Nullable VoipMeetingMemberSettingInfo meetingMemberSettingInfoReceived) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                VoipMeetingMemberSettingInfo meetingMemberSettingInfo = meetingMemberSettingInfoReceived;

                if(null == meetingMemberSettingInfo) {
                    meetingMemberSettingInfo = new VoipMeetingMemberSettingInfo();
                    if(VoipMeetingController.getInstance().getMySelf().mIsMute) {
                        meetingMemberSettingInfo.muteVoice = VoipMeetingMemberSettingInfo.VOICE_MUTED;
                    } else {
                        meetingMemberSettingInfo.muteVoice = VoipMeetingMemberSettingInfo.VOICE_NOT_MUTED;

                    }
                }

                HttpResult httpResult = VoipMeetingSyncService.refreshMeeting(context, meetingId, meetingMemberSettingInfo);
                return httpResult;
            }


            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    CreateOrQueryMeetingResponseJson responseJson = (CreateOrQueryMeetingResponseJson) httpResult.resultResponse;
                    //群聊时开启心跳监控模式
                    if(VoipMeetingController.getInstance().isGroupChat()) {

                        VoipMeetingController.getInstance().calibrateStatusAndRefreshUI(responseJson);
                    }
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }






    public interface OnInviteVoipMeetingListener extends NetWorkFailListener {
        void onSuccess(List<VoipMeetingMember> meetingMemberList);
    }

    public interface OnCreateAndQueryVoipMeetingListener extends NetWorkFailListener {
        void onSuccess(CreateOrQueryMeetingResponseJson responseJson);
    }

    public interface OnHandleVoipMeetingListener extends NetWorkFailListener {
        void onSuccess();
    }

    public interface OnGetJoinTokenListener extends NetWorkFailListener {
        void onSuccess(String token);
    }


    /**
     * 维护用户离线状态的控制类
     * */
    public static class OfflineController {

        //续命+1s X 15
        private final int OFFLINE_OVERTIME = 15;

        private ScheduledExecutorService mCheckOfflineVoipScheduled = Executors.newScheduledThreadPool(AtworkConfig.VOIP_MEMBER_COUNT_MAX);
        private SparseArray<ScheduledFuture> mFutureResultMap = new SparseArray<>();

        public void checkOfflineStatus(Context context, List<VoipMeetingMember> memberArray) {
            for(VoipMeetingMember member : memberArray) {
                if(!User.isYou(context, member.mUserId) && "meeting".equalsIgnoreCase(member.mMeetingStatus)) {
                    checkOfflineStatus(member.getUid());
                }
            }
        }

        public void checkOfflineStatus(int uid) {
            ScheduledFuture scheduledFuture = mCheckOfflineVoipScheduled.schedule(() -> {

                mFutureResultMap.remove(uid);

                VoipMeetingMember meetingMember = VoipMeetingController.getInstance().findMember(uid);


                if (!VoipMeetingController.getInstance().isGroupChat()) {

                    if(null != meetingMember) {
                        if(!User.isYou(BaseApplicationLike.baseContext, meetingMember.getId())) {
                            VoipMeetingController.getInstance().stopCall();

                            UserHandleInfo loginUser = AtworkApplicationLike.getLoginUserHandleInfo(BaseApplicationLike.baseContext);
                            //离开会议
                            if (null != loginUser) {
                                VoipMeetingSyncService.leaveMeeting(BaseApplicationLike.baseContext, VoipManager.getInstance().getVoipMeetingController().getWorkplusVoipMeetingId(), null, loginUser, null);
                            }
                        }
                    }
                } else {

                    if(null != meetingMember) {
                        VoipManager.getInstance().getVoipMeetingController().setParticipantStatusAndRefreshUI(meetingMember, UserStatus.UserStatus_Left);

                    }

                }


            }, OFFLINE_OVERTIME, TimeUnit.SECONDS);

            mFutureResultMap.put(uid, scheduledFuture);
        }

        public void cancel(int uid) {
            ScheduledFuture scheduledFuture = mFutureResultMap.get(uid);
            if(null != scheduledFuture) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }

            mFutureResultMap.remove(uid);
        }


        public void cancelAll() {
            for (int index = 0; index < mFutureResultMap.size(); index++) {
                ScheduledFuture scheduledFuture = mFutureResultMap.valueAt(index);
                scheduledFuture.cancel(true);
            }

            mFutureResultMap.clear();

        }

    }


    /**
     * 管理群组语音聊天超时邀请等的控制类, 在对方超过60s 没有答应时, 把"待加入"的状态改为"已退出"
     */
    public static class TimeController {
        private ScheduledExecutorService mCheckDiscussionVoipScheduled = Executors.newScheduledThreadPool(AtworkConfig.VOIP_MEMBER_COUNT_MAX);

        private HashMap<String, ScheduledFuture> mFutureResultMap = new HashMap<>();

        private HashMap<String, List<VoipMeetingMember>> mMonitoredMemberMap = new HashMap<>();

        public void monitorVoipMembers(Context context, List<VoipMeetingMember> memberList) {

            List<VoipMeetingMember> copiedMemberList = new ArrayList<>();
            copiedMemberList.addAll(memberList);

            //过滤掉自己以及正在 meeting 的人
            List<VoipMeetingMember> userRemovedList = new ArrayList<>();
            for (VoipMeetingMember voipMeetingMember : copiedMemberList) {
                if (User.isYou(context, voipMeetingMember.mUserId) || "meeting".equalsIgnoreCase(voipMeetingMember.mMeetingStatus)) {
                    userRemovedList.add(voipMeetingMember);
                }
            }

            if (!ListUtil.isEmpty(userRemovedList)) {
                copiedMemberList.removeAll(userRemovedList);
            }

            String scheduleKey = UUID.randomUUID().toString();

            ScheduledFuture scheduledFuture = mCheckDiscussionVoipScheduled.schedule(() -> {


                List<VoipMeetingMember> membersMonitored = mMonitoredMemberMap.get(scheduleKey);
                if (!ListUtil.isEmpty(membersMonitored)) {
//                    TangSDKInstance.getInstance().setParticipantsStatusAndRefreshUI(membersMonitored, UserStatus.UserStatus_Rejected);

                    //拼凑超时呼叫的名单
                    StringBuilder overTimeNameList = new StringBuilder();
                    for (int i = 0; i < membersMonitored.size(); i++) {
                        VoipMeetingMember member = membersMonitored.get(i);
                        overTimeNameList.append(member.mShowName);
                        if (i != membersMonitored.size() - 1) {
                            overTimeNameList.append("、");
                        }
                    }


                    if (VoipHelper.isHandlingVoipCallExcludeInit()) {
                        String busyTip = context.getString(R.string.call_overtime, overTimeNameList.toString());
                        VoipManager.getInstance().getVoipMeetingController().tipToast(busyTip);
                    }
                    VoipManager.getInstance().getVoipMeetingController().removeParticipantsAndRefreshUI(UserHandleInfo.toUserIdList(membersMonitored));

                    mFutureResultMap.remove(scheduleKey);
                    mMonitoredMemberMap.remove(scheduleKey);
                }

            }, AtworkConfig.VOIP_MAX_WAIT_ANSWER_DURATION, TimeUnit.SECONDS);

            mMonitoredMemberMap.put(scheduleKey, copiedMemberList);
            mFutureResultMap.put(scheduleKey, scheduledFuture);
        }

        public void cancel(String userId) {
            for (List<VoipMeetingMember> memList : mMonitoredMemberMap.values()) {
                VoipMeetingMember memberRemoved = null;

                for (VoipMeetingMember member : memList) {
                    if (member.mUserId.equals(userId)) {
                        memberRemoved = member;

                        break;
                    }
                }

                if (null != memberRemoved) {
                    memList.remove(memberRemoved);
                }

            }
        }

        public void cancelAll() {
            for (ScheduledFuture scheduledFuture : mFutureResultMap.values()) {
                scheduledFuture.cancel(true);
            }

            mFutureResultMap.clear();
            mMonitoredMemberMap.clear();

        }


    }

    /**
     * 通过 userId 在本地查找对应的 domainId
     */
    public String findDomainId(String workplusMeetingId, String userId) {
        VoipMeetingGroup voipMeetingGroup = VoipManager.getInstance().queryVoipMeetingGroup(workplusMeetingId);
        VoipMeetingMember peerMember = null;
        if (null != voipMeetingGroup) {
            peerMember = voipMeetingGroup.findMember(userId);
        }

        if (null != peerMember) {
            return peerMember.mDomainId;

        } else {
            return AtworkConfig.DOMAIN_ID;
        }
    }

    public void filterContactsInMeeting(List<ShowListItem> memberList) {
        List<VoipMeetingMember> memberListInMeeting = getVoipMeetingController().getVoipMemInMeetingList();

        List<ShowListItem> removedContacts = new ArrayList<>();

        for(ShowListItem contact : memberList) {

            for (VoipMeetingMember member : memberListInMeeting) {
                if (member.getId().equals(contact.getId())) {
                    removedContacts.add(contact);
                    break;
                }
            }

        }

        memberList.removeAll(removedContacts);
    }

}
