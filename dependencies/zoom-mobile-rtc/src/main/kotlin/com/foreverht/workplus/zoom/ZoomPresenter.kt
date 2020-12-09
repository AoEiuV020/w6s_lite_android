package com.foreverht.workplus.zoom

import android.content.Context
import android.net.Uri
import com.foreveross.atwork.infrastructure.Presenter.BasePresenter
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin
import com.foreveross.atwork.infrastructure.plugin.zoom.IZoomConfMeetingServiceListenerProxy
import com.foreveross.atwork.infrastructure.plugin.zoom.IZoomMeetingFinishedListenerProxy
import com.foreveross.atwork.infrastructure.plugin.zoom.IZoomPlugin
import com.foreveross.atwork.infrastructure.plugin.zoom.ZoomMeetingStatusProxy
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.Logger
import com.foreveross.atwork.infrastructure.utils.StringUtils
import us.zoom.sdk.*


private const val TAG = "zoom"

class ZoomPresenter : BasePresenter(), IZoomPlugin {

    private var zoomMeetingFinishedListenerProxy: IZoomMeetingFinishedListenerProxy? = null

    private var zoomConfMeetingServiceListenerProxy: IZoomConfMeetingServiceListenerProxy? = null

    override fun getWorkplusPlugin(): MutableMap<Class<out WorkplusPlugin>, WorkplusPlugin> {
        val map = HashMap<Class<out WorkplusPlugin>, WorkplusPlugin>()
        map[IZoomPlugin::class.java] = this
        return map
    }


