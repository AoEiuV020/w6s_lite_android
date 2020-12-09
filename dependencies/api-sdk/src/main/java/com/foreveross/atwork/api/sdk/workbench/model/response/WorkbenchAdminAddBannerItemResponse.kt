package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class WorkbenchAdminAddBannerItemResponse
(
        @SerializedName("result")
        val result: WorkbenchAdminAddBannerItemResult

) : BasicResponseJSON()


class WorkbenchAdminAddBannerItemResult(

        @SerializedName("id")
        val id: String,

        @SerializedName("domain_id")
        val domainId: String
)