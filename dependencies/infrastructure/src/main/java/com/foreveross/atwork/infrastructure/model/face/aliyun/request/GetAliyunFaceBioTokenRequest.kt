package com.foreveross.atwork.infrastructure.model.face.aliyun.request

import com.google.gson.annotations.SerializedName

open class GetAliyunFaceBioTokenRequest(

        @SerializedName("domain_id")
        var domainId: String,

        @SerializedName("user_id")
        var UserId: String?,

        @SerializedName("ticket_id")
        var ticketId: String?
)