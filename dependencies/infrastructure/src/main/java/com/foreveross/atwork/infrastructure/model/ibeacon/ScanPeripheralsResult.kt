package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * cordova发过来的数据,用于扫描所有设备
 */
class ScanPeripheralsResult {
    /**超时时间*/
    @SerializedName("timeout")
    var timeout: Int? = null
    /**扫描得到的个数*/
    @SerializedName("return_count")
    var returnCount: Int? = null
    /**是否一直扫描true 停止扫描， false 不停止扫描*/
    @SerializedName("is_stop")
    var iStop: Boolean? = null
}