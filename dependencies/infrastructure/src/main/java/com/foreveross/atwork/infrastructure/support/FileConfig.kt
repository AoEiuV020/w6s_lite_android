package com.foreveross.atwork.infrastructure.support

import java.util.*

class FileConfig : BaseConfig(){

    var isAutoDownloadInChatFileView = false

    override fun parse(pro: Properties) {
        pro.getProperty("FILE_CONFIG_AUTO_DOWNLOAD_IN_CHAT_FILE_VIEW")?.apply {
            isAutoDownloadInChatFileView = toBoolean()
        }
    }
}