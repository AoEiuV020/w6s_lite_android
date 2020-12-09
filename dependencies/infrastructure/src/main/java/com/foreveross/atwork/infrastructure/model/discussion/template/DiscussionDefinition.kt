package com.foreveross.atwork.infrastructure.model.discussion.template

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class DiscussionDefinition(
        @SerializedName("url")
        var url: String? = null,

        @SerializedName("params")
        var params: List<HashMap<String, String>>? = null,


        @SerializedName("app_id")
        var appId: String? = null,

        @SerializedName("domain_id")
        var domainId: String? = null,

        @SerializedName("entry_id")
        var entryId: String? = null,

        @SerializedName("org_code")
        var orgCode: String? = null,

        @SerializedName("org_name")
        var orgName: String? = null,

        @SerializedName("desc")
        var desc: String? = null

) : Parcelable