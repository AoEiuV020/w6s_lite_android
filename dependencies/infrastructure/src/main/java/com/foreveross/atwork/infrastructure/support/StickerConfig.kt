package com.foreveross.atwork.infrastructure.support

import java.util.*

class StickerConfig: BaseConfig() {

    var isEnable = true

    override fun parse(pro: Properties) {

        pro.getProperty("STICKER_ENABLE")?.apply {
            isEnable = toBoolean()
        }
    }
}