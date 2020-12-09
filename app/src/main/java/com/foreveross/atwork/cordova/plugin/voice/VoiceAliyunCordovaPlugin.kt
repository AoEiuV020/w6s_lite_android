package com.foreveross.atwork.cordova.plugin.voice

import android.Manifest
import com.alibaba.idst.util.NlsClient
import com.alibaba.idst.util.SpeechRecognizerWithRecorder
import com.alibaba.idst.util.SpeechRecognizerWithRecorderCallback
import com.foreverht.voice.aliyun.*
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.cordova.plugin.model.CordovaBasicResponse
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.utils.CommonUtil
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.PluginResult
import org.json.JSONObject


class VoiceAliyunCordovaPlugin:  IVoiceCordovaPlugin {

    companion object {
        var speechRecognizer: SpeechRecognizerWithRecorder? = null
        var nlsClient: NlsClient? = null

        @JvmStatic
        fun release() {

            destroyAliyunSpeechRecognizer(nlsClient, speechRecognizer)
            nlsClient = null
            speechRecognizer = null

        }

        @JvmStatic
        fun init() {
            nlsClient = newAliyunNlsClient()
            checkAccessToken()
        }
    }


    override fun startTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext) {

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.activity, arrayOf(Manifest.permission.RECORD_AUDIO), object : PermissionsResultAction() {

            override fun onGranted() {
                doStartTalkingRecognize(callbackContext)
            }

            override fun onDenied(permission: String) {
                AtworkUtil.popAuthSettingAlert(cordova.activity, permission)
            }
        })




    }

    private fun doStartTalkingRecognize(callbackContext: CallbackContext) {
        newAliyunSpeechRecognizer(nlsClient, VoiceAliyunRecognizerListener(callbackContext)) {
            nlsClient = it.client
            speechRecognizer = it.speechRecognizer

            val result = startAliyunSpeechRecognizer(it.speechRecognizer)
            if(0 != result) {
                callbackContext.error(CordovaBasicResponse(result, "start failed"))

            }
        }
    }


    override fun cancelTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext) {
        cancelAliyunSpeechRecognizer(speechRecognizer)
    }
}

class VoiceAliyunRecognizerListener(val callbackContext: CallbackContext) : SpeechRecognizerWithRecorderCallback {
    override fun onRecognizedResultChanged(message: String, code: Int) {
        LogUtil.e(com.foreveross.atwork.modules.search.component.TAG, "onRecognizedResultChanged ->  message: $message    code: $code")
        onTalking(message, isLast = false)
    }


    override fun onVoiceVolume(i: Int) {
        LogUtil.e(com.foreveross.atwork.modules.search.component.TAG, "onVoiceVolume ->  i: $i")

    }

    override fun onRecognizedCompleted(message: String, code: Int) {
        LogUtil.e(com.foreveross.atwork.modules.search.component.TAG, "onRecognizedCompleted ->  message: $message    code: $code")

        onTalking(message, isLast = true)


    }

    private fun onTalking(message: String, isLast: Boolean) {
        if (!StringUtils.isEmpty(message)) {
            val jsonObject = JSONObject(message)
            if (jsonObject.has("payload")) {
                val result = jsonObject.getJSONObject("payload").getString("result")
                AtworkApplicationLike.runOnMainThread {

                    val talkingRecognizeResult = TalkingRecognizeResult(isLast)
                    talkingRecognizeResult.code = 0
                    talkingRecognizeResult.message = result

                    val jsonObject = JSONObject(JsonUtil.toJson(talkingRecognizeResult))

                    val pluginResult = PluginResult(PluginResult.Status.OK, jsonObject)
                    pluginResult.keepCallback = true

                    callbackContext.sendPluginResult(pluginResult)
                }
            }


        }
    }

    override fun onTaskFailed(message: String, code: Int) {
        LogUtil.e(com.foreveross.atwork.modules.search.component.TAG, "onTaskFailed ->  message: $message    code: $code")

        if(403 == code) {
            refreshAccessToken()
        }

        AtworkApplicationLike.runOnMainThread {

            if(!CommonUtil.isFastClick(1000)) {
                AtworkToast.showToast( "${AtworkApplicationLike.getResourceString(R.string.network_not_avaluable)} code: $code")
            }

            callbackContext.error(CordovaBasicResponse(code, message))

        }
    }

    override fun onChannelClosed(message: String, code: Int) {
        LogUtil.e(com.foreveross.atwork.modules.search.component.TAG, "onChannelClosed ->  message: $message    code: $code")
        AtworkApplicationLike.runOnMainThread {

        }

    }

    override fun onVoiceData(data: ByteArray, i: Int) {
        LogUtil.e(com.foreveross.atwork.modules.search.component.TAG, "onVoiceData ->  i: $i")

    }

    override fun onRecognizedStarted(message: String, code: Int) {
        LogUtil.e(com.foreveross.atwork.modules.search.component.TAG, "onRecognizedStarted ->  message: $message    code: $code")

        AtworkApplicationLike.runOnMainThread {
        }
    }

}



