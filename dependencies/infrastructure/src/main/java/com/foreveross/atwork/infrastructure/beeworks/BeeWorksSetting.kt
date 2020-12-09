package com.foreveross.atwork.infrastructure.beeworks

import com.google.gson.annotations.SerializedName

class BeeWorksSetting {

    @SerializedName("faceBio")
    var  faceBioSetting: FaceBioSetting = FaceBioSetting()

    @SerializedName("httpDns")
    var httpDnsSetting: HttpDnsSetting = HttpDnsSetting()


    class FaceBioSetting {
        @SerializedName("enable")
        var enable: Boolean = true

        @SerializedName("faceBioUpdateFilm")
        var faceBioUpdateFile: Boolean = true

        @SerializedName("faceBioAuth")
        var faceBioAuth: Boolean = true
    }

    class HttpDnsSetting {

        @SerializedName("enable")
        var enable: Boolean = true

        @SerializedName("account_id")
        var accountId: String = ""

        @SerializedName("secret")
        var secret: String = ""
    }
}