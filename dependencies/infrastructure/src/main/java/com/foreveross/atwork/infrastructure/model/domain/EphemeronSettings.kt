package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class EphemeronSettings (
        @SerializedName("enabled")
        var enabled: Boolean = false,

        @SerializedName("text_read_time_words15")
        var textReadTimeWords15: Int = 0,

        @SerializedName("text_read_time_words30")
        var textReadTimeWords30: Int = 0,

        @SerializedName("text_read_time_words100")
        var textReadTimeWords100: Int = 0,

        @SerializedName("text_read_time_words140")
        var textReadTimeWords140: Int = 0,

        @SerializedName("image_read_time")
        var imageReadTime: Int = 0,

        @SerializedName("voice_read_time")
        var voiceReadTime: Int = 0,

        @SerializedName("msg_retention_time")
        var msgRetentionTime: Long = 0
) : Parcelable