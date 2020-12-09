package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData

class WorkbenchAppContainer1Card: WorkbenchCommonTitleCard() {

    var appContainer: List<AppBundles> = emptyList()

    var listCount = 3

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.listCount?.let { listCount = it }

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.appContainer?.map { it.transfer() }?.let {
            appContainer = it
        }

        headerShow = true

    }
}