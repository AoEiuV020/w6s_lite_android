package com.foreveross.atwork.infrastructure.model.i18n

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CommonI18nInfoData(
        @SerializedName("name")
        var name: String? = null,

        @SerializedName("en_name")
        var enName: String? = null,

        @SerializedName("tw_name")
        var twName: String? = null


) : Parcelable