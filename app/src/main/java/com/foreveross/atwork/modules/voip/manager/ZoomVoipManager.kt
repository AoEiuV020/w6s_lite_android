package com.foreveross.atwork.modules.voip.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import com.foreveross.atwork.api.sdk.voip.VoipMeetingSyncService
import com.foreveross.atwork.api.sdk.voip.requestJson.ZoomHandleInfo
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson
import com.foreveross.atwork.api.sdk.voip.responseJson.InviteMembersResponseJson
import com.foreveross.atwork.api.sdk.voip.responseJson.QueryZoomTypeMeetingStatusResponse
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo
import com.foreveross.atwork.infrastructure.model.voip.*
import com.foreveross.atwork.infrastructure.model.zoom.ZoomJoinMeetingHistoryDataItem
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage
import com.foreveross.atwork.infrastructure.plugin.zoom.IZoomMeetingFinishedListenerProxy
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.manager.VoipManager
import com.foreveross.atwork.modules.voip.activity.zoom.ZoomCallActivity
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager.queryZoomTypeMeetingStatus
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnControllerVoipListener
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnVoipStatusListener
import com.foreveross.atwork.modules.voip.support.qsy.utils.VibratorUtil
import com.foreveross.atwork.modules.voip.support.zoom.interfaces.OnZoomVoipHandleListener
import com.foreveross.atwork.modules.voip.utils.SoundHelper
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import java.util.*


object ZoomVoipManager {

    var currentVoipMeeting: CurrentVoipMeeting? = null
    var onControllerVoipListener: OnControllerVoipListener? = null
    var onZoomVoipHandleListener: OnZoomVoipHandleListener? = null
    var onVoipStatusListener: OnVoipStatusListener? = null

    var callState = CallState.CallState_Idle

    var zoomJoinMeetingHistoryDataItemTempSaved: ZoomJoinMeetingHistoryDataItem? = null

    fun init() {
        ZoomManager.addMeetingFinishedListener(AtworkApplicationLike.baseContext, object : IZoomMeetingFinishedListenerProxy {

            override fun onMeetingFinished() {
                ZoomManager.isMeetingCalling = false

                currentVoipMeeting?.mWorkplusVoipMeetingId?.let { leaveMeeting(AtworkApplicationLike.baseContext, it) }

                stopCall()

                LogUtil.e("ZoomManager.isMeetingCalling : ${ZoomManager.isMeetingCalling}")

            }

            override fun inMeetingStatus() {
                ZoomManager.isMeetingCalling = true
                zoomJoinMeetingHistoryDataItemTempSaved?.let { ZoomManager.updateJoinData(AtworkApplicationLike.baseContext, it) }
                zoomJoinMeetingHistoryDataItemTempSaved = null

                LogUtil.e("ZoomManager.isMeetingCalling : ${ZoomManager.isMeetingCalling}")

            }

        })
    }

    fun getMemberList(): List<VoipMeetingMember>? {
        if(!isCurrentVoipMeetingValid()) {
            return null
        }

        val memberList = ArrayList<VoipMeetingMember>()

        if (isGroupChat()) {
            currentVoipMeeting?.mMeetingGroup?.mParticipantList?.let { memberList.addAll(it) }

        } else {
            getMySelf()?.let { memberList.add(it) }

            getPeer()?.let { memberList.add(it) }

        }

        return memberList
    }

    fun isCalling(): Boolean {
        return isVoipCalling() || ZoomManager.isMeetingCalling
    }

    fun isVoipCalling(): Boolean {
        return CallState.CallState_Idle != callState && CallState.CallState_Ended != callState
    }

    fun isHandlingVoipCallExcludeInit(): Boolean {
        return isVoipCalling() && CallState.CallState_Init != VoipManager.getInstance().callState
    }

    fun isHandlingVoipCallAndInit(): Boolean {
        return isVoipCalling() && CallState.CallState_Init == VoipManager.getInstance().callState
    }

