package com.foreveross.atwork.modules.dropbox

import com.foreveross.atwork.infrastructure.utils.TimeUtil

/**
 *  create by reyzhang22 at 2019-11-14
 */
//-1:已失效 0 有效期内，1永久有效(考虑到kt使用enum耗费内存，所以用数字表达)
fun isFileOverdue(expiredTime: Long): Int {
    if (expiredTime <= 0 || expiredTime >= 9999999999999L) {
        return 1
    }
    val deadlineStamp = expiredTime - TimeUtil.getCurrentTimeInMillis()
    if (deadlineStamp <= 0) {
        return -1
    }
    return 0
}