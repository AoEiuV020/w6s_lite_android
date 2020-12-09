package com.foreveross.atwork.infrastructure.manager.face.aliyun

import android.content.Context
import com.foreveross.atwork.infrastructure.model.face.aliyun.StartBioAuthAction
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore
import com.foreveross.atwork.infrastructure.plugin.face.aliyun.IAliyunFaceIDPlugin
import com.foreveross.atwork.infrastructure.plugin.face.inter.IAliyunFaceBioListener
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException

object AliyunFaceBioManager: IAliyunFaceIDPlugin {

    private var plugin: IAliyunFaceIDPlugin? = null

    private fun checkPlugin() {
        if (null == plugin) {
            try {
                WorkplusPluginCore.registerPresenterPlugin("com.foreverht.face.aliyun.AliyunFaceBioPresenter")
                plugin = WorkplusPluginCore.getPlugin(IAliyunFaceIDPlugin::class.java)

            } catch (e: ReflectException) {
                e.printStackTrace()
            }

        }
    }

    override fun init(context: Context) {
        checkPlugin()
        plugin?.init(context)
    }

    override fun startBioAuth(context: Context, startBioAuthAction: StartBioAuthAction, onResultListener: IAliyunFaceBioListener) {
        checkPlugin()
        plugin?.startBioAuth(context, startBioAuthAction, onResultListener)

    }

}