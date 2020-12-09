package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.google.gson.annotations.SerializedName

data class WorkbenchQueryListResponse(

        @SerializedName("result")
        val workbenchQueryResult: WorkbenchQueryListResult?


) : BasicResponseJSON() {


    fun isLegal()  = workbenchQueryResult != null


}

data class WorkbenchQueryListResult (

    @SerializedName("total_count")
    val count: Int,

    @SerializedName("records")
    val records: List<WorkbenchData>
)