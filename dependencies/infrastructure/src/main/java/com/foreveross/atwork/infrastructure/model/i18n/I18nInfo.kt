package com.foreveross.atwork.infrastructure.model.i18n

import android.content.Context
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil

abstract class I18nInfo {


    open fun getStringName(): String? = null

    open fun getStringTwName(): String? = null

    open fun getStringEnName(): String? = null

    fun getNameI18n(context: Context): String {
        return getNameI18n(context, i18nInfo = this)
    }

    open fun getI18nInfo(): CommonI18nInfoData {
        val commonI18NInfoData = CommonI18nInfoData()
        commonI18NInfoData.name = getStringName()
        commonI18NInfoData.enName = getStringEnName()
        commonI18NInfoData.twName = getStringTwName()
        return commonI18NInfoData
    }


    companion object {

        @JvmStatic
        fun getNameI18n(context: Context, i18nInfo: I18nInfo): String {

            var nameI18n: String? = null
            when (LanguageUtil.getLanguageSupport(context)) {
                LanguageSupport.TRADITIONAL_CHINESE -> nameI18n = i18nInfo.getStringTwName()

                LanguageSupport.ENGLISH -> nameI18n = i18nInfo.getStringEnName()
            }

            if (StringUtils.isEmpty(nameI18n)) {
                nameI18n = i18nInfo.getStringName()
            }

            if(null == nameI18n) {
                nameI18n = StringUtils.EMPTY
            }

            return nameI18n
        }

    }

}