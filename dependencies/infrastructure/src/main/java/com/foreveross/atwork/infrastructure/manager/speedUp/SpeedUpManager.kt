package com.foreveross.atwork.infrastructure.manager.speedUp

import android.content.Context
import com.foreveross.atwork.infrastructure.model.speedUp.SpeedUpSdk
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore
import com.foreveross.atwork.infrastructure.plugin.speedUp.ISpeedUpPlugin
import com.foreveross.atwork.infrastructure.plugin.speedUp.IWangsuPlugin

object SpeedUpManager: ISpeedUpPlugin {

    var speedUpPlugin: ISpeedUpPlugin? = null

    private var speedUpSdk: SpeedUpSdk? = null



    fun setSdk(speedUpSdk: SpeedUpSdk) {
        this.speedUpSdk = speedUpSdk
    }

    override fun init(context: Context) {
        checkPlugin()
        speedUpPlugin?.init(context)
    }


    private fun checkPlugin() {
        when(speedUpSdk) {
            SpeedUpSdk.WANGSU -> {
                checkWangsuPlugin()


            }
        }

    }

    private fun checkWangsuPlugin() {
        if(null == speedUpPlugin) {
            WorkplusPluginCore.registerPresenterPlugin("com.foreverht.speedUp.wangsu.sce.WangsuScePresenter")
            speedUpPlugin = WorkplusPluginCore.getPlugin(IWangsuPlugin::class.java)
        }
    }


}