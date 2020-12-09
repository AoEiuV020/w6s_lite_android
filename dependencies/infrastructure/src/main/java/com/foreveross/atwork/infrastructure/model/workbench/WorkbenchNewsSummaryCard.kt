package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData

class WorkbenchNewsSummaryCard : WorkbenchCard() {
    var listCount: Int = 3
    var singleIcon: String? = null

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.listCount?.let {
            listCount = it
        }
        singleIcon = workbenchCardDetailData.workbenchCardDisplayDefinitions?.singleIcon
    }
}