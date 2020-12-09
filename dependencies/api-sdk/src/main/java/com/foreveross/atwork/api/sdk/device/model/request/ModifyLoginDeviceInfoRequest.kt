package com.foreveross.atwork.api.sdk.device.model.request

import com.google.gson.annotations.SerializedName

class ModifyLoginDeviceInfoRequest(

        @SerializedName("ops")
        var ops: String = "profileReset",

        @SerializedName("id")
        var id: String,

        @SerializedName("device_name")
        var deviceName: String
)