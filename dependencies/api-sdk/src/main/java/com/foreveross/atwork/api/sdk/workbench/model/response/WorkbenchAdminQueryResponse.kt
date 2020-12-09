package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchScopeRecord
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

data class WorkbenchAdminQueryResponse (

    @SerializedName("result")
    val result: WorkbenchAdminQueryResult?
): BasicResponseJSON()


data class WorkbenchAdminQueryResult(
        @SerializedName("record")
        val record: WorkbenchAdminQueryRecord?,

        @SerializedName("scope_records")
        val scopes: List<WorkbenchScopeRecord>?,

        @SerializedName("advertisements")
        val advertisements: List<AdvertisementConfig>?
) {
    fun transferWorkbenchData(orgCode: String): WorkbenchData? {

        if(null == record) {
            return null
        }

        val resultView = record.view?: return null

        if(StringUtils.isEmpty(resultView.orgCode)) {
            resultView.orgCode = orgCode
        }

        val workbenchData = WorkbenchData(
                id = resultView.id,
                domainId = resultView.domainId,
                orgCode = resultView.orgCode,
                name = resultView.name,
                enName = resultView.enName,
                twName = resultView.twName,
                remarks = resultView.remarks,
                disabled = resultView.disabled,
                platforms = resultView.platforms,
                workbenchCards = resultView.workbenchCards,
                createTime = resultView.createTime
        )


        record.widgets?.let { detailData ->

            detailData.forEach {detailDataItem->
                if(StringUtils.isEmpty(detailDataItem.orgCode)) {
                    detailDataItem.orgCode = orgCode
                }
            }

            workbenchData.workbenchCardDetailDataList = detailData
        }

        scopes?.getOrNull(0)?.let { scopeData->
            workbenchData.scopeRecord = scopeData
        }

        advertisements?.let { workbenchData.advertisements = ArrayList(it) }

        return workbenchData

    }

}




data class WorkbenchAdminQueryRecord (
    @SerializedName("view")
    val view: WorkbenchAdminQueryResultView?,

    @SerializedName("widgets")
    val widgets: List<WorkbenchCardDetailData>?
)



data class WorkbenchAdminQueryResultView (
        @SerializedName("id")
        val id: Long,

        @SerializedName("domain_id")
        val domainId: String,

        @SerializedName("org_code")
        var orgCode: String,

        @SerializedName("name")
        val name: String?,

        @SerializedName("en_name")
        val enName: String?,

        @SerializedName("tw_name")
        val twName: String?,

        @SerializedName("remarks")
        val remarks: String?,

        @SerializedName("disabled")
        val disabled: Boolean?,

        @SerializedName("platforms")
        val platforms: List<String>,

        @SerializedName("create_time")
        val createTime: Long,

        @SerializedName("definitions")
        var workbenchCards: List<WorkbenchDefinitionData> = emptyList()
)