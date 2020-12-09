package com.foreveross.atwork.infrastructure.plugin.behavior

import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IBehaviorLogManager: WorkplusPlugin {


    fun logLocalEmailWrite()

    fun logLocalEmailLoginClick()
}