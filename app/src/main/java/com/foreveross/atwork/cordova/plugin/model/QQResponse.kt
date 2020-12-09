package com.foreveross.atwork.cordova.plugin.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class QQResponse(

        @SerializedName("result")
        val result: JSONObject

) : CordovaBasicResponse()