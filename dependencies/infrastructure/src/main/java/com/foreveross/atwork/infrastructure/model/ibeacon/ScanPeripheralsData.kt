package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 扫描到设备之后返回给cordova的数据
 */
class ScanPeripheralsData {
    /**设备唯一地址 UUID*/
    @SerializedName("device_id")
    var deviceId: String? = null
    /**设备名称*/
    @SerializedName("device_name")
    var deviceName: String? = null
    /**设备信号强度，越小离越近*/
    @SerializedName("device_rssi")
    var deviceRssi: String? = null
    /**是否已经连接*/
    @SerializedName("device_isConnect")
    var deviceIsConnect: String? = null
    /**获取设备的平台*/
    @SerializedName("device_platform")
    var devicePlatform: String? = null
    /**deviceUuid*/
    var deviceUuid: String? = null
    /**devicemMajor*/
    var devicemMajor: String? = null
    /**deviceMinor*/
    var deviceMinor: String? = null
}