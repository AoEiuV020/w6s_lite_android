package com.foreveross.atwork.infrastructure.model.employee

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PositionDisplayNode(

        @Expose
        @SerializedName("id")
        var id: String = StringUtils.EMPTY,

        @Expose
        @SerializedName("name")
        var name: String = StringUtils.EMPTY,

        @Expose
        @SerializedName("path")
        var path: String = StringUtils.EMPTY,

        @Expose
        @SerializedName("type")
        var type: String = StringUtils.EMPTY

) : Parcelable