    fun setCurrentVoipMeetingId(meetingId: String) {
        currentVoipMeeting?.mWorkplusVoipMeetingId = meetingId
    }


    fun isCurrentMeetingHandling(voipPostMessage: VoipPostMessage): Boolean {
        return voipPostMessage.mMeetingId == currentVoipMeeting?.mWorkplusVoipMeetingId
    }

    fun isCurrentVoipMeetingValid(): Boolean {
        return null != currentVoipMeeting
    }

    @SuppressLint("StaticFieldLeak")
    fun queryZoomTypeMeetingStatus(meetingId: String, listener: BaseNetWorkListener<Boolean>) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                return VoipMeetingSyncService.queryZoomTypeMeetingStatus(meetingId)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    val response = result.resultResponse as QueryZoomTypeMeetingStatusResponse
                    listener.onSuccess(response.result)
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance())
    }


    fun clearData() {
        currentVoipMeeting = null

        callState = CallState.CallState_Idle

        onControllerVoipListener = null
        onVoipStatusListener = null
        onZoomVoipHandleListener = null
        zoomJoinMeetingHistoryDataItemTempSaved = null
    }

    fun stopCall() {
        changeCallState(CallState.CallState_Ending)
        changeCallState(CallState.CallState_Ended)

        clearData()
    }


    fun changeCallState(callState: CallState) {
        val oldCallState = this.callState

        if (CallState.CallState_ReConnecting == callState || callState != oldCallState) {
            this.callState = callState

            onVoipStatusListener?.onCallStateChanged(callState)

            when (callState) {
                CallState.CallState_Idle -> {
                }
                CallState.CallState_Init -> {
                    if(UserType.Originator != getMySelf()?.userType) {
                        SoundHelper.getInstance().play(BaseApplicationLike.baseContext)

                    }
                }

                CallState.CallState_StartCall -> {
                }
                CallState.CallState_Waiting -> {
                }
                CallState.CallState_Calling -> {
                    if (CallState.CallState_ReConnecting != oldCallState && CallState.CallState_Disconnected != oldCallState) {
//                        startCountDuration()
                    }

                    if (UserType.Originator == getMySelf()?.userType) {
                        VibratorUtil.Vibrate(BaseApplicationLike.baseContext, 100)

                    }

                    SoundHelper.getInstance().stop()
                }
                CallState.CallState_Disconnected -> {
                }
                CallState.CallState_ReConnecting -> {
                }
                CallState.CallState_Ended -> SoundHelper.getInstance().release()
                else -> {
                }
            }
        }

    }

    fun isGroupChat(): Boolean = null != currentVoipMeeting?.mMeetingGroup

    fun getMySelf(): VoipMeetingMember? = currentVoipMeeting?.mCallSelf

    fun getPeer(): VoipMeetingMember? = currentVoipMeeting?.mCallPeer

    fun getInvitor(): UserHandleInfo? = currentVoipMeeting?.mCallInviter


    fun goToCallActivity(context: Context, workplusVoipMeetingId: String? = null, meetingInfo: MeetingInfo, voipType: VoipType, isGroupType: Boolean, isOriginator: Boolean, contactSelectList: List<ShowListItem>, joinToken: String? = null, inviter: UserHandleInfo? = null) {

        setCurrentVoipMeeting(context, isGroupType, isOriginator, contactSelectList, workplusVoipMeetingId, meetingInfo, voipType, joinToken, inviter)

        val intent = ZoomCallActivity.getIntent(context)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        context.startActivity(intent)
    }

    fun setCurrentVoipMeeting(context: Context, isGroupType: Boolean, isOriginator: Boolean, contactSelectList: List<ShowListItem>, workplusVoipMeetingId: String?, meetingInfo: MeetingInfo, voipType: VoipType, joinToken: String?, inviter: UserHandleInfo?) {
        val callParams = VoipHelper.getCallParams(context, isGroupType, isOriginator, contactSelectList)

        setCurrentVoipMeeting(workplusVoipMeetingId, meetingInfo, voipType, callParams, joinToken, inviter)
    }

    private fun setCurrentVoipMeeting(workplusVoipMeetingId: String?, meetingInfo: MeetingInfo, voipType: VoipType, callParams: CallParams, joinToken: String?, inviter: UserHandleInfo?) {

        clearData()

        currentVoipMeeting = CurrentVoipMeeting()

        with(currentVoipMeeting!!) {
            mWorkplusVoipMeetingId = workplusVoipMeetingId
            mMeetingInfo = meetingInfo
            mVoipType = voipType
            mCallParams = callParams

            if (callParams.isGroupChat) {
                mCallSelf = callParams.mMySelf
                mMeetingGroup = callParams.mGroup

            } else {
                mCallSelf = callParams.mMySelf
                mCallPeer = callParams.mPeer

            }

            mCallInviter = inviter
            mJoinToken = joinToken
        }



    }


    @SuppressLint("StaticFieldLeak")
    fun createMeeting(context: Context, meetingInfo: MeetingInfo, voipType: VoipType = VoipType.VIDEO, memberList: List<UserHandleInfo>, onCreateAndQueryVoipMeetingListener: VoipManager.OnCreateAndQueryVoipMeetingListener) {

        object : AsyncTask<Void, Void, HttpResult>(){
            override fun doInBackground(vararg p0: Void?): HttpResult {

                val zoomHandleInfo = ZoomHandleInfo(bizconfHoldDuration = 30)
                val userHandleInfo = UserHandleInfo.findLoginUserHandleInfo(context, memberList)
                val httpResult = VoipMeetingSyncService.createMeeting(context, meetingInfo, userHandleInfo, voipType, memberList, zoomHandleInfo)
                return httpResult
            }

            override fun onPostExecute(httpResult: HttpResult) {
                if (httpResult.isRequestSuccess) {


                    val resultResponse = httpResult.resultResponse as CreateOrQueryMeetingResponseJson

                    //自己主动创建会议的时候, 会议 id 需要接口回调后设置
                    setCurrentVoipMeetingId(resultResponse.mResult.mMeetingId)

                    val participantList = resultResponse.toParticipantList()

                    onCreateAndQueryVoipMeetingListener.onSuccess(resultResponse)

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onCreateAndQueryVoipMeetingListener)

                }

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    @SuppressLint("StaticFieldLeak")
    fun inviteMeeting(context: Context, meetingId: String, meetingInfo: MeetingInfo, voipType: VoipType, memberList: List<UserHandleInfo>, onHandledResultListener: VoipManager.OnInviteVoipMeetingListener) {
        object : AsyncTask<Void, Void, HttpResult>(){

            override fun doInBackground(vararg p0: Void?): HttpResult {
                return VoipMeetingSyncService.inviteMeeting(context, meetingId, meetingInfo, voipType, memberList, ZoomHandleInfo())
            }

            override fun onPostExecute(httpResult: HttpResult) {
                if (httpResult.isRequestSuccess) {
                    val inviteMembersResponseJson = httpResult.resultResponse as InviteMembersResponseJson


                    onHandledResultListener.onSuccess(inviteMembersResponseJson.toParticipantList(meetingId))

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onHandledResultListener)

                }

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

    @SuppressLint("StaticFieldLeak")
    fun leaveMeeting(context: Context,  meetingId: String, meetingInfo: MeetingInfo? = null) {

        AsyncTaskThreadPool.getInstance().execute {
            VoipMeetingSyncService.leaveMeeting(context, meetingId, meetingInfo, AtworkApplicationLike.getLoginUserHandleInfo(context), ZoomHandleInfo())
        }
    }



    fun joinMeeting(context: Context, meetingInfo: MeetingInfo, meetingId: String, voipType: VoipType = VoipType.VIDEO, onGetJoinTokenListener: VoipManager.OnGetJoinTokenListener) {
        VoipManager.joinMeeting(context, meetingInfo, meetingId, voipType, ZoomHandleInfo(), onGetJoinTokenListener)
    }

}