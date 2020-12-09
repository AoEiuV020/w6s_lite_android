package com.foreveross.atwork.cordova.plugin

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.cordova.plugin.model.SharePreferenceHandle
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils
import com.foreveross.atwork.infrastructure.utils.StringUtils
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin

const val LEGAL_PREFIX = "cordova_app_"

const val ACTION_PUT_DATA = "putData"

const val ACTION_GET_DATA = "getData"

class SharePreferencePlugin: CordovaPlugin() {

    override fun execute(action: String, rawArgs: String, callbackContext: CallbackContext): Boolean {
        when(action) {
            ACTION_PUT_DATA -> {
                putData(rawArgs, callbackContext)

                return true
            }


            ACTION_GET_DATA -> {
                getData(rawArgs, callbackContext)
                return true
            }
        }

        return false
    }

    private fun putData(rawArgs: String, callbackContext: CallbackContext) {
        val handleInfo: SharePreferenceHandle? = NetGsonHelper.fromCordovaJson(rawArgs, SharePreferenceHandle::class.java)
        if (null == handleInfo) {
            callbackContext.error()
            return

        }

        if (!handleInfo.isLegal()) {
            callbackContext.error()
            return
        }


        PreferencesUtils.putString(AtworkApplicationLike.baseContext, getSpName(handleInfo), handleInfo.key, handleInfo.value)
    }

    private fun getSpName(handleInfo: SharePreferenceHandle) =
            LEGAL_PREFIX + handleInfo.appId

    private fun getData(rawArgs: String, callbackContext: CallbackContext) {
        val handleInfo: SharePreferenceHandle? = NetGsonHelper.fromCordovaJson(rawArgs, SharePreferenceHandle::class.java)
        if (null == handleInfo) {
            callbackContext.error()
            return

        }

        if (!handleInfo.isLegal()) {
            callbackContext.error()
            return
        }


        handleInfo.value = PreferencesUtils.getString(AtworkApplicationLike.baseContext, getSpName(handleInfo), handleInfo.key, StringUtils.EMPTY)
        callbackContext.success(handleInfo)

    }
}