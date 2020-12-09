package com.foreveross.atwork.api.sdk.auth.model

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

/**
 *  create by reyzhang22 at 2019-10-16
 */
class LoginInitResp : BasicResponseJSON() {

    @SerializedName("result")
    var result: Result = Result()

    inner class Result {

        @SerializedName("secret")
        var secret : String = ""

        @SerializedName("expire_time")
        var expireTime : Long = -1
    }
}