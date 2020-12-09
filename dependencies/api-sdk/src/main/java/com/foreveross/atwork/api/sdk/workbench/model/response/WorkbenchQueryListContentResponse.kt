package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.google.gson.annotations.SerializedName

data class WorkbenchQueryListContentResponse(

        @SerializedName("result")
        val result: WorkbenchQueryListContentResponseResult?

) : BasicResponseJSON() {


    data class WorkbenchQueryListContentResponseResult(

            @SerializedName("items")
            val items: List<WorkbenchListItem>
    )
}


