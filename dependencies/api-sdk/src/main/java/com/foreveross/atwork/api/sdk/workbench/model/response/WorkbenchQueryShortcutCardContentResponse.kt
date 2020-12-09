package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.google.gson.annotations.SerializedName

data class WorkbenchQueryShortcutCardContentResponse(

        @SerializedName("result")
        val result: WorkbenchQueryShortcutCardContentResponseResult?

) : BasicResponseJSON() {


    data class WorkbenchQueryShortcutCardContentResponseResult(

            @SerializedName("items")
            val items: List<WorkbenchShortcutCardItem>
    )
}


