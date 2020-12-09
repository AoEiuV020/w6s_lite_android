package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.google.gson.annotations.SerializedName

data class WorkbenchQueryResponse(

        @SerializedName("result")
        val workbenchQueryResult: WorkbenchData?


) : BasicResponseJSON() {


    fun isLegal()  = workbenchQueryResult != null

//    data class WorkbenchQueryResult(
//            @SerializedName("detail")
//            val workbenchData: WorkbenchData?
//    )
}