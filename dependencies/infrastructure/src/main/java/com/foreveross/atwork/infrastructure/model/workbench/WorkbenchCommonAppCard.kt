package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData

class WorkbenchCommonAppCard: WorkbenchCommonTitleCard() {

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        headerShow = true
    }
}