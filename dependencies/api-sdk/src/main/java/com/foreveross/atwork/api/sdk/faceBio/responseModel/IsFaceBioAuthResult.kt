package com.foreveross.atwork.api.sdk.faceBio.responseModel

import android.widget.BaseAdapter
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class IsFaceBioAuthResult: BasicResponseJSON() {

    @SerializedName("result")
    lateinit var result: Result

    inner class Result {

        @SerializedName("enabled")
        var enabled = false

        @SerializedName("provider")
        var provider = ""

        @SerializedName("biological_auth_enabled")
        var biologicalAuthEnable = true

        @SerializedName("ticket_id")
        var ticket = ""
    }
}