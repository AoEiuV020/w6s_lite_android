package com.foreveross.atwork.api.sdk.discussion.requestJson

import com.google.gson.annotations.SerializedName

data class MapDiscussionTagRequest(

        @SerializedName("ops")
        val ops: String = "tag",

        @SerializedName("members")
        val mapDiscussionTagItems: List<MapDiscussionTagItem>
)

data class MapDiscussionTagItem(
        @SerializedName("user_id")
        var userId: String,

        @SerializedName("tags")
        var tags: List<String>? = null
)