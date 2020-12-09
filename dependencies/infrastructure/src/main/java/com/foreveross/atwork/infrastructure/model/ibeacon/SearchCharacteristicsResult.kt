package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 获取当前设备的特征，cordova传过来的数据
 */
class SearchCharacteristicsResult {
    /**设备id*/
    @SerializedName("device_id")
    var deviceId: String? = null
}