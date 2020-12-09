package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 写入设备的结果反馈
 */
class WriteStateData {
    /**code*/
    @SerializedName("code")
    var code: Int? = null
    /**message*/
    @SerializedName("message")
    var message: String? = null
}