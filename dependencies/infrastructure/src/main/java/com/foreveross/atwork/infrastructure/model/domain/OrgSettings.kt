package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class OrgSettings(
        @SerializedName("limit")
        var limit: Int = 1,

        @SerializedName("permission")
        var permission: String = "anonymous",

        @SerializedName("request_enabled")
        var requestEnable: Boolean = false
) : Parcelable