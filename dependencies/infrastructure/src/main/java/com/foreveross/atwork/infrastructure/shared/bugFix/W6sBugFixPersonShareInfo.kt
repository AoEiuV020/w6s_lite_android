package com.foreveross.atwork.infrastructure.shared.bugFix

import android.content.Context
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils

object W6sBugFixPersonShareInfo: PersonalShareInfo() {
    /**
     * (置顶以及消息勿扰旧版本)兼容处理是否已经完成
     */
    private const val MAKE_COMPATIBLE_FOR_SESSION_TOP_AND_SHIELD = "MAKE_COMPATIBLE_FOR_SESSION_TOP_AND_SHIELD"

    private const val UPDATE_SETTING_DB_PRIMARY_KEY_FOR_BUSINESS_CASE = "UPDATE_SETTING_DB_PRIMARY_KEY_FOR_BUSINESS_CASE"

    private const val UPDATE_APP_DB_BIOMETRIC_AUTHENTICATION_DB_COLUMN = "UPDATE_APP_DB_BIOMETRIC_AUTHENTICATION_DB_COLUMN"

    private const val ORGCODES_NEED_FORCED_CHECK_APP_REFRESH = "ORGCODES_NEED_FORCED_CHECK_APP_REFRESH"


    fun setMakeCompatibleForSessionTopAndShield(context: Context, makeCompatible: Boolean) {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), MAKE_COMPATIBLE_FOR_SESSION_TOP_AND_SHIELD, makeCompatible)
    }

    fun hasMakeCompatibleForSessionTopAndShield(context: Context): Boolean {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), MAKE_COMPATIBLE_FOR_SESSION_TOP_AND_SHIELD, false)
    }



    fun setUpdateSettingDbPrimaryKeyForBusinessCaseValue(context: Context, makeCompatible: Boolean) {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), UPDATE_SETTING_DB_PRIMARY_KEY_FOR_BUSINESS_CASE, makeCompatible)
    }

    fun hasUpdatedSettingDbPrimaryKeyForBusinessCaseValue(context: Context): Boolean {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), UPDATE_SETTING_DB_PRIMARY_KEY_FOR_BUSINESS_CASE, false)
    }


    fun setUpdateAppDbBiometricAuthenticationDbColumnValue(context: Context, value: Boolean) {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), UPDATE_APP_DB_BIOMETRIC_AUTHENTICATION_DB_COLUMN, value)
    }


    fun hasUpdateAppDbBiometricAuthenticationDbColumnValue(context: Context): Boolean {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), UPDATE_APP_DB_BIOMETRIC_AUTHENTICATION_DB_COLUMN, false)
    }


    fun getOrgCodesNeedForcedCheckAppRefresh(context: Context): Set<String>? {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        return PreferencesUtils.getStringSet(context, getPersonalSpName(username), ORGCODES_NEED_FORCED_CHECK_APP_REFRESH)
    }

    fun setOrgCodesNeedForcedCheckAppRefresh(context: Context, orgCodes: Set<String>) {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        PreferencesUtils.putStringSet(context, getPersonalSpName(username), ORGCODES_NEED_FORCED_CHECK_APP_REFRESH, orgCodes);
    }




}