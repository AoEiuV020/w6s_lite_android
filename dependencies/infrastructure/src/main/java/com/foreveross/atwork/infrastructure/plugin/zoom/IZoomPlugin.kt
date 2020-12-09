package com.foreveross.atwork.infrastructure.plugin.zoom

import android.content.Context
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IZoomPlugin: WorkplusPlugin {

    fun init(context: Context, appKey: String, appSecret: String, domain: String)

    fun startMeeting(context: Context, handleMeetingInfo: HandleMeetingInfo)

    fun joinMeeting(context: Context, handleMeetingInfo: HandleMeetingInfo)

    fun isInitialized(context: Context): Boolean

    fun addMeetingFinishedListener(context: Context, listenerProxy: IZoomMeetingFinishedListenerProxy)

    fun addZoomConfMeetingServiceListener(context: Context, listenerProxy: IZoomConfMeetingServiceListenerProxy)

    fun setMuteMyMicrophoneWhenJoinMeeting(value: Boolean)

    fun setTurnOffMyVideoWhenJoinMeeting(value: Boolean)

}