package com.foreveross.atwork.infrastructure.support.setting

import com.foreveross.atwork.infrastructure.beeworks.settingPage.BeeWorksInvisibleSetting
import com.foreveross.atwork.infrastructure.model.settingPage.W6sAccountAndSecureSetting
import com.foreveross.atwork.infrastructure.model.settingPage.W6sGeneralSetting
import com.foreveross.atwork.infrastructure.model.settingPage.W6sSetting
import com.foreveross.atwork.infrastructure.model.settingPage.W6sTopSetting
import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils

class InvisibleSettingConfig(

        var settingCategory: String = StringUtils.EMPTY,

        var settings: ArrayList<W6sSetting> = arrayListOf()

) {
    fun parse(beeWorksInvisibleSetting: BeeWorksInvisibleSetting) {

        settingCategory = beeWorksInvisibleSetting.category
        beeWorksInvisibleSetting.names.forEach { name ->

            when (settingCategory) {
                "W6sTopSetting" -> {
                    EnumLookupUtil.lookup(W6sTopSetting::class.java, name)?.let {
                        settings.add(it)
                    }
                }
                "W6sAccountAndSecureSetting" -> {
                    EnumLookupUtil.lookup(W6sAccountAndSecureSetting::class.java, name)?.let {
                        settings.add(it)
                    }
                }
                "W6sGeneralSetting" -> {
                    EnumLookupUtil.lookup(W6sGeneralSetting::class.java, name)?.let {
                        settings.add(it)
                    }
                }

            }



        }
    }
}