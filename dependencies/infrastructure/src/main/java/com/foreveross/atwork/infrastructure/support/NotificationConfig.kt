package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*

class NotificationConfig: BaseConfig() {


    /**
     * 8.0后 notification 上设置 sound 已经废弃了不能使用(部分机型做了兼容适配, 就仍有效),
     * 需要在 NotificationChannel 上设置 sound 值. 若如此处理, 会无法做到每个notification去控制是否静音,
     * 因此 app 功能里的的"是否静音"就没有意义了, 需要屏蔽, 将此功能交给手机系统控制
     *
     * 需要注意的是: NotificationChannel#setSound在初次安装有作用
     * */
    var useChannelSoundOnAndroidO = true


    var keyNewDefaultNotificationChannelSuffix = StringUtils.EMPTY


    var isCommandHideDetail: Boolean = false

    override fun parse(pro: Properties) {
        pro.getProperty("NOTIFICATION_CONFIG_USE_CHANNEL_SOUND_ON_ANDROID_O")?.apply {
            useChannelSoundOnAndroidO = toBoolean()
        }


        pro.getProperty("NOTIFICATION_CONFIG_KEY_NEW_DEFAULT_NOTIFICATION_CHANNEL_SUFFIX")?.apply {
            keyNewDefaultNotificationChannelSuffix = toString()

//            CommonShareInfo.KEY_NEW_DEFAULT_NOTIFICATION_CHANNEL += keyNewDefaultNotificationChannelSuffix
        }


        pro.getProperty("NOTIFICATION_CONFIG_COMMAND_HIDE_DETAIL")?.apply {
            isCommandHideDetail = toBoolean()
        }
    }
}