package com.foreveross.atwork.infrastructure.model.workbench

import android.content.Context
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.utils.StringUtils

abstract class WorkbenchCommonTitleCard : WorkbenchCard() {
    var isSubCard: Boolean = false

    var headerShow: Boolean = false


//    get() {
//        if(!isSubCard) {
//            return field
//        }
//
//        if(!field) {
//            return false
//        }
//
//
//        headerButton.find { it.isLegal() }?.let {
//            return true
//        }
//
//
//
//        return false
//    }

    var headerButton: MutableList<WorkbenchCardHeaderButton> = arrayListOf()


    fun getTitle(context: Context): String {
        if(isSubCard) {
            return StringUtils.EMPTY
        }

        return getNameI18n(context)
    }

    fun headerShowInCommonTitleView(): Boolean {
        if(isSubCard) {
            return false
        }

        return headerShow
    }

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        val definitionData = workbenchCardDetailData.workbenchCardDisplayDefinitions

        if(null == definitionData) {
            return
        }

        definitionData.headerShow?.let {
            headerShow = it
        }


        definitionData.headerButton?.forEach {
            val button = WorkbenchCardHeaderButton()
            button.parse(it)

            headerButton.add(button)
        }



    }
}