package com.foreveross.atwork.infrastructure.support

import java.util.*

class AboutWorkplusConfig: BaseConfig() {

    var isNeedIntroFriends = true

    override fun parse(pro: Properties) {

        pro.getProperty("ABOUT_WORKPLUS_NEED_INTRO_FRIENDS")?.apply {
            isNeedIntroFriends = toBoolean()
        }


    }
}