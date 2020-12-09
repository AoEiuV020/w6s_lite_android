package com.foreveross.atwork.infrastructure.support

import java.util.*

/**
 * Created by dasunsy on 2018/3/16.
 */

class LoginViewConfig : BaseConfig() {


    var avatarSize = 132

    var forcedHideRegisterBtn = false

    override fun parse(pro: Properties) {
        pro.getProperty("LOGIN_AVATAR_SIZE")?.apply {
            avatarSize = toInt()
        }

        pro.getProperty("LOGIN_VIEW_CONFIG_FORCED_HIDE_REGISTER_BTN")?.apply {
            forcedHideRegisterBtn = toBoolean()
        }

    }




}