    override fun init(context: Context, appKey: String, appSecret: String, domain: String) {

        if(ZoomSDK.getInstance().isInitialized){
            ZoomSDK.getInstance().logoutZoom()
        }
        val zoomSDKInitParams = ZoomSDKInitParams().apply {
            this.appKey = appKey
            this.appSecret = appSecret
            this.domain = domain
        }

        LogUtil.e("zoom appkey: ${zoomSDKInitParams.appKey}   appSecret:  ${zoomSDKInitParams.appSecret}   domain: ${zoomSDKInitParams.domain} " )
        LogUtil.e("zoom version ${ZoomSDK.getInstance().getVersion(context)}")

        ZoomSDK.getInstance().initialize(context, object : ZoomSDKInitializeListener {
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCodep: Int) {

                Logger.e(TAG, "zoom errorCode : $errorCode,   internalErrorCode: $internalErrorCodep")


                ZoomSDK.getInstance().meetingService?.addListener { meetingStatus, errorCode, internalErrorCode->
                    val bizconfMeetingStatusProxy = when (meetingStatus) {
                        MeetingStatus.MEETING_STATUS_IDLE -> ZoomMeetingStatusProxy.MEETING_STATUS_IDLE
                        MeetingStatus.MEETING_STATUS_CONNECTING -> ZoomMeetingStatusProxy.MEETING_STATUS_CONNECTING
                        MeetingStatus.MEETING_STATUS_WAITINGFORHOST -> ZoomMeetingStatusProxy.MEETING_STATUS_WAITINGFORHOST
                        MeetingStatus.MEETING_STATUS_INMEETING -> ZoomMeetingStatusProxy.MEETING_STATUS_INMEETING
                        MeetingStatus.MEETING_STATUS_DISCONNECTING -> ZoomMeetingStatusProxy.MEETING_STATUS_DISCONNECTING
                        MeetingStatus.MEETING_STATUS_RECONNECTING -> ZoomMeetingStatusProxy.MEETING_STATUS_RECONNECTING
                        MeetingStatus.MEETING_STATUS_FAILED -> ZoomMeetingStatusProxy.MEETING_STATUS_FAILED
                        MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM -> ZoomMeetingStatusProxy.MEETING_STATUS_IN_WAITING_ROOM
                        MeetingStatus.MEETING_STATUS_WEBINAR_PROMOTE -> ZoomMeetingStatusProxy.MEETING_STATUS_WEBINAR_PROMOTE
                        MeetingStatus.MEETING_STATUS_WEBINAR_DEPROMOTE -> ZoomMeetingStatusProxy.MEETING_STATUS_WEBINAR_DEPROMOTE
                        else -> ZoomMeetingStatusProxy.MEETING_STATUS_UNKNOWN
                    }

                    zoomConfMeetingServiceListenerProxy?.onMeetingStatusChanged(bizconfMeetingStatusProxy, errorCode, internalErrorCode)

                    when(meetingStatus) {
                        MeetingStatus.MEETING_STATUS_IDLE -> zoomMeetingFinishedListenerProxy?.onMeetingFinished()
                        MeetingStatus.MEETING_STATUS_INMEETING -> zoomMeetingFinishedListenerProxy?.inMeetingStatus()
                        else -> {}
                    }
                }


            }

            override fun onZoomAuthIdentityExpired() {
                Logger.e(TAG, "zoom onZoomAuthIdentityExpired~~~")

            }

        }, zoomSDKInitParams)




    }

    override fun addMeetingFinishedListener(context: Context, listenerProxy: IZoomMeetingFinishedListenerProxy) {
        this.zoomMeetingFinishedListenerProxy = listenerProxy
    }


    override fun addZoomConfMeetingServiceListener(context: Context, listenerProxy: IZoomConfMeetingServiceListenerProxy) {
        this.zoomConfMeetingServiceListenerProxy = listenerProxy
    }

    override fun setMuteMyMicrophoneWhenJoinMeeting(value: Boolean) {
        ZoomSDK.getInstance().meetingSettingsHelper?.setMuteMyMicrophoneWhenJoinMeeting(value)
    }

    override fun setTurnOffMyVideoWhenJoinMeeting(value: Boolean) {
        ZoomSDK.getInstance().meetingSettingsHelper?.setTurnOffMyVideoWhenJoinMeeting(value)

    }

    override fun startMeeting(context: Context, handleMeetingInfo: HandleMeetingInfo) {
        if(!StringUtils.isEmpty(handleMeetingInfo.meetingUrl)) {
            val meetingUri = Uri.parse(handleMeetingInfo.meetingUrl)

            val startMeetingParams = StartMeetingParamsWithoutLogin()
            startMeetingParams.meetingNo = meetingUri.getQueryParameter("confno")
            startMeetingParams.userId = meetingUri.getQueryParameter("uid")
            startMeetingParams.displayName = meetingUri.getQueryParameter("uname")
            startMeetingParams.zoomToken = meetingUri.getQueryParameter("token")
            startMeetingParams.zoomAccessToken = meetingUri.getQueryParameter("zak")
            startMeetingParams.userType = MeetingService.USER_TYPE_ZOOM

            val result = ZoomSDK.getInstance().meetingService.startMeetingWithParams(context, startMeetingParams)

            LogUtil.e("result: $result    meetingNo: ${startMeetingParams.meetingNo}   userId: ${startMeetingParams.userId} ")
            LogUtil.e("result url: ${handleMeetingInfo.meetingUrl}")


            return
        }


        val startMeetingParams = StartMeetingParamsWithoutLogin()
        startMeetingParams.meetingNo = handleMeetingInfo.meetingId
        startMeetingParams.displayName = handleMeetingInfo.displayName
        startMeetingParams.userId = handleMeetingInfo.userId
        startMeetingParams.zoomToken = handleMeetingInfo.token
        startMeetingParams.zoomAccessToken = handleMeetingInfo.zak
        startMeetingParams.userType = MeetingService.USER_TYPE_ZOOM

        val result = ZoomSDK.getInstance().meetingService.startMeetingWithParams(context, startMeetingParams)
        LogUtil.e("result: $result")

    }

    override fun joinMeeting(context: Context, handleMeetingInfo: HandleMeetingInfo) {
        if(!StringUtils.isEmpty(handleMeetingInfo.meetingUrl)) {
            val meetingUri = Uri.parse(handleMeetingInfo.meetingUrl)

            val joinMeetingParams = JoinMeetingParams()
            joinMeetingParams.meetingNo = meetingUri.getQueryParameter("confno")
            joinMeetingParams.displayName = meetingUri.getQueryParameter("uname")


            val result = ZoomSDK.getInstance().meetingService.joinMeetingWithParams(context, joinMeetingParams)

            LogUtil.e("result: $result    meetingNo: ${joinMeetingParams.meetingNo}   userId: ${joinMeetingParams.displayName} ")
            LogUtil.e("result url: ${handleMeetingInfo.meetingUrl}")

            return
        }


        val joinMeetingParams = JoinMeetingParams()
        joinMeetingParams.meetingNo = handleMeetingInfo.meetingId
        joinMeetingParams.displayName = handleMeetingInfo.displayName

        val result = ZoomSDK.getInstance().meetingService?.joinMeetingWithParams(context, joinMeetingParams)

        LogUtil.e("result: $result    meetingNo: ${joinMeetingParams.meetingNo}   userId: ${joinMeetingParams.displayName} ")
    }



    override fun isInitialized(context: Context): Boolean {
       return ZoomSDK.getInstance().isInitialized
    }

}