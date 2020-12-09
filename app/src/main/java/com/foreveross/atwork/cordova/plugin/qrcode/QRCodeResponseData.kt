package com.foreveross.atwork.cordova.plugin.qrcode

import com.google.gson.annotations.SerializedName

/**
 * 调用阿里云二维码识别的接口返回的数据
 */
class QRCodeResponseData {

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("msg")
    var msg: String? = null
}