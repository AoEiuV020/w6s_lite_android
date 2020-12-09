package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class EmailSettings (
        @SerializedName("enabled")
        var enable : Boolean = true

) : Parcelable