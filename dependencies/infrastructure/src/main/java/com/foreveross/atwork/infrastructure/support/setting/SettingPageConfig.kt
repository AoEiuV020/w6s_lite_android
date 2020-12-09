package com.foreveross.atwork.infrastructure.support.setting

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.model.settingPage.W6sSetting
import com.foreveross.atwork.infrastructure.support.BaseConfig

class SettingPageConfig(

        var invisibleSettings: ArrayList<InvisibleSettingConfig> = arrayListOf()

): BaseConfig() {


    override fun parse() {
        BeeWorks.getInstance().mSettingPage?.invisibleSettings?.forEach {

            val invisibleSettingConfig = InvisibleSettingConfig()
            invisibleSettingConfig.parse(it)
            invisibleSettings.add(invisibleSettingConfig)

        }
    }

    fun isInvisible(setting: W6sSetting) = invisibleSettings.flatMap { it.settings }.contains(setting)
}