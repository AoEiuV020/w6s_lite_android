package com.foreveross.atwork.infrastructure.shared

import android.content.Context
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils
import com.foreveross.atwork.infrastructure.utils.StringUtils

class LiteCommonShareInfo: CommonShareInfo() {

    companion object {

        private const val KEY_DATA_LITE_BIND_CONFIG = "KEY_DATA_LITE_BIND_CONFIG"

        private const val KEY_DATA_LITE_BIND_CONFIG_CURRENT = "KEY_DATA_LITE_BIND_CONFIG_CURRENT"

        fun getDataLiteBindConfigs(context: Context): Set<String>? {
            return PreferencesUtils.getStringSet(context, SP_COMMON, KEY_DATA_LITE_BIND_CONFIG)
        }


        fun updateDataLiteBindConfig(context: Context, configDatas: Set<String>) {
            PreferencesUtils.putStringSet(context, SP_COMMON, KEY_DATA_LITE_BIND_CONFIG, configDatas)
        }

        fun getDataLiteBindConfigCurrent(context: Context): String {
            return PreferencesUtils.getString(context, SP_COMMON, KEY_DATA_LITE_BIND_CONFIG_CURRENT, StringUtils.EMPTY)
        }

        fun updateDataLiteBindConfigCurrent(context: Context, value: String) {
            PreferencesUtils.putString(context, SP_COMMON, KEY_DATA_LITE_BIND_CONFIG_CURRENT, value)
        }


        fun clear(context: Context) {
            PreferencesUtils.clear(context, SP_COMMON)
        }

    }


}