package com.foreveross.atwork.infrastructure.model.organizationSetting

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class VolumeSettings: Parcelable {
        @SerializedName("disabled")
        var disabled : Boolean = false
}