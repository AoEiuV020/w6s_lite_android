package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class AssetSettings(
        @SerializedName("assets_enabled")
        var assetsEnabled: Boolean = false,

        @SerializedName("cn_name")
        var cnName: String,

        @SerializedName("en_name")
        var enName: String,

        @SerializedName("tw_name")
        var twName: String,

        @SerializedName("to_fortunes_enabled")
        var toFortunesEnabled: Boolean = false,

        @SerializedName("integration_unit")
        var integrationUnit: Long = 0,

        @SerializedName("to_fortunes_unit")
        var toFortunesUnit: Long = 0
) : Parcelable