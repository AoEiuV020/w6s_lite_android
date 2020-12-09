package com.foreveross.atwork.infrastructure.model.deling

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class DelingDeleteRecordsAction(

        @SerializedName("device_id")
        var deviceId: String = StringUtils.EMPTY,

        @SerializedName("device_password")
        var devicePassword: String = StringUtils.EMPTY


)

