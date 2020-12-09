package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 注册iBeacon设备
 */
class RegisterIbeaconResult {
    /**uuid_string*/
    @SerializedName("device_id")
    var uuidString: List<String>? = null
    /**timeout*/
    @SerializedName("timeout")
    var timeout: Int? = null
//    /**majorValue*/
//    @SerializedName("major_value")
//    var majorValue: String? = null
//    /**minorValue*/
//    @SerializedName("minor_value")
//    var minorValue: String? = null
//    /**notifyType*/
//    @SerializedName("notify_type")
//    var notifyType: String? = null
}