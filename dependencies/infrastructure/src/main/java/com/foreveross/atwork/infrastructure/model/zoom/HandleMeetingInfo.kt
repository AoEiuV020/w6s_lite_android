package com.foreveross.atwork.infrastructure.model.zoom

import com.google.gson.annotations.SerializedName

class HandleMeetingInfo(

        @SerializedName("user_id")
        var userId: String? = null,

        @SerializedName("display_name")
        var displayName: String? = null,

        @SerializedName("token")
        var token: String? = null,

        @SerializedName("zak")
        var zak: String? = null,

        @SerializedName("meeting_id")
        var meetingId: String? = null,

        @SerializedName("meeting_pwd")
        var meetingPwd: String? = null,

        @SerializedName("cuid")
        var cuid: String? = null,

        @SerializedName("meeting_url")
        var meetingUrl: String? = null
)