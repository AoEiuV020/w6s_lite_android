package com.foreveross.atwork.api.sdk.faceBio.aliyun.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName


data class GetAliyunFaceBioTokenResponse(

        @SerializedName("result")
        var result: Result?

): BasicResponseJSON() {


    data class Result (
        @SerializedName("token")
        var token: String?,

        @SerializedName("ticket_id")
        var ticketId: String?,

        @SerializedName("duration_seconds")
        var durationSeconds: Long?
    )
}