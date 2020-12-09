package com.foreveross.atwork.infrastructure.beeworks

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BeeWorksVote(

        @SerializedName("creator")
        var creatorUrl: String? = null,

        @SerializedName("myvote")
        var myvoteUrl: String? = null

) : Parcelable