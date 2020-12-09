package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

data class WorkbenchAdminAddResponse(
        @SerializedName("result")
        var id: Long = -1L

) : BasicResponseJSON()