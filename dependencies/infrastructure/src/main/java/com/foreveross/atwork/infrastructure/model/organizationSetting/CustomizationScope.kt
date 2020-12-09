package com.foreveross.atwork.infrastructure.model.organizationSetting

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomizationScope(

        @SerializedName("type")
        var type: String? = null,


        @SerializedName("scopes")
        var scopes: List<String>? = null

) : Parcelable


enum class CustomizationScopeType {
        EMAIL_ATTACHMENT
}