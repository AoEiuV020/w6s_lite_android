package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.utils.MapUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*
import java.util.regex.Pattern

/**
 * Created by dasunsy on 2018/3/25.
 */

class WebviewConfig : BaseConfig(){



    /**
     * 触发连续返回的 url 正则表达式(某些 url 因做了自跳转, 如果按正常的按下返回的话, 每当返回到"首页", 就会又自跳转到其他页面, 所以该处为
     * 客户提供配置, 可以对使用场景频繁的页面进行适配, 做"连续返回", 从而跳出无限循环的怪圈)
     */
    private val comboBackRegex = HashMap<String, Pattern?>()

    var isCommandMainWebviewAppearCallback = false

    var isLogoutClearCookies = true

    var isAlwaysShowClose = false

    var isDownloadUseSystem = false

    var isDownloadUseNotification = true

    var isOpenFileAfterDownloaded = true

    var isForcedUseX5 = false

    var isTextZoomOnCommonTextSizeSetting = false

    var isCloseImageBtn = false

    var isNeedFetchInfoFromRemote = true

    var titleCanUrl = true



    fun setComboBackRegex(comboBackRegexs: Array<String>): WebviewConfig {
        for (regex in comboBackRegexs) {
            comboBackRegex[regex] = null
        }
        return this
    }


    fun isUrlNeedComboBack(url: String?): Boolean {
        if(StringUtils.isEmpty(url)) {
            return false
        }

        if (MapUtil.isEmpty(comboBackRegex)) {
            return false
        }


        for (entry in comboBackRegex.entries) {
            var pattern: Pattern? = entry.value

            if (null == pattern) {
                pattern = Pattern.compile(entry.key)
                entry.setValue(pattern)
            }

            if (pattern!!.matcher(url).matches()) {

                return true
            }
        }


        return false

    }

    override fun parse(pro: Properties) {
        pro.getProperty("WEBVIEW_COMBO_BACK_REGEX")?.let {
            val comboBackRegexs = it.split("::::".toRegex()).filter { !it.isEmpty() }.toTypedArray()
            setComboBackRegex(comboBackRegexs)

        }


        pro.getProperty("WEBVIEW_ALWAYS_SHOW_CLOSE")?.apply {
            isAlwaysShowClose = toBoolean()
        }

        pro.getProperty("WEBVIEW_COMMAND_MAIN_WEBVIEW_APPEAR_CALLBACK")?.apply {
            isCommandMainWebviewAppearCallback = toBoolean()
        }

        pro.getProperty("WEBVIEW_LOGOUT_CLEAR_COOKIES")?.apply {
            isLogoutClearCookies = toBoolean()
        }

        pro.getProperty("WEBVIEW_DOWNLOAD_USE_SYSTEM")?.apply {
            isDownloadUseSystem = toBoolean()
        }


        pro.getProperty("WEBVIEW_DOWNLOAD_USE_NOTIFICATION")?.apply {
            isDownloadUseNotification = toBoolean()
        }


        pro.getProperty("WEBVIEW_FORCED_USE_X5")?.apply {
            isForcedUseX5 = toBoolean()
        }


        pro.getProperty("WEBVIEW_TEXT_ZOOM_ON_COMMON_TEXT_SIZE_SETTING")?.apply {
            isTextZoomOnCommonTextSizeSetting = toBoolean()
        }


        pro.getProperty("OPEN_FILE_AFTER_DOWNLOADED")?.apply {
            isOpenFileAfterDownloaded = toBoolean()
        }


        pro.getProperty("WEBIVEW_IS_CLOSE_IMAGE_BTN")?.apply {
            isCloseImageBtn = toBoolean()
        }

    }



}
