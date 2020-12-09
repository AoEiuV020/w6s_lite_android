package com.foreveross.atwork.modules.workbench.model

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.workbench.*
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager

class WorkbenchFilterCardsResult {

    lateinit var workbench: Workbench

    var cardsSort: ArrayList<WorkbenchCard> = arrayListOf()

    var cardsDisplay: ArrayList<WorkbenchCard> = arrayListOf()

    var cardsNotDisPlay: ArrayList<WorkbenchCard> = arrayListOf()



    fun classify() {
        val context = AtworkApplicationLike.baseContext
        val cardIdListCustomSorted: List<String>? = OrgPersonalShareInfo.getInstance().getCustomSortedCardIdList(context, workbench.orgCode, workbench.id)
        val cardIdListCustomDisplay: List<String>? = OrgPersonalShareInfo.getInstance().getCustomDisplayCardIdList(context, workbench.orgCode, workbench.id)

        cardsDisplay.clear()
        cardsNotDisPlay.clear()

        cardsSort.forEach { card ->


            if(shouldDisplay(card, cardIdListCustomDisplay, cardIdListCustomSorted)) {
                cardsDisplay.add(card)
            } else {
                cardsNotDisPlay.add(card)

            }


        }
    }



    private fun shouldDisplay(card: WorkbenchCard, cardIdListCustomDisplay: List<String>?, cardIdListCustomSorted: List<String>?): Boolean {
        if (!card.deletable)
            return true


        cardIdListCustomDisplay?.let {
            if(it.contains(card.id.toString())) {
                return true
            }
        }

        cardIdListCustomSorted?.let {
            if (it.contains(card.id.toString())) {
                return false
            }
        }



        return card.displayable
    }


}