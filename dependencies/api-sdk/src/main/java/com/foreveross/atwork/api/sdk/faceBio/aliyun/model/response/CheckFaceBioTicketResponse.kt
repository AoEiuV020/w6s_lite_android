package com.foreveross.atwork.api.sdk.faceBio.aliyun.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName


data class CheckFaceBioTicketResponse(

        @SerializedName("result")
        var result: Result?

): BasicResponseJSON() {


    data class Result (
        @SerializedName("user_id")
        var userId: String?,

        @SerializedName("ticket_id")
        var ticketId: String?,

        @SerializedName("name")
        var name: String?
    )
}