package com.w6s.module

import android.content.Context
import android.content.res.Resources
import android.os.Parcelable
import android.text.TextUtils
import com.foreveross.atwork.infrastructure.R
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.utils.language.LanguageSetting
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class MessageTags (

        @SerializedName("id")
        var tagId: String = "",

        @SerializedName("app_id")
        var appId: String = "",

        @SerializedName("domain_id")
        var domainId: String = "",

        @SerializedName("org_id")
        var orgId: String = "",

        @SerializedName("name")
        var name: String = "",

        @SerializedName("english_name")
        var enName: String = "",

        @SerializedName("tw_name")
        var twName: String = ""


) : Parcelable {
        fun getShowName(context: Context): String {
                var returnName = ""
                val currentSetting = CommonShareInfo.getLanguageSetting(context)
                if (LanguageSetting.SIMPLIFIED_CHINESE == currentSetting) {
                        returnName = name

                } else if (LanguageSetting.TRADITIONAL_CHINESE == currentSetting) {
                        returnName = twName

                } else if (LanguageSetting.ENGLISH == currentSetting) {
                        returnName = enName

                }

                return if (TextUtils.isEmpty(returnName)) name else returnName
        }
}