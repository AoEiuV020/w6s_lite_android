package com.foreveross.atwork.infrastructure.model.workbench

import android.net.Uri
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo
import com.foreveross.atwork.infrastructure.model.workbench.content.IWorkbenchCardContent
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.utils.StringUtils

abstract class WorkbenchCard(

        var id: Long = 0,

        var requestId: String = StringUtils.EMPTY,

        var domainId: String = StringUtils.EMPTY,

        var orgCode: String = StringUtils.EMPTY,

        var type: WorkbenchCardType = WorkbenchCardType.UNKNOWN,

        var name: String = StringUtils.EMPTY,

        var enName: String = StringUtils.EMPTY,

        var twName: String = StringUtils.EMPTY,

        var platforms: List<String> = emptyList(),

        var createTime: Long? = -1L,

        var sortOrder: Int = 0,

        var displayable: Boolean = true,

        var deletable: Boolean = true,

        var workbenchCardContent: IWorkbenchCardContent? = null,

        var callBackUrl: String? = null,

        var bannerHeight: String? = null,

        var adminDisplay: Boolean = false,

        var originalWorkbenchCardDetailData: WorkbenchCardDetailData? = null,

        var disable: Boolean = false


): I18nInfo(), Comparable<WorkbenchCard> {

    override fun compareTo(other: WorkbenchCard): Int {
        if(sortOrder != other.sortOrder) {
            return sortOrder.compareTo(other.sortOrder)
        }

        val comparedCreateTime = createTime ?: -1
        val comparedOtherCreateTime = other.createTime ?: -1



        return comparedOtherCreateTime.compareTo(comparedCreateTime)

    }


    override fun getStringName(): String? {
        return name
    }

    override fun getStringTwName(): String? {
        return twName
    }

    override fun getStringEnName(): String? {
        return enName
    }

    fun isContentLegal() = (null != workbenchCardContent)

    open fun isContentDataEmpty() = if (isContentLegal()) {
        workbenchCardContent!!.isContentDataEmpty()
    } else {
        false
    }


    fun <T: IWorkbenchCardContent>getCardContent(): T? {
        workbenchCardContent?.let {
            return it as T

        }

        return null
    }

    fun isCallbackUrlLegal(): Boolean {
        if (StringUtils.isEmpty(callBackUrl)) {
            return false
        }

        if(StringUtils.isEmpty(Uri.parse(callBackUrl).host)) {
            return false
        }

        return true
    }

    fun isCardLegal(): Boolean = platformMatch()

    fun platformMatch(): Boolean = platformMatch(platforms)

    open fun supportNotice(): Boolean = false

    open fun getNoticeDataUrlInfos(): Map<String, String>? = null

    open fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        with(this) {
            id = workbenchCardDetailData.id
            domainId = workbenchCardDetailData.domainId
            orgCode = workbenchCardDetailData.orgCode
            type = WorkbenchCardType.parse(workbenchCardDetailData.type)
            workbenchCardDetailData.name?.let { name = it }
            workbenchCardDetailData.enName?.let { enName = it }
            workbenchCardDetailData.twName?.let { twName = it }

            platforms = workbenchCardDetailData.platforms
            createTime = workbenchCardDetailData.createTime
            bannerHeight = workbenchCardDetailData.workbenchCardDisplayDefinitions?.bannerHeight
            sortOrder = workbenchDefinitionData.sortOrder
            displayable = workbenchDefinitionData.displayable
            deletable = workbenchDefinitionData.deletable
            generateCallBackUrl(workbenchCardDetailData.workbenchCardDisplayDefinitions?.callbackUrl)

            originalWorkbenchCardDetailData = workbenchCardDetailData
            disable = workbenchCardDetailData.disabled
        }

    }

    fun generateCallBackUrl(callBackUrl: String?) {
        this.callBackUrl = callBackUrl
        generateRequestId()
    }

    private fun generateRequestId() {
        this.requestId = "${this.id}_${this.callBackUrl?.hashCode()}"
    }


}