package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardContent
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.utils.StringUtils

class WorkbenchShortcut0Card : WorkbenchCard() {

    var entryCount: Int = 5


    override fun supportNotice(): Boolean = true

    override fun parse(workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        workbenchCardDetailData.workbenchCardDisplayDefinitions?.entryCount?.let {
            entryCount = it
        }
    }

    override fun getNoticeDataUrlInfos(): Map<String, String>? =
            getShowItemList()
            ?.filter {
//                val showType = WorkbenchShortCardShowType.parse(it.showType)
//                WorkbenchShortCardShowType.ICON == showType
//                        &&
                 !StringUtils.isEmpty(it.getNoticeDataId())
                        && !StringUtils.isEmpty(it.tipUrl)
            }
            ?.associate {
                it.getNoticeDataId()!! to it.tipUrl!!
            }


    fun getShowItemList(): List<WorkbenchShortcutCardItem>? {
        val itemList = getCardContent<WorkbenchShortcutCardContent>()?.itemList ?: return null

        if(itemList.size <= entryCount) {
            return itemList
        }

        return itemList.subList(0, entryCount - 1) + itemList.last()
    }
}