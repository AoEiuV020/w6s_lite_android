package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class WatermarkSettings(
        @SerializedName("user")
        var user: String = "none",

        @SerializedName("discussion")
        var discussion: String = "none",

        @SerializedName("bing")
        var bing: String = "none",

        @SerializedName("email")
        var email: String = "none",

        @SerializedName("favorites")
        var favorites: String = "none"


) : Parcelable