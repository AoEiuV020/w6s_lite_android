package com.foreveross.atwork.cordova.plugin.model

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class SharePreferenceHandle {

    @SerializedName("app_id")
    var appId: String? = null

    @SerializedName("key")
    var key: String? = null

    @SerializedName("value")
    var value: String? = null


    fun isLegal(): Boolean {
        if(StringUtils.isEmpty(appId)) {
            return false
        }

        if(StringUtils.isEmpty(key)) {
            return false
        }

        return true
    }

}