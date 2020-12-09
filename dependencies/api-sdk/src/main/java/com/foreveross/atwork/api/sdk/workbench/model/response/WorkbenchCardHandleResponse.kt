package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

data class WorkbenchCardHandleResponse(
        @SerializedName("result")
        var widgetId: Long = -1L

) : BasicResponseJSON()