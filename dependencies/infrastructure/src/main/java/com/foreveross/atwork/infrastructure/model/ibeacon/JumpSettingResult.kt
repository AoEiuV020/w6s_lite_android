package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

class JumpSettingResult {
    /**需要跳转到其他页面时则为空，蓝牙设置时为"bluetooth"*/
    @SerializedName("type")
    var type: String? = null
}