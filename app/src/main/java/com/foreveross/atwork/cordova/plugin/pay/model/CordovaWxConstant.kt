package com.foreveross.atwork.cordova.plugin.pay.model

class CordovaWxConstant {

    companion object {
        const val ERROR_CODE_APP_NOT_INSTALLED = 3

        @JvmField
        val errorCodeInfos = mapOf(
                ERROR_CODE_APP_NOT_INSTALLED to "没有安装微信"
        )
    }
}