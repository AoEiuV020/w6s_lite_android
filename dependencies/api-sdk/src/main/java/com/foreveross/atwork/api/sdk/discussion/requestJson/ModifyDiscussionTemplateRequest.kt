package com.foreveross.atwork.api.sdk.discussion.requestJson

import com.google.gson.annotations.SerializedName

data class ModifyDiscussionTemplateRequest(
        @SerializedName("label")
        var label: String
)