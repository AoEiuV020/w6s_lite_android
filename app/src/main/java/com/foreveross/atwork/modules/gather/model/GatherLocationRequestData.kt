package com.foreveross.atwork.modules.gather.model

import com.google.gson.annotations.SerializedName

class GatherLocationRequestData (

        @SerializedName("longitude")
        var longitude: Double,

        @SerializedName("latitude")
        var latitude: Double,

        @SerializedName("user_id")
        var userId: String,


        @SerializedName("username")
        var username: String
)