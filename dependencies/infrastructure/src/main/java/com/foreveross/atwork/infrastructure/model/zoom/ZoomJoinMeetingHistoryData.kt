package com.foreveross.atwork.infrastructure.model.zoom

import com.google.gson.annotations.SerializedName

class ZoomJoinMeetingHistoryData (

        @SerializedName("data")
        val dataList: ArrayList<ZoomJoinMeetingHistoryDataItem> = ArrayList()


) {
        override fun toString(): String {
                return "ZoomJoinMeetingHistoryData(dataList=$dataList)"
        }
}