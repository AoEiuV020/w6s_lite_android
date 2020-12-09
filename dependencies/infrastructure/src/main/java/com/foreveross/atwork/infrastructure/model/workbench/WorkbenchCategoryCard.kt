package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData

class WorkbenchCategoryCard : WorkbenchCard() {

    var subModuleCount: Int = 0
    var subModules = arrayListOf<WorkbenchCardSubModule>()
    var subCards = arrayListOf<WorkbenchCard>()

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        val definitionData = workbenchCardDetailData.workbenchCardDisplayDefinitions

        if (null == definitionData) {
            return
        }

        definitionData.subModuleCount?.let {
            this.subModuleCount = it
        }

        definitionData.subModule?.forEach { subModuleData ->
            subModules.add(WorkbenchCardSubModule().apply { parse(subModuleData) })
        }


    }



}


enum class WorkbenchCardSubModuleType {

    TEXT,

    ICON;

    companion object {

        fun parse(typeStr: String?): WorkbenchCardSubModuleType = when (typeStr?.toUpperCase()) {
            "TEXT_WIDGET" -> WorkbenchCardSubModuleType.TEXT
            "ICON_TEXT_WIDGET" -> WorkbenchCardSubModuleType.ICON
            else -> WorkbenchCardSubModuleType.TEXT
        }


    }
}