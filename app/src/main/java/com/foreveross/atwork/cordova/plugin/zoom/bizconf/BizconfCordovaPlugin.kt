package com.foreveross.atwork.cordova.plugin.zoom.bizconf

import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.cordova.plugin.WorkPlusCordovaPlugin
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo
import org.apache.cordova.CallbackContext


const val ACTION_START_MEETING = "startMeeting"

const val ACTION_JOIN_MEETING = "joinMeeting"

class BizconfCordovaPlugin: WorkPlusCordovaPlugin() {

    override fun execute(action: String?, rawArgs: String?, callbackContext: CallbackContext): Boolean {
        if(!requestCordovaAuth())return false;

        when(action) {
            ACTION_START_MEETING -> {
                startMeeting(rawArgs, callbackContext)

                return true
            }

            ACTION_JOIN_MEETING -> {
                joinMeeting(rawArgs, callbackContext)
                return true
            }
        }

        return false
    }

    private fun startMeeting(rawArgs: String?, callbackContext: CallbackContext) {
        val handleMeetingInfo: HandleMeetingInfo? = NetGsonHelper.fromCordovaJson(rawArgs, HandleMeetingInfo::class.java)
        if (null == handleMeetingInfo) {
            callbackContext.error()
            return
        }

        ZoomManager.startMeeting(cordova.activity, handleMeetingInfo)
    }

    private fun joinMeeting(rawArgs: String?, callbackContext: CallbackContext) {
        val handleMeetingInfo: HandleMeetingInfo? = NetGsonHelper.fromCordovaJson(rawArgs, HandleMeetingInfo::class.java)
        if (null == handleMeetingInfo) {
            callbackContext.error()
            return
        }

        ZoomManager.joinMeeting(cordova.activity, handleMeetingInfo)

    }

}