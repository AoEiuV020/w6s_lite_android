package com.foreveross.atwork.modules.monitor.tingyun

import android.content.Context
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore
import com.foreveross.atwork.infrastructure.plugin.monitor.tingyun.ITingyunPlugin
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException

object TingyunManager : ITingyunPlugin {


    private var tingyunPlugin: ITingyunPlugin? = null


    private fun checkPlugin() {
        if (null == tingyunPlugin) {
            try {
                WorkplusPluginCore.registerPresenterPlugin("com.workplus.tingyun.TingyunPlugin")
                tingyunPlugin = WorkplusPluginCore.getPlugin(ITingyunPlugin::class.java)

            } catch (e: ReflectException) {
                e.printStackTrace()
            }

        }

    }


    override fun setUserId(userId: String?) {
        checkPlugin()
        tingyunPlugin?.setUserId(userId)
    }

    override fun setLicenseKey(appKey: String): ITingyunPlugin {
        checkPlugin()
        tingyunPlugin?.setLicenseKey(appKey)
        return this
    }

    override fun withLocationServiceEnabled(enable: Boolean): ITingyunPlugin {
        checkPlugin()
        tingyunPlugin?.withLocationServiceEnabled(enable)
        return this
    }

    override fun start(context: Context) {
        checkPlugin()
        tingyunPlugin?.start(context)
    }

}