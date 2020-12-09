package com.foreveross.atwork.api.sdk.device.model.request

import com.google.gson.annotations.SerializedName

class HandleLoginDeviceRequest (

    @SerializedName("ops")
    var ops: String,

    @SerializedName("ids")
    var ids: List<String>
)


