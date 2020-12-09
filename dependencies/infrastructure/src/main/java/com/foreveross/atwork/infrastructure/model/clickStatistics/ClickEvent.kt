package com.foreveross.atwork.infrastructure.model.clickStatistics

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList

class ClickEvent(
        var identifier: String = UUID.randomUUID().toString(),

        var id: String = StringUtils.EMPTY,

        var type: Type = Type.UNKNOWN,

        var begin: Long = TimeUtil.getCurrentTimeInMillis()

) {



    companion object {
        fun getIds(clickEvents: List<ClickEvent>): List<String> {
            val ids = ArrayList<String>()

            for(clickEvent in clickEvents) {
                ids.add(clickEvent.id)
            }

            return ids
        }

    }

}