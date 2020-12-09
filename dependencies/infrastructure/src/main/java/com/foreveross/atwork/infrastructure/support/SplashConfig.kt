package com.foreveross.atwork.infrastructure.support

import java.util.*

class SplashConfig : BaseConfig(){

    var isPureImgBg = false

    var needCompanyUrl = true


    override fun parse(pro: Properties) {
        pro.getProperty("SPLASH_PURE_IMG_BG")?.apply {
            isPureImgBg = toBoolean()
        }


        pro.getProperty("SPLASH_NEED_COMPANY_URL")?.apply {
            needCompanyUrl = toBoolean()
        }
    }
}