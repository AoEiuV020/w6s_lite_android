package com.foreveross.atwork.cordova.plugin.voice

import android.Manifest
import android.os.Bundle
import com.foreveross.atwork.cordova.plugin.model.CordovaBasicResponse
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.xunfei.*
import com.iflytek.cloud.*
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.PluginResult
import org.json.JSONObject


class VoiceXunfeiCordovaPlugin:  IVoiceCordovaPlugin {



    companion object {
        var speechRecognizer: SpeechRecognizer? = null

        fun release() {

            speechRecognizer?.let {
                destroyXfSpeechRecognizer(it)
                speechRecognizer = null
            }

        }
    }


    override fun startTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext) {

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.activity, arrayOf(Manifest.permission.RECORD_AUDIO), object : PermissionsResultAction() {

            override fun onGranted() {
                doStartTalkingRecognize(cordova, callbackContext)
            }

            override fun onDenied(permission: String) {
                AtworkUtil.popAuthSettingAlert(cordova.activity, permission)
            }
        })




    }

    private fun doStartTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext) {
        speechRecognizer?.let {
            cancelXfSpeechRecognizer(it)
        }

        val newXfSpeechRecognizerResult = if (null == speechRecognizer) {
            newXfSpeechRecognizer(cordova.activity, VoicePluginRecognizerListener(callbackContext))

        } else {
            startXfSpeechRecognizer(cordova.activity, speechRecognizer!!, VoicePluginRecognizerListener(callbackContext))
        }

        if (ErrorCode.SUCCESS != newXfSpeechRecognizerResult.errorCode) {
            callbackContext.error(CordovaBasicResponse(newXfSpeechRecognizerResult.errorCode))
            return
        }

        speechRecognizer = newXfSpeechRecognizerResult.speechRecognizer
    }


    override fun cancelTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext) {
        speechRecognizer?.let {
            cancelXfSpeechRecognizer(it)
        }
    }
}


class VoicePluginRecognizerListener(val callbackContext: CallbackContext) : RecognizerListener {


    override fun onVolumeChanged(volume: Int, data: ByteArray) {
        LogUtil.e(TAG, "返回音频数据：" + data.size)

    }

    override fun onResult(results: RecognizerResult, isLast: Boolean) {
        LogUtil.e(TAG, "onResult ->" + results.resultString)

        val result = JsonParser.parseIatResult(results.resultString)

        LogUtil.e(TAG, "onResult text ->$result")


        val talkingRecognizeResult = TalkingRecognizeResult(isLast)
        talkingRecognizeResult.code = 0
        talkingRecognizeResult.message = result

        val jsonObject = JSONObject(JsonUtil.toJson(talkingRecognizeResult))

        val pluginResult = PluginResult(PluginResult.Status.OK, jsonObject)
        pluginResult.keepCallback = true

        callbackContext.sendPluginResult(pluginResult)

    }

    override fun onBeginOfSpeech() {
        // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入

        LogUtil.e(TAG, "onBeginOfSpeech")
    }

    override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
    }

    override fun onEndOfSpeech() {
        // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        LogUtil.e(TAG, "onEndOfSpeech")
    }

    override fun onError(error: SpeechError) {
        LogUtil.e(TAG, "error -> $error.errorDescription   error_code ->   $error.errorCode")
        // Tips：
        // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
        // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
        if(10118 == error.errorCode) {

        } else {
            AtworkToast.showToast(error.toString())
        }


        callbackContext.error(CordovaBasicResponse(error.errorCode, error.errorDescription))
    }

}
