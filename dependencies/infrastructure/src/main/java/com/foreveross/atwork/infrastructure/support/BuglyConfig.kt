package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class BuglyConfig : BaseConfig() {
    @SerializedName("enabled")
    var enabled: Boolean = true

    @SerializedName("appId")
    var appId: String? = "900033769"

    @SerializedName("secret")
    var secret: String? = StringUtils.EMPTY


    override fun parse() {
        BeeWorks.getInstance().config.beeWorksBugly?.let {
            enabled = it.enabled
            appId = it.appId
            secret = it.secret
        }
    }
}