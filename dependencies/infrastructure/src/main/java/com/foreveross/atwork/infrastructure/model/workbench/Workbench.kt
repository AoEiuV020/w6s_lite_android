package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils


fun platformMatch(platforms: List<String>) = platforms.contains("ANDROID")


class Workbench(

        var id: Long = 0,

        var domainId: String = StringUtils.EMPTY,

        var orgCode: String = StringUtils.EMPTY,

        var name: String = StringUtils.EMPTY,

        var enName: String = StringUtils.EMPTY,

        var twName: String = StringUtils.EMPTY,

        var workbenchCards: ArrayList<WorkbenchCard> = arrayListOf(),

        var platforms: List<String> = emptyList()
): I18nInfo() {


    override fun getStringName(): String? {
        return name
    }

    override fun getStringTwName(): String? {
        return twName
    }

    override fun getStringEnName(): String? {
        return enName
    }


    fun isLegal(): Boolean {
        if (!platformMatch(platforms)) {
            return false
        }

//        if (ListUtil.isEmpty(workbenchCards)) {
//            return false
//        }

        return true
    }


    fun findWorkbenchCard(id: Long): List<WorkbenchCard> {

        val cardFoundList = arrayListOf<WorkbenchCard>()

        workbenchCards.find { it.id == id }?.let {
            cardFoundList.add(it)
        }


        workbenchCards
                .filter { it is WorkbenchCategoryCard }
                .flatMap { (it as WorkbenchCategoryCard).subCards }
                .filter { it.id == id }
                .forEach { cardFoundList.add(it) }

        return cardFoundList

    }


}