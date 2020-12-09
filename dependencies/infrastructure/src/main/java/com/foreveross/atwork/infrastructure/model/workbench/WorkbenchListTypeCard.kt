package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData

class WorkbenchListTypeCard: WorkbenchCommonTitleCard() {
    var listCount: Int = 3


    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.listCount?.let {
            listCount = it
        }
    }
}