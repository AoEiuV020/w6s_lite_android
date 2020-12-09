package com.foreveross.atwork.infrastructure.beeworks.settingPage

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BeeWorksInvisibleSetting(

    @SerializedName("category")
    var category: String,

    @SerializedName("names")
    var names: Array<String>


): Parcelable