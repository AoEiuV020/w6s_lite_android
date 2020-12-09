package com.foreveross.atwork.cordova.plugin

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin

abstract class WxOrQQPlugin: CordovaPlugin() {



    companion object {

        var currentCallbackContext: CallbackContext? = null
        var appId: String? = null




        fun release() {
            currentCallbackContext = null
            appId = null
        }
    }
}