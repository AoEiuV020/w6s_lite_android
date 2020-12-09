package com.foreveross.atwork.modules.workbench.model

import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*

class WorkbenchSortCardItem(

        var titleContent: String? = null,


        var card: WorkbenchCard? = null,

        var cardDisplay: Boolean = true
) {

    var id: Long = if(null != card) {
        card!!.id
    } else {
        Random().nextLong()
    }


    fun isTitle(): Boolean = !StringUtils.isEmpty(titleContent)
}