package com.foreveross.atwork.infrastructure.model.workbench.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkbenchDefinitionData (

        @SerializedName("widget_id")
        val widgetId : Long,

        @SerializedName("type")
        val type: String,


        @SerializedName("sort_order")
        var sortOrder: Int,

        @SerializedName("platforms")
        val platforms: List<String>,


        @SerializedName("displayable")
        val displayable: Boolean = true,

        @SerializedName("deletable")
        val deletable: Boolean = true

): Parcelable