package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 开启或关闭ibeacon扫描
 */
class ScanIbeaconResult {
    /**0 监听边界 1 监听区域 2 监听边界和区域*/
    @SerializedName("mode")
    var mode: String? = null
}