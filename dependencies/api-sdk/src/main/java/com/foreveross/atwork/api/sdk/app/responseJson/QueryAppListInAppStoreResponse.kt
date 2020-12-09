package com.foreveross.atwork.api.sdk.app.responseJson

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.app.App
import com.google.gson.annotations.SerializedName

class QueryAppListInAppStoreResponse(

        @SerializedName("result")
        val result: QueryAppListInAppStoreResult

) : BasicResponseJSON()

class QueryAppListInAppStoreResult(

        @SerializedName("total_count")
        val totalCount: Int,

        @SerializedName("access_list")
        val appList: List<App>
)