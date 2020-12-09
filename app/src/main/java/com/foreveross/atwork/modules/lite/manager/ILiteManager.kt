package com.foreveross.atwork.modules.lite.manager

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.modules.lite.module.LiteBindConfig

interface ILiteManager {

    fun getBindConfigs(): Set<LiteBindConfig>?

    fun updateBindConfig(liteBindConfig: LiteBindConfig)

    fun produceBindConfig(data: String): LiteBindConfig

    fun updateBindConfigCurrent(liteBindConfig: LiteBindConfig)

    fun getBindConfigCurrent(): LiteBindConfig?

    fun noBindConfigCurrent(): Boolean

    fun matchBindRule(data: String): Boolean

    fun processBeeWork(beeWorks: BeeWorks, liteBindConfig: LiteBindConfig? = null)

    fun clearConfigs()
}