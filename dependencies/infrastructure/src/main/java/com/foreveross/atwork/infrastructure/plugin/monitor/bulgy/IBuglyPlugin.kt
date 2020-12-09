package com.foreveross.atwork.infrastructure.plugin.monitor.bulgy

import android.content.Context
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IBuglyPlugin: WorkplusPlugin {
    fun init(context: Context, key: String, debug: Boolean)
}