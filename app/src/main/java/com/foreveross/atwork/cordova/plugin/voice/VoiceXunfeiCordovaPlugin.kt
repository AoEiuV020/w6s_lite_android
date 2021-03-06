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
        LogUtil.e(TAG, "?????????????????????" + data.size)

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
        // ??????????????????sdk??????????????????????????????????????????????????????????????????

        LogUtil.e(TAG, "onBeginOfSpeech")
    }

    override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
    }

    override fun onEndOfSpeech() {
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????
        LogUtil.e(TAG, "onEndOfSpeech")
    }

    override fun onError(error: SpeechError) {
        LogUtil.e(TAG, "error -> $error.errorDescription   error_code ->   $error.errorCode")
        // Tips???
        // ????????????10118(???????????????)????????????????????????????????????????????????????????????????????????????????????
        // ????????????????????????????????????????????????????????????????????????????????????
        if(10118 == error.errorCode) {

        } else {
            AtworkToast.showToast(error.toString())
        }


        callbackContext.error(CordovaBasicResponse(error.errorCode, error.errorDescription))
    }

}
