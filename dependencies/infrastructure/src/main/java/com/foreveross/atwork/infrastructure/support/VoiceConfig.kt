package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.beeworks.voice.BeeworksVoiceAliyun
import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil

class VoiceConfig (

        var isEnabled: Boolean = false,

        var sdk: VoiceTypeSdk = VoiceTypeSdk.UNKNOWN,

        var aliyunSdk: BeeworksVoiceAliyun? = null

): BaseConfig() {

    override fun parse() {
        BeeWorks.getInstance().config.beeworksVoice?.let { beeworksVoice->
            isEnabled = beeworksVoice.enabled

            val sdkType = EnumLookupUtil.lookup(VoiceTypeSdk::class.java, beeworksVoice.sdk.toUpperCase())
            if(null != sdkType) {
                sdk = sdkType
            }


            aliyunSdk = beeworksVoice.aliyunSdk

        }

    }
}