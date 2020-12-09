package com.w6s.model.favorite

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class FavoriteUsage(
        @SerializedName("tags")
        val favoriteTags: List<String>,

        @SerializedName("cost")
        val cost: Int,

        @SerializedName("owner_id")
        val ownerId: String
        ) : Parcelable {

}