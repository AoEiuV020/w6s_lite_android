package com.foreveross.atwork.api.sdk.workbench.model.request

import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.google.gson.annotations.SerializedName

data class WorkbenchModifyWorkbenchDefinitionRequest(
        @SerializedName("definitions")
        val definitions: ArrayList<WorkbenchDefinitionData>
)