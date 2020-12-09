package com.foreveross.atwork.api.sdk.auth.model

import com.google.gson.annotations.SerializedName

class LoginWithMobileRequest (

        @SerializedName("client_principal")
        var clientPrincipal:  String? = null,

        @SerializedName("ip")
        var ip: String? = null,

        @SerializedName("user_device_authenticated")
        var userDeviceAuthenticated: Boolean = true

): LoginTokenJSON() {

    init {
        grantType = "security_code"
    }
}