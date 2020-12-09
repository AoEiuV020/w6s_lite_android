@file: JvmName("EncryptHelper")
package com.foreveross.atwork.api.sdk.auth.util

import android.content.Context
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.encryption.AES128CBCUtil
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils


fun getEncryptedPwd(context: Context, username: String? = null, originalPwd: String): String {

    val usernameUsed: String = if (StringUtils.isEmpty(username)) {
        LoginUserInfo.getInstance().getLoginUserRealUserName(context)

    } else {
        username!!
    }


    var key = MD5Utils.encoderByMd5(usernameUsed)
    if (16 < key.length) {
        key = key.substring(0, 16)
    }

    return Base64Util.encode(AES128CBCUtil.encrypt(key, originalPwd))
}

