package com.foreveross.atwork.infrastructure.manager.bugFix

import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore
import com.foreveross.atwork.infrastructure.plugin.bugFix.IW6sBugFixManager


class W6sBugFixManager {

    companion object {

        @JvmStatic
        fun getInstance(): IW6sBugFixManager {
            return WorkplusPluginCore.getPluginAndCheckRegisterInstance(IW6sBugFixManager::class.java, "com.foreveross.atwork.modules.bugFix.manager.W6sBugFixCoreManager")
        }
    }
}