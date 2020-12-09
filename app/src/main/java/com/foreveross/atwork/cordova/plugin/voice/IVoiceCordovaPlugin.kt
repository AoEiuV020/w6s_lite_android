package com.foreveross.atwork.cordova.plugin.voice

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface

interface IVoiceCordovaPlugin {

    fun startTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext)

    fun cancelTalkingRecognize(cordova: CordovaInterface, callbackContext: CallbackContext)
}