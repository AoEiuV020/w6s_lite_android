package com.foreveross.atwork.infrastructure.model.workbench.factory

import com.foreveross.atwork.infrastructure.model.workbench.*
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.utils.StringUtils

object WorkbenchFactory {


    fun produce(workbenchData: WorkbenchData, displayAll: Boolean = false): Workbench {

        return Workbench().apply {
            id = workbenchData.id
            domainId = workbenchData.domainId
            orgCode = workbenchData.orgCode
            workbenchData.name?.let {
                name = it
            }

            workbenchData.enName?.let {
                enName = it
            }

            workbenchData.twName?.let {
                twName = it
            }

            platforms = workbenchData.platforms

            workbenchData.workbenchCards.forEach { workbenchDefinitionData ->

                workbenchData.findWorkbenchCardDetailData(workbenchDefinitionData.widgetId)?.let { workbenchCardDetailData ->

                    produceCard(workbenchData, workbenchDefinitionData, workbenchCardDetailData)
                            ?.takeIf { it.isCardLegal()  }
                            ?.takeIf { displayAll || !it.disable  }
                            ?.let { workbenchCards.add(it) }


                }
            }

//            workbenchCards.sortBy { it.sortOrder }
            workbenchCards.sort()


        }
    }


    private fun produceCard(workbenchData: WorkbenchData, workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData): WorkbenchCard? {

        val workbenchCardType = WorkbenchCardType.parse(workbenchCardDetailData.type)
        return when (workbenchCardType) {
            WorkbenchCardType.BANNER -> WorkbenchBannerCard().apply { parse(workbenchData.advertisements, workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.COMMON_APP -> WorkbenchCommonAppCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.SEARCH -> WorkbenchSearchCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.SHORTCUT_0 -> WorkbenchShortcut0Card().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.SHORTCUT_1 -> WorkbenchShortcut1Card().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.LIST_0 -> WorkbenchListTypeCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.LIST_1 -> WorkbenchListTypeCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.NEWS_0 -> WorkbenchListTypeCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.NEWS_1 -> WorkbenchListTypeCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.NEWS_2 -> WorkbenchListTypeCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.NEWS_3 -> WorkbenchNews3Card().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.CATEGORY -> produceCategoryCard(workbenchData, workbenchDefinitionData, workbenchCardDetailData)
            WorkbenchCardType.CUSTOM -> WorkbenchCustomCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.APP_MESSAGES -> WorkbenchNewsSummaryCard().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.APP_CONTAINER_0 -> WorkbenchAppContainer0Card().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            WorkbenchCardType.APP_CONTAINER_1 -> WorkbenchAppContainer1Card().apply { parse(workbenchDefinitionData, workbenchCardDetailData) }
            else -> null
        }

    }

    private fun produceCategoryCard(workbenchData: WorkbenchData, workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData): WorkbenchCategoryCard {
        val categoryCard =  WorkbenchCategoryCard().apply {
            parse(workbenchDefinitionData, workbenchCardDetailData)
        }

        categoryCard.subModules.forEachIndexed { index, cardSubModule ->

            parseCardFromSubModuleData(index, cardSubModule, workbenchData)?.let {
                categoryCard.subCards.add(it)
            }

        }

        return categoryCard
    }

    private fun parseCardFromSubModuleData(index: Int, cardSubModule: WorkbenchCardSubModule, workbenchData: WorkbenchData): WorkbenchCard? {
        if(null == cardSubModule.widgetId) {
            return null
        }


        val subCardDetailData = workbenchData.findWorkbenchCardDetailData(cardSubModule.widgetId!!)

        if(null == subCardDetailData) {
            return null
        }

        val subCardDefinitionData = WorkbenchDefinitionData(subCardDetailData.id, subCardDetailData.type, index, subCardDetailData.platforms)
        val card = produceCard(workbenchData, subCardDefinitionData, subCardDetailData)

        card?.let {
            it.requestId += "_${it.id}"

            if(it is WorkbenchCommonTitleCard) {
                it.isSubCard = true
            }
        }

        return card
    }


}