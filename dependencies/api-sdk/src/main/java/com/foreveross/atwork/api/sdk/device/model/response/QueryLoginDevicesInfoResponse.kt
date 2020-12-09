package com.foreveross.atwork.api.sdk.device.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo
import com.google.gson.annotations.SerializedName

class QueryLoginDevicesInfoResponse (

        @SerializedName("result")
        var devices: List<LoginDeviceInfo>?

): BasicResponseJSON()

