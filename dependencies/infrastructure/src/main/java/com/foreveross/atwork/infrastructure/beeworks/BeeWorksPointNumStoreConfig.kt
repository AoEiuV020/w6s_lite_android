package com.foreveross.atwork.infrastructure.beeworks

import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class BeeWorksPointNumStoreConfig {

    @SerializedName("enable")
    var enable = true

    @SerializedName("url")
    var url = StringUtils.EMPTY

    @SerializedName("appId")
    var appId = StringUtils.EMPTY

    @SerializedName("appKey")
    var appKey = StringUtils.EMPTY

    @SerializedName("version")
    var version = StringUtils.EMPTY



}