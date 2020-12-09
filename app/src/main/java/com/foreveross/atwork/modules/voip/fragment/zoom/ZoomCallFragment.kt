package com.foreveross.atwork.modules.voip.fragment.zoom

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.voip.*
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnControllerVoipListener
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnVoipStatusListener
import com.foreveross.atwork.modules.voip.support.zoom.interfaces.OnZoomVoipHandleListener
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.atwork.utils.AvatarHelper
import kotlinx.android.synthetic.main.fragment_call_zoom.*

class ZoomCallFragment: BackHandledFragment(), OnVoipStatusListener {

    private val handler = Handler()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_call_zoom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshBottomControlArea()
        registerListener()

        startCall()
    }

    override fun findViews(view: View?) {

    }

    override fun onBackPressed(): Boolean {
        return true
    }

    private fun isCurrentVoipMeetingValid(): Boolean {
        return ZoomVoipManager.isCurrentVoipMeetingValid()
    }


    /**
     * 处理超时 call 的时候的挂断处理(拨打方才发送"reject"通知)
     */
    fun autoFinishCall() {
        val myself = getMySelf() ?: return
        if (UserType.Originator == myself.userType) {
            getControllerVoipListener()?.onCancelCall()

            ZoomVoipManager.stopCall()


        } else if (UserType.Recipient == myself.userType) {
            if (null != getControllerVoipListener() && ZoomVoipManager.isGroupChat()) {
                //                getDelegate().onRejectCall();
            }

            ZoomVoipManager.stopCall()

        }

    }


    private fun startCall() {
        ZoomVoipManager.changeCallState(CallState.CallState_Init)
        getZoomHandleListener()?.onInitVoipMeeting()
    }

    private fun getControllerVoipListener(): OnControllerVoipListener? {
        return ZoomVoipManager.onControllerVoipListener
    }


    private fun getZoomHandleListener(): OnZoomVoipHandleListener? {
        return ZoomVoipManager.onZoomVoipHandleListener
    }

    private fun getMySelf(): VoipMeetingMember? {
        return ZoomVoipManager.currentVoipMeeting?.mCallSelf
    }

    private fun getCallState(): CallState {
        return ZoomVoipManager.callState
    }

    private fun registerListener() {
        rlHangupCall.setOnClickListener {
            finishCall()
        }

        rlPickupCall.setOnClickListener {
            acceptCallAndCheckPermission()
        }
    }


    private fun finishCall() {

        val callState = getCallState()
        if(CallState.CallState_Idle == callState || CallState.CallState_Init == callState) {
            if(UserType.Originator == getMySelf()?.userType) {
                getControllerVoipListener()?.onCancelCall()

            } else {
                getControllerVoipListener()?.onRejectCall()

            }

        } else {
            getControllerVoipListener()?.onFinishCall(-1)

        }

        ZoomVoipManager.stopCall()

        finish()
    }



    private fun acceptCallAndCheckPermission() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity, arrayOf(Manifest.permission.RECORD_AUDIO), object : PermissionsResultAction() {
            override fun onGranted() {
                acceptCall()
            }

            override fun onDenied(permission: String) {

                val alertDialog = AtworkUtil.getAuthSettingAlert(activity!!, permission)
                alertDialog.show()
            }
        })
    }


    /**
     * 接通电话
     */
    private fun acceptCall() {
        getControllerVoipListener()?.onStartVoipMeeting()
        getControllerVoipListener()?.onAcceptCall()
    }

    private fun refreshBottomControlArea(){
        if(UserType.Originator == getMySelf()?.userType) {
            rlPickupCall.visibility = View.GONE
        } else {
            rlPickupCall.visibility = View.VISIBLE

        }


    }

    private fun handleHangupTextView() {
        if (CallState.CallState_Init == getCallState()) {

            if (UserType.Originator == getMySelf()?.userType) {
                tvHangupCall.setText(R.string.cancel)

            } else {
                tvHangupCall.setText(R.string.reject)

            }

        }

    }

    /**
     * 根据状态更新, 处理交互, 包括文字提示的刷新以及 fragment 的消亡等
     */
    private fun handleCallStateChanged(callState: CallState) {
        if (!isCurrentVoipMeetingValid())
            return

        handler.run {

            when(callState) {
                CallState.CallState_Init -> {
                    onUsersProfileRefresh()
                    handleHangupTextView()
                    refreshPreCallStatusTip()

                }

                CallState.CallState_Ended -> {

                    handler.postDelayed({
                        if (isAdded) {
                            finish()
                        }

                    }, 1000)
                }
                else -> {}
            }
        }


    }

    private fun refreshPreCallStatusTip() {
        val userType = getMySelf()?.userType
        val voipType = ZoomVoipManager.currentVoipMeeting?.mVoipType

        if (UserType.Originator == userType) {
            setVoipPreCallStatusTip(R.string.voip_tip_wait_peer_accept)

        } else {
            if (VoipType.VIDEO == voipType) {
                setVoipPreCallStatusTip(R.string.voip_tip_invite_join_video_meeting)

            } else {
                setVoipPreCallStatusTip(R.string.voip_tip_invite_join_audio_meeting)

            }
        }
    }

    private fun setVoipPreCallStatusTip(contentResId: Int) {
        tvPreCallTip.setText(contentResId)
//        mTvCallingTip.setText(StringUtils.EMPTY)
    }

    override fun onUsersProfileRefresh() {
        if(isAdded && isCurrentVoipMeetingValid()) {

            if(ZoomVoipManager.isGroupChat()) {
                ZoomVoipManager.getInvitor()?.let {
                    this.tvUserName.text = it.mShowName
                    AvatarHelper.setUserInfoAvatar(it, this.ivAvatar)
                }

            } else {


                ZoomVoipManager.getPeer()?.let {
                    this.tvUserName.text = it.mShowName
                    AvatarHelper.setUserInfoAvatar(it, this.ivAvatar)
                }

            }



        }
    }

    override fun onTipToast(tip: String?) {
    }

    override fun onLoudSpeakerStatusChanged(bLoudSpeaker: Boolean) {
    }

    override fun onIsSpeakingChanged(strSpeakingNames: String?) {
    }

    override fun onVideoItemAdded(userId: String?) {
    }

    override fun onVideoItemDeleted(userId: String?) {
    }

    override fun onVideoItemAttachSurface(userId: String?): Any? {
        return null
    }

    override fun onVideoItemDetachSurface(userId: String?, videoSurface: Any?) {
    }

    override fun onVideoItemShowed(userId: String?, domainId: String?) {
    }

    override fun onVideoCallClosed() {
    }

    override fun onCallStateChanged(callState: CallState) {
        handleCallStateChanged(callState)

    }

    override fun onCallingTimeElpased(nSeconds: Long) {
    }

    override fun onVOIPQualityIsBad() {
    }

    override fun onPhoneCallResult(bSucceeded: Boolean) {
    }

    override fun onPhoneStateChanged(phoneState: PhoneState?) {
    }

}