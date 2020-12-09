package com.foreveross.atwork.infrastructure.plugin.monitor.tingyun

import android.content.Context
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface ITingyunPlugin: WorkplusPlugin {

    fun setLicenseKey(appKey: String): ITingyunPlugin

    fun withLocationServiceEnabled(enable: Boolean): ITingyunPlugin

    fun start(context: Context)

    fun setUserId(userId: String?)

}