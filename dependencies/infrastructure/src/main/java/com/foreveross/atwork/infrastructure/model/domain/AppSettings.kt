package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

const val DEFAULT_INTERVAL: Long = 24 * 60 * 60 * 1000

@Parcelize
class AppSettings(

        @SerializedName("upgrade_remind")
        @Deprecated("方舟以及 3.16.0 的版本废弃该字段, 转而使用 #upgradeRemindMode ")
        var upgradeRemind: Boolean = true,

        @SerializedName("upgrade_remind_mode")
        var upgradeRemindMode: String? = null,

        @SerializedName("upgrade_remind_time")
        var upgradeRemindTime: Long = DEFAULT_INTERVAL,

        @SerializedName("user_device_auth_enable")
        var userDeviceAuthEnable: Boolean = false,

        @SerializedName("user_device_max_un_auth_count")
        var userDeviceMaxUnAuthCount: Int = 0,

        @SerializedName("user_device_un_auth_prompt")
        var userDeviceUnAuthPrompt: String = StringUtils.EMPTY,

        @SerializedName("user_device_un_auth_tw_prompt")
        var userDeviceUnAuthPromptTw: String = StringUtils.EMPTY,

        @SerializedName("user_device_un_auth_en_prompt")
        var userDeviceUnAuthPromptEn: String = StringUtils.EMPTY,

        @SerializedName("user_device_refuse_auth_prompt")
        var userDeviceRefuseAuthPrompt: String = StringUtils.EMPTY,

        @SerializedName("user_device_refuse_auth_tw_prompt")
        var userDeviceRefuseAuthPromptTw: String = StringUtils.EMPTY,

        @SerializedName("user_device_refuse_auth_en_prompt")
        var userDeviceRefuseAuthPromptEn: String = StringUtils.EMPTY


) : Parcelable