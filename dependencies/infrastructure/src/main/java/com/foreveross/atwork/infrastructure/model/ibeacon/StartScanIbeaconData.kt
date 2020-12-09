package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 开启扫描返回数据
 */
class StartScanIbeaconData {
    /**device_id*/
    @SerializedName("device_id")
    var deviceId: String? = null
    /** 0 didEnter, 1 didExit, 2 didRange  返回监听状态*/
    @SerializedName("status")
    var status: String? = null
}