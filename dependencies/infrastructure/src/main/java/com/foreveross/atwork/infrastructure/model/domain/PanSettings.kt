package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PanSettings(
        @SerializedName("org_total_limit")
        var orgTotalLimit: Long = java.lang.Long.MAX_VALUE,

        @SerializedName("user_total_limit")
        var userTotalLimit: Long = java.lang.Long.MAX_VALUE,

        @SerializedName("internal_discussion_total_limit")
        var internalDiscussionTotalLimit: Long = java.lang.Long.MAX_VALUE,

        @SerializedName("user_discussion_total_limit")
        var userDiscussionTotalLimit: Long = java.lang.Long.MAX_VALUE,

        @SerializedName("user_discussion_enabled")
        var userDiscussionEnabled: Boolean = true,

        @SerializedName("org_item_limit")
        var orgItemLimit: Long = java.lang.Long.MAX_VALUE,

        @SerializedName("user_item_limit")
        var userItemLimit: Long = java.lang.Long.MAX_VALUE,

        @SerializedName("internal_discussion_item_limit")
        var internalDiscussionItemLimit: Long = java.lang.Long.MAX_VALUE,

        @SerializedName("user_discussion_item_limit")
        var userDiscussionItemLimit: Long = java.lang.Long.MAX_VALUE
) : Parcelable