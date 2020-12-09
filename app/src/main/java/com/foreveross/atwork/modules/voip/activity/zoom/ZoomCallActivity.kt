package com.foreveross.atwork.modules.voip.activity.zoom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.voip.requestJson.ZoomHandleInfo
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager
import com.foreveross.atwork.infrastructure.model.voip.*
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper
import com.foreveross.atwork.manager.VoipManager
import com.foreveross.atwork.manager.VoipMeetingController
import com.foreveross.atwork.modules.voip.fragment.zoom.ZoomCallFragment
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnControllerVoipListener
import com.foreveross.atwork.modules.voip.support.zoom.interfaces.OnZoomVoipHandleListener
import com.foreveross.atwork.support.SingleFragmentActivity
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil

class ZoomCallActivity: SingleFragmentActivity(), OnControllerVoipListener, OnZoomVoipHandleListener {


    lateinit var callParams: CallParams
    lateinit var meetingInfo: MeetingInfo
    lateinit var workplusVoipMeetingId: String


    override fun createFragment(): Fragment {
        val zoomCallFragment = ZoomCallFragment()
        ZoomVoipManager.onZoomVoipHandleListener = this
        ZoomVoipManager.onControllerVoipListener = this
        ZoomVoipManager.onVoipStatusListener = zoomCallFragment

        return zoomCallFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!processData()) return

        //锁屏情况下需要优先显示接电话界面
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    private fun processData(): Boolean {


        if (null != ZoomVoipManager.currentVoipMeeting) {

            with(ZoomVoipManager.currentVoipMeeting!!){
                callParams = mCallParams
                meetingInfo = mMeetingInfo
                workplusVoipMeetingId = mWorkplusVoipMeetingId?: StringUtils.EMPTY
            }


        } else {
            AtworkToast.showResToast(R.string.error_happened)
            finish()
            return false
        }
        return true
    }

    private fun createMeeting() {
        ZoomVoipManager.createMeeting(context = AtworkApplicationLike.baseContext, meetingInfo = meetingInfo, memberList = callParams.callMemberList, onCreateAndQueryVoipMeetingListener = object : VoipManager.OnCreateAndQueryVoipMeetingListener {

            override fun onSuccess(responseJson: CreateOrQueryMeetingResponseJson) {
                workplusVoipMeetingId = responseJson.mResult.mMeetingId

                //单对单语音会话, 在对方 join 了后才自己加入
                if (MeetingInfo.Type.USER != meetingInfo.mType) {
                    joinMeeting(workplusVoipMeetingId)
                }


            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg)
                ZoomVoipManager.stopCall()
            }

        })
    }


    private fun joinMeeting(workplusVoipMeetingId: String) {
        ZoomVoipManager.joinMeeting(context = AtworkApplicationLike.baseContext, meetingInfo = meetingInfo, meetingId = workplusVoipMeetingId, onGetJoinTokenListener = object : VoipManager.OnGetJoinTokenListener {
            override fun onSuccess(token: String) {
                //会畅入会
                goMeeting(token)
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg)

                finish()
            }

        })
    }


    /**
     * 通知服务器离开会议
     */
    private fun leaveVoipMeeting() {
        VoipManager.leaveMeeting(this, null, VoipMeetingController.getInstance().currentVoipMeeting, workplusVoipMeetingId, AtworkApplicationLike.getLoginUserHandleInfo(this), ZoomHandleInfo(), object : VoipManager.OnHandleVoipMeetingListener {
            override fun onSuccess() {
                LogUtil.e("bizconf", "leave success")
            }

            override fun networkFail(errorCode: Int, errorMsg: String) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg)
                LogUtil.e("bizconf", "leave failed")



            }
        })

    }


    /**
     * 通知服务器拒绝电话
     */
    private fun rejectVoipMeeting() {
        if (!StringUtils.isEmpty(workplusVoipMeetingId)) {
            VoipManager.getInstance().rejectMeeting(this, null, VoipMeetingController.getInstance().currentVoipMeeting, workplusVoipMeetingId, ZoomHandleInfo(), object : VoipManager.OnHandleVoipMeetingListener {
                override fun onSuccess() {
                    LogUtil.e("VOIP", "reject success")

                }

                override fun networkFail(errorCode: Int, errorMsg: String) {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg)

                }
            })
        }
    }


    override fun onStartVoipMeeting() {
        if (UserType.Originator == callParams.mMySelf.userType) {
            if (!StringUtils.isEmpty(workplusVoipMeetingId)) { // 加入已存在的呼场景

                joinMeeting(workplusVoipMeetingId)

            } else {
                createMeeting()


            }
        } else if (UserType.Recipient == callParams.mMySelf.userType) {

            joinMeeting(workplusVoipMeetingId)

        }

    }

    override fun onInviteMember() {
    }

    override fun onStartCallSuccess() {
    }

    override fun onStartCallFailure() {
    }

    override fun onHideView() {
    }

    override fun onCancelCall() {
        rejectVoipMeeting()

    }

    override fun onFinishCall(memberCount: Int) {

        LogUtil.e("bizconf", "onFinishCall")

        leaveVoipMeeting()
    }

    override fun onTimeOut() {
    }

    override fun onParticipantEnter(participant: VoipMeetingMember?) {
    }

    override fun onParticipantLeave(participant: VoipMeetingMember?) {
    }

    override fun onAcceptCall() {
    }

    override fun onRejectCall() {
        rejectVoipMeeting()
    }

    override fun onInitVoipMeeting() {
        if (UserType.Originator == callParams.mMySelf.userType) {
            if (!StringUtils.isEmpty(workplusVoipMeetingId)) { // 加入已存在的呼场景

                joinMeeting(workplusVoipMeetingId)

            } else {
                createMeeting()


            }
        }
    }

    override fun goMeeting(tokenKey: String) {
        val handleMeetingInfo = HandleMeetingInfo()
        handleMeetingInfo.meetingUrl = tokenKey

        ZoomVoipManager.changeCallState(CallState.CallState_StartCall)

        if(tokenKey.contains("token")) {
            ZoomManager.startMeeting(this@ZoomCallActivity, handleMeetingInfo)
        } else {

            if(!StringUtils.isEmpty(handleMeetingInfo.meetingUrl)) {
                if(null == Uri.parse(handleMeetingInfo.meetingUrl).getQueryParameter("uname")) {
                    handleMeetingInfo.meetingUrl = UrlHandleHelper.addParam(handleMeetingInfo.meetingUrl, "uname", LoginUserInfo.getInstance().getLoginUserName(AtworkApplicationLike.baseContext))
                }
            }

            ZoomManager.joinMeeting(this@ZoomCallActivity, handleMeetingInfo)

        }

        ZoomVoipManager.changeCallState(CallState.CallState_Calling)


        finish()
    }

    companion object {

        @JvmStatic
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, ZoomCallActivity::class.java)
            return intent
        }
    }
}