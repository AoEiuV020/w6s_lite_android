package com.foreveross.atwork.api.sdk.message.model

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class QueryMessageHistoryResponse(

        @SerializedName("result")
        var result: QueryMessageHistoryResult? = null


) : BasicResponseJSON()

class QueryMessageHistoryResult (
        @SerializedName("total_count")
        var totalCount: Int,

        @SerializedName("records")
        var records: List<Any>
)