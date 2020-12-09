package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserSettings(
        @SerializedName("activated")
        var activated: Boolean = true,

        @SerializedName("mobile_contacts_enabled")
        var mobileContactsEnable: Boolean = false,

        @SerializedName("contacts_sync")
        var contactSync: String = "unlimit",

        @SerializedName("relationships_enabled")
        var relationshipsEnable: Boolean = false,

        @SerializedName("name_modification_enabled")
        var nameModificationEnable: Boolean = false,

        @SerializedName("online_enabled")
        var onlineEnabled: Boolean = false,

        @SerializedName("password_strength")
        var passwordStrength : String = PasswordStrength.MIDDLE.toString(),

        @SerializedName("screen_lock")
        var screenLock : String = CommonUsingSetting.DISABLED.toString(),

        @SerializedName("reset_password")
        var resetPassword : String = CommonUsingSetting.DISABLED.toString(),

        @SerializedName("biological_auth")
        var biologicalAuth: String = CommonUsingSetting.DISABLED.toString(),

        @SerializedName("password_regexs")
        var passwordRegexs: Array<String>? = null,

        @SerializedName("user_agreement_enabled")
        var userAgreementEnable: Boolean = false,

        @SerializedName("file_assistant_enabled")
        var fileAssistantEnabled: Boolean = false,

        @SerializedName("favorites_total_limit")
        var favoriteTotalLimit: Long = 0,

        @SerializedName("moments_enabled")
        var PersonalSignatureEnabled: Boolean = false,

        /**
         * 开启域语音指令（智能机器人）
         */
        @SerializedName("voice_assistant_enabled")
        var voiceAssistantEnabled: Boolean = false

) : Parcelable