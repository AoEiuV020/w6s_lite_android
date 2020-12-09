package com.foreveross.atwork.infrastructure.model.workbench.data

import android.content.Context
import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo
import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.foreveross.atwork.infrastructure.model.workbench.platformMatch
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkbenchData(

        @SerializedName("id")
        val id: Long,

        @SerializedName("domain_id")
        val domainId: String,

        @SerializedName("org_code")
        val orgCode: String,

        @SerializedName("name")
        var name: String?,

        @SerializedName("en_name")
        var enName: String?,

        @SerializedName("tw_name")
        var twName: String?,

        @SerializedName("remarks")
        var remarks: String?,

        @SerializedName("disabled")
        var disabled: Boolean?,

        @SerializedName("platforms")
        val platforms: List<String>,

        @SerializedName("definitions")
        var workbenchCards: List<WorkbenchDefinitionData> = emptyList(),

        @SerializedName("create_time")
        val createTime: Long,

        @SerializedName("widgets")
        var workbenchCardDetailDataList: List<WorkbenchCardDetailData> = emptyList(),

        @SerializedName("advertisements")
        var advertisements: ArrayList<AdvertisementConfig> = ArrayList(),

        @SerializedName("scope_record")
        var scopeRecord: WorkbenchScopeRecord? = null



): I18nInfo(), Parcelable {

    override fun getStringName(): String? {
        return name
    }

    override fun getStringEnName(): String? {
        return enName
    }

    override fun getStringTwName(): String? {
        return twName
    }

    fun getScopeList(context: Context): List<Scope> {
        val scopeDataList = ArrayList<Scope>()
        scopeRecord?.orgs
                ?.map {it.transfer(context)}
                ?.let { scopeDataList.addAll(it) }

        scopeRecord?.employees
                ?.mapNotNull { it.transfer(scopeDataList) }
                ?.let { scopeDataList.addAll(it) }

        return scopeDataList
    }

    fun isLegal(): Boolean {

        if(!platformMatch(platforms)) {
            return false
        }

        if (ListUtil.isEmpty(workbenchCards)) {
            return false
        }

        val cardsMatching =  workbenchCards.filter { platformMatch(it.platforms) }

        if (ListUtil.isEmpty(cardsMatching)) {
            return false
        }

        return true
    }


    fun findWorkbenchCardDetailData(cardId: Long): WorkbenchCardDetailData? =  workbenchCardDetailDataList.find { cardId == it.id }



}