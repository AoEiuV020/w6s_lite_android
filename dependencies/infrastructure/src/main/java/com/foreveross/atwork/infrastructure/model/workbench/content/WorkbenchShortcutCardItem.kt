package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class WorkbenchShortcutCardItem(

        @SerializedName("show_type")
        val showType: String? = "NUMBER",


        @SerializedName("number")
        val number: String? = StringUtils.EMPTY,

        @SerializedName("title")
        val title: String? = StringUtils.EMPTY,


        @SerializedName("icon_type")
        val iconType: String? = "URL",


        @SerializedName("icon_value")
        val iconValue: String? = StringUtils.EMPTY,


        @SerializedName("event_type")
        val eventType: String? = "URL",


        @SerializedName("event_value")
        val eventValue: String? = StringUtils.EMPTY,


        @SerializedName("tip_url")
        val tipUrl: String?,

        var index: Int = -1,

        var requestId: String = StringUtils.EMPTY

) {


    fun getNoticeDataId(): String? = "${tipUrl}_${requestId}_${index}"


}