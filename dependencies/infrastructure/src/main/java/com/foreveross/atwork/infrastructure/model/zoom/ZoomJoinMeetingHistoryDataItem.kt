package com.foreveross.atwork.infrastructure.model.zoom

import com.google.gson.annotations.SerializedName

class ZoomJoinMeetingHistoryDataItem (
        @SerializedName("name")
        val name: String,

        @SerializedName("meeting_id")
        val meetingId: String,

        @SerializedName("join_time")
        val joinTime: Long
) {
        override fun toString(): String {
                return "ZoomJoinMeetingHistoryDataItem(name='$name', meetingId='$meetingId', joinTime=$joinTime)"
        }
}