package com.foreveross.atwork.infrastructure.support

import java.util.*

class AboutMeConfig : BaseConfig(){

    var isContactItemInAboutMe = false

    var isHideMoreBtn = false

    var needGuideByImage = false


    override fun parse(pro: Properties) {

        pro.getProperty("CONTACT_ITEM_IN_ABOUT_ME")?.apply {
            isContactItemInAboutMe = this.toBoolean()
        }


        pro.getProperty("HIDE_MORE_BTN_IN_ABOUT_ME")?.apply {
            isHideMoreBtn = this.toBoolean()
        }



        pro.getProperty("ABOUT_ME_NEED_GUIDE_BY_IMAGE")?.apply {
            needGuideByImage = toBoolean()
        }
    }
}
