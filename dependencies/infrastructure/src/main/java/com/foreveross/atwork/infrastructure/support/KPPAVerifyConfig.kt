package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks

private const val DEFAULT_CHECK_REMOTE_REQUEST_INTERVAL = 24 * 60 * 60 * 1000L

class KPPAVerifyConfig: BaseConfig() {

    var safeCheckTime: Long? = null

    var crackCheck: Boolean = false

    var canScreenCapture: Boolean = true


    fun needVerify(): Boolean = null != safeCheckTime


    fun getRequestInterval(): Long {

        if(null == safeCheckTime
                ||-1L == safeCheckTime) {
            return DEFAULT_CHECK_REMOTE_REQUEST_INTERVAL
        }

        return safeCheckTime!!
    }

    override fun parse() {
        safeCheckTime = BeeWorks.getInstance().config.safeCheckTime
        crackCheck = (1 == BeeWorks.getInstance().config.crackCheck)
        canScreenCapture = BeeWorks.getInstance().config.canScreenCapture()
    }
}