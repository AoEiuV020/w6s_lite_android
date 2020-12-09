package com.foreveross.atwork.infrastructure.model.workbench.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkbenchCardHeaderButtonData (

    @SerializedName("type")
    val type: String?,

    @SerializedName("icon")
    val icon: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("en_name")
    val enName: String?,

    @SerializedName("tw_name")
    val twName: String?,

    @SerializedName("url")
    val url : String?

): Parcelable