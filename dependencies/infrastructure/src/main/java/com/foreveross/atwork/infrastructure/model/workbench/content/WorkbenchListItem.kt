package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class WorkbenchListItem(

        @SerializedName("title")
        val title: String?,

        @SerializedName("sub_title")
        val subTitle: String?,

        @SerializedName("date_time")
        val dateTime: String?,


        @SerializedName("source")
        val source: String?,


        @SerializedName("event_type")
        val eventType: String?,


        @SerializedName("event_value")
        val eventValue: String?,

        @SerializedName("icon_type")
        val iconType: String?,

        @SerializedName("icon_value")
        val iconValue: String?

) {

    fun getDateTime(): Long {
        if (StringUtils.isEmpty(dateTime)) {
            return 0
        }

        val timeLong = dateTime!!.toLongOrNull()

        if(null == timeLong) {
            return 0
        }

        return timeLong
    }
}