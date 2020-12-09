package com.foreveross.atwork.api.sdk.discussion.responseJson

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionMemberTag
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DiscussionTagsResponse(
        @SerializedName("result")
        var result: DiscussionTagsResult?

) : BasicResponseJSON()

data class DiscussionTagsResult(
        @SerializedName("tags")
        var discussionTags: List<DiscussionMemberTag>?
)