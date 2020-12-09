package com.foreveross.atwork.api.sdk.discussion.responseJson

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionTemplate
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class DiscussionTemplateResponse(
        @SerializedName("result")
        var template: DiscussionTemplate?

) : BasicResponseJSON()