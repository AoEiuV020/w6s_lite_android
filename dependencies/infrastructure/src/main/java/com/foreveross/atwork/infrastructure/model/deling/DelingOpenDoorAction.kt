package com.foreveross.atwork.infrastructure.model.deling

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class DelingOpenDoorAction (

    @SerializedName("key_info")
    var keyInfoList: List<KeyInfo> = emptyList(),

    @SerializedName("mode")
    var mode: Int = -1,

    @SerializedName("user_id")
    var userId: String? = StringUtils.EMPTY


)