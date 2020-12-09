package com.foreveross.atwork.infrastructure.plugin.speedUp

import android.content.Context
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface ISpeedUpPlugin: WorkplusPlugin {

    fun init(context: Context)

}