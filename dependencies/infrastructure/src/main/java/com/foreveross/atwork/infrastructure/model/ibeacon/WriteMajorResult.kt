package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 写入设备Maijor
 */
class WriteMajorResult {
    /**majorId值*/
    @SerializedName("major_id")
    var majorId: String? = null
    /**majorValue值*/
    @SerializedName("major_value")
    var majorValue: String? = null
}