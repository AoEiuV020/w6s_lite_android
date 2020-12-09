package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData

class WorkbenchCustomCard: WorkbenchCard() {

    var linkUrl: String? = null

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.linkUrl?.let {
            linkUrl = it
        }
    }



}