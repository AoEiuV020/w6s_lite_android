package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 写入设备minor
 */
class WriteMinorResult {
    /**minor_id*/
    @SerializedName("minor_id")
    var minorId: String? = null
    /**minor_value*/
    @SerializedName("minor_value")
    var minorValue: String? = null
}