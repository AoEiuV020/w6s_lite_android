package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData

class WorkbenchAppContainer0Card: WorkbenchCommonTitleCard() {

    var appContainer: List<AppBundles> = emptyList()

    var gridType: WorkbenchGridType = WorkbenchGridType.TYPE_1_4

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.entrySize?.let { gridType = WorkbenchGridType.parse(it) }

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.appContainer?.map { it.transfer() }?.let {
            appContainer = it
        }

        headerShow = true

    }


}