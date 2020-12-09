package com.foreveross.atwork.infrastructure.beeworks

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class BeeWorksDeling {

    @SerializedName("authKey")
    var authKey: String = StringUtils.EMPTY

    @SerializedName("api")
    var api: String = StringUtils.EMPTY

}