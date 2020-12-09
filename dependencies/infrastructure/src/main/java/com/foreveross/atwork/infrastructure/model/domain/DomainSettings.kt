package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.organizationSetting.VolumeSettings
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize


@Parcelize
class DomainSettings(@SerializedName("id")
                     var id: String = StringUtils.EMPTY,

                     @SerializedName("domain_id")
                     var domainId: String = StringUtils.EMPTY,

                     @SerializedName("workplus_url")
                     var workPlusUrl: String = StringUtils.EMPTY,

                     @SerializedName("dash_base_url")
                     var dashBaseUrl: String = StringUtils.EMPTY,

                     @SerializedName("org_settings")
                     var orgSettings: OrgSettings?,

                     @SerializedName("user_settings")
                     var userSettings: UserSettings?,

                     @SerializedName("email_settings")
                     var emailSettings: EmailSettings?,

                     @SerializedName("create_time")
                     var createTime: Long = 0,

                     @SerializedName("modify_time")
                     var modifyTime: Long = 0,

                     @SerializedName("weixin_sync_enabled")
                     var syncWeChat: Boolean = false,

                     @SerializedName("watermark_settings")
                     var watermarkSettings: WatermarkSettings?,

                     @SerializedName("ephemeron_settings")
                     var ephemeronSettings: EphemeronSettings?,

                     @SerializedName("chat_file_settings")
                     var chatFileSettings: ChatFileSettings?,

                     @SerializedName("pan_settings")
                     var panSettings: PanSettings?,

                     @SerializedName("assets_settings")
                     var assetSettings: AssetSettings?,

                     @SerializedName("chat_settings")
                     var chatSettings: ChatSettings?,

                     @SerializedName("user_schema_settings")
                     private var _userSchemaSettings: List<UserSchemaSettingItem>?,

                     @SerializedName("app_settings")
                     var appSettings: AppSettings?,

                     @SerializedName("phone_assistant")
                     var phoneAssistant: PhoneAssistant = PhoneAssistant(),

                     @SerializedName("volume_settings")
                     var volumeSettings: VolumeSettings = VolumeSettings(),

                     @SerializedName("envs")
                     var envsSettings: List<EnvSettings?> = arrayListOf()


) : Parcelable

{
    var userSchemaSettings: List<UserSchemaSettingItem>
    set(value) {
        _userSchemaSettings = value
    }

    get() {
        if(null == _userSchemaSettings) {
            _userSchemaSettings  = Gson().fromJson(DEFAULT_USER_SCHEMA_SETTINGS,  object : TypeToken<List<UserSchemaSettingItem>>(){}.type)
        }

        return _userSchemaSettings!!
    }

    override fun equals(o: Any?): Boolean {
        if (null == o) {
            return false
        }

        return if (o !is DomainSettings) {
            false
        } else toString() == o.toString()


    }


    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun toString(): String {
        return JsonUtil.toJson(this)
    }

}

internal var DEFAULT_USER_SCHEMA_SETTINGS = "[\n" +
        "{\n" +
        "\"property\": \"avatar\",\n" +
        "\"name\": \"头像\",\n" +
        "\"alias\": \"头像\",\n" +
        "\"visible\": true,\n" +
        "\"modifiable\": true,\n" +
        "\"sort_order\": 1\n" +
        "},\n" +
        "{\n" +
        "\"property\": \"name\",\n" +
        "\"name\": \"姓名\",\n" +
        "\"alias\": \"姓名\",\n" +
        "\"visible\": true,\n" +
        "\"modifiable\": true,\n" +
        "\"sort_order\": 2\n" +
        "},\n" +
        "{\n" +
        "\"property\": \"qr_code\",\n" +
        "\"name\": \"二维码名片\",\n" +
        "\"alias\": \"二维码名片\",\n" +
        "\"visible\": true,\n" +
        "\"modifiable\": true,\n" +
        "\"sort_order\": 3\n" +
        "},\n" +
        "{\n" +
        "\"property\": \"gender\",\n" +
        "\"name\": \"性别\",\n" +
        "\"alias\": \"性别\",\n" +
        "\"visible\": true,\n" +
        "\"modifiable\": true,\n" +
        "\"sort_order\": 4\n" +
        "},\n" +
        "{\n" +
        "\"property\": \"birthday\",\n" +
        "\"name\": \"生日\",\n" +
        "\"alias\": \"生日\",\n" +
        "\"visible\": true,\n" +
        "\"modifiable\": true,\n" +
        "\"sort_order\": 5\n" +
        "}\n" +
        "]"