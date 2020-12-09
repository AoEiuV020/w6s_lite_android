package com.foreveross.atwork.infrastructure.shared.dev

import android.content.Context
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils

object DevCommonShareInfo: CommonShareInfo() {

    private const val KEY_OPEN_DORAEMON_KIT = "KEY_OPEN_DORAEMON_KIT"
    private const val KEY_OPEN_LEAK_CANARY = "KEY_OPEN_LEAK_CANARY"
    private const val KEY_OPEN_WANGSU_SCE = "KEY_OPEN_WANGSU_SCE"
    private const val KEY_OPEN_OFFLINE_MESSAGE_SESSION_STRATEGY = "KEY_OPEN_OFFLINE_MESSAGE_SESSION_STRATEGY"

    fun setOpenOfflineMessageSessionStrategy(context: Context, open: Boolean) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_OPEN_OFFLINE_MESSAGE_SESSION_STRATEGY, open)
    }

    fun isOpenOfflineMessageSessionStrategy(context: Context): Boolean {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_OPEN_OFFLINE_MESSAGE_SESSION_STRATEGY, AtworkConfig.MESSAGE_CONFIG.modeDefaultOfflineMessagesOnSessionStrategy)
    }

    fun setOpenWangsuSce(context: Context, open: Boolean) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_OPEN_WANGSU_SCE, open)
    }

    fun isOpenWangsuSce(context: Context): Boolean {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_OPEN_WANGSU_SCE, false)
    }

    fun setOpenLeakCanary(context: Context, open: Boolean) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_OPEN_LEAK_CANARY, open)
    }

    fun isOpenLeakCanary(context: Context): Boolean {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_OPEN_LEAK_CANARY, false)
    }

    fun setOpenDoraemonKit(context: Context, open: Boolean) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_OPEN_DORAEMON_KIT, open)
    }

    fun isOpenDoraemonKit(context: Context): Boolean {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_OPEN_DORAEMON_KIT, false)
    }

}