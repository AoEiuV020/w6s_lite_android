package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardContent
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils

class WorkbenchShortcut1Card : WorkbenchCommonTitleCard() {

    var shortCut1Type = WorkbenchGridType.TYPE_2_4

    override fun supportNotice(): Boolean = true

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.entrySize?.let {
            shortCut1Type = WorkbenchGridType.parse(it)
        }


    }

    override fun getNoticeDataUrlInfos(): Map<String, String>? = getCardContent<WorkbenchShortcutCardContent>()?.itemList
            ?.filter {
//                val showType = WorkbenchShortCardShowType.parse(it.showType)
//
//                WorkbenchShortCardShowType.ICON == showType
//                        &&
                        !StringUtils.isEmpty(it.getNoticeDataId())
                        && !StringUtils.isEmpty(it.tipUrl)
            }
            ?.associate {
                it.getNoticeDataId()!! to it.tipUrl!!
            }
}
