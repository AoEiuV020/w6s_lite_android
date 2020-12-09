package com.foreveross.atwork.cordova.plugin.voice

import com.foreveross.atwork.cordova.plugin.WorkPlusCordovaPlugin
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.support.VoiceTypeSdk
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.CordovaPlugin

val TAG: String = CordovaPlugin::class.java.name

const val ACTION_START_TALKING_RECOGNIZE = "startTalkingRecognize"

const val ACTION_CANCEL_TALKING_RECOGNIZE = "cancelTalkingRecognize"

class VoiceCordovaPlugin : WorkPlusCordovaPlugin(), IVoiceCordovaPlugin {

    private var voiceCordovaPlugin: IVoiceCordovaPlugin? = null

    init {
        voiceCordovaPlugin = when (AtworkConfig.VOICE_CONFIG.sdk) {
            VoiceTypeSdk.ALIYUN -> VoiceAliyunCordovaPlugin()
            VoiceTypeSdk.XUNFEI -> VoiceXunfeiCordovaPlugin()
            else -> null
        }
    }


    companion object {


        @JvmStatic
        fun release() {
            when (AtworkConfig.VOICE_CONFIG.sdk) {
                VoiceTypeSdk.ALIYUN -> VoiceAliyunCordovaPlugin.release()
                VoiceTypeSdk.XUNFEI -> VoiceXunfeiCordovaPlugin.release()
            }

        }

        @JvmStatic
        fun init() {
            when (AtworkConfig.VOICE_CONFIG.sdk) {
                VoiceTypeSdk.ALIYUN -> VoiceAliyunCordovaPlugin.init()
                VoiceTypeSdk.XUNFEI -> {
                }
            }
        }
    }


    override fun execute(action: String, rawArgs: String, callbackContext: CallbackContext): Boolean {
        if(!requestCordovaAuth())return false

        when (action) {
            ACTION_START_TALKING_RECOGNIZE -> {
                startTalkingRecognize(cordova, callbackContext)
                return true
            }

            ACTION_CANCEL_TALKING_RECOGNIZE -> {
                cancelTalkingRecognize(cordova, callbackContext)
                return true
            }
        }

        return false
    }

    override fun startTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext) {
        voiceCordovaPlugin?.startTalkingRecognize(cordova, callbackContext)

    }

    override fun cancelTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext) {
        voiceCordovaPlugin?.cancelTalkingRecognize(cordova, callbackContext)

    }
}