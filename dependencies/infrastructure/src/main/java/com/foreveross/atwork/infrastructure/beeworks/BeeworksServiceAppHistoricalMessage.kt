package com.foreveross.atwork.infrastructure.beeworks

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BeeworksServiceAppHistoricalMessage (

    @SerializedName("searchable")
    var searchable: Boolean = true,

    @SerializedName("tags")
    var tags: Boolean = true

): Parcelable