package com.foreveross.atwork.api.sdk.message.model

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName
import com.w6s.module.MessageTags

class QueryMessageTagResponse(
        @SerializedName("result")
        var result: QueryMessageTagResult

): BasicResponseJSON()

class QueryMessageTagResult(
        @SerializedName("records")
        var records: List<MessageTags>
)