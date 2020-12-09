package com.foreveross.atwork.infrastructure.support

import java.util.*

class EncryptConfig : BaseConfig() {

    var isImageSaveIgnoringEncrypt = false

    var isRevertNamePure = true

    override fun parse(pro: Properties) {

        pro.getProperty("ENCRYPT_CONFIG_IMAGE_SAVE_IGNORING_ENCRYPT")?.apply {
            isImageSaveIgnoringEncrypt = toBoolean()
        }

        pro.getProperty("ENCRYPT_CONFIG_REVERT_NAME_PURE")?.apply {
            isRevertNamePure = toBoolean()
        }


    }

}