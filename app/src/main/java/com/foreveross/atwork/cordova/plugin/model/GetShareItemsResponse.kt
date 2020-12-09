package com.foreveross.atwork.cordova.plugin.model

import com.google.gson.annotations.SerializedName

class GetShareItemsResponse(
        @SerializedName("items")
        val items: Array<CordovaGetShareItem>
)

class CordovaGetShareItem(
        @SerializedName("type")
        val type: String
)