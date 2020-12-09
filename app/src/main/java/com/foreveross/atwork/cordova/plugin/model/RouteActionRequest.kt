package com.foreveross.atwork.cordova.plugin.model

import com.google.gson.annotations.SerializedName

class RouteActionRequest {

    @SerializedName("android")
    var routeAction: RouteAction? = null
}

class RouteAction {
    @SerializedName("action")
    var action: String? = null
}