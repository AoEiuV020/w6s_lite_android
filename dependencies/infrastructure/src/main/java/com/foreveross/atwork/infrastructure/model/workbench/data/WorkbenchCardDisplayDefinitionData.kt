package com.foreveross.atwork.infrastructure.model.workbench.data

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.app.admin.QueryAppItemResultEntry
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class WorkbenchCardDisplayDefinitionData (

        @SerializedName("header_show")
        val headerShow: Boolean? = null,

        @SerializedName("header_button")
        var headerButton: List<WorkbenchCardHeaderButtonData>? = null,

        @SerializedName("sub_module")
        var subModule: List<WorkbenchCardSubModuleData>?= null,

        @SerializedName("sub_module_count")
        var subModuleCount: Int? = null,

        @SerializedName("entry_count")
        var entryCount: Int? = null,

        @SerializedName("entry_size")
        var entrySize: String? = null,

        @SerializedName("list_count")
        var listCount: Int? = null,

        @SerializedName("callback_url")
        var callbackUrl: String? = null,

        @SerializedName("link_url")
        var linkUrl: String? = null,

        @SerializedName("banner_height")
        var bannerHeight: String? = null,

        @SerializedName("single_icon")
        var singleIcon: String? = null,

        @SerializedName("interval_seconds")
        var intervalSeconds: Long? = null,

        @SerializedName("app_container")
        var appContainer: List<QueryAppItemResultEntry>? = null




): Parcelable