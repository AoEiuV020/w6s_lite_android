package com.foreveross.atwork.api.sdk.auth.model

import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.google.gson.annotations.SerializedName

/**
 *  create by reyzhang22 at 2019-10-16
 */
data class LoginInitRequest(
        @SerializedName("domain_id")
        val domainId: String = AtworkConfig.DOMAIN_ID,

        @SerializedName("device_id")
        val deviceId: String = AtworkConfig.getDeviceId()
)