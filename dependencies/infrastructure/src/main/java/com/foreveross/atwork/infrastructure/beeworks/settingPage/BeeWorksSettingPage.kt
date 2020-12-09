package com.foreveross.atwork.infrastructure.beeworks.settingPage

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BeeWorksSettingPage(

    @SerializedName("invisible")
    var invisibleSettings: Array<BeeWorksInvisibleSetting>

): Parcelable