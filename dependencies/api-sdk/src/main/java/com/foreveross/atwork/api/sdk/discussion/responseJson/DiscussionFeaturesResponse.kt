package com.foreveross.atwork.api.sdk.discussion.responseJson

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.google.gson.annotations.SerializedName

data class DiscussionFeaturesResponse(
        @SerializedName("result")
        var result: DiscussionFeaturesResult?

) : BasicResponseJSON()

data class DiscussionFeaturesResult(
        @SerializedName("features")
        var discussionFeatures: List<DiscussionFeature>?
)