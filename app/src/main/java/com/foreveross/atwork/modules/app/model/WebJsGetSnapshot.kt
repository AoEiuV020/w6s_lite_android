package com.foreveross.atwork.modules.app.model

import com.google.gson.annotations.SerializedName

class WebJsGetSnapshot {


    @SerializedName("title")
    var title: String? = null

    @SerializedName("coverUrl")
    var coverUrl: String? = null

    @SerializedName("description")
    var description: String? = null
}