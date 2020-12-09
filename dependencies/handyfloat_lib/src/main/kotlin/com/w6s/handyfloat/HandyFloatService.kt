package com.w6s.handyfloat

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.org.apache.commons.lang3.math.NumberUtils.createFloat
import android.os.IBinder
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

/**
 *  悬浮窗后台监听服务
 *  create by reyzhang22 at 2019-08-16
 */

internal class HandyFloatService: Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val FLOAT_ACTION = "floatAction"
        private const val FLOAT_VISIBLE = "floatVisible"
        private const val FLOAT_DISMISS = "floatDismiss"
        private const val FLOAT_TAG = "floatTag"
        const val DEFAULT_TAG = "default"
        var floatMap = mutableMapOf<String, WindowFloatManager>()
        private var config = FloatConfig()

        /**
         * 开启创建浮窗的Service
         */
        fun startService(context: Context, floatConfig: FloatConfig) {
            config = floatConfig
            context.startService(Intent(context, HandyFloatService::class.java))
        }

        /**
         * 关闭浮窗后，检测是否需要关闭Service
         */
        fun checkStop(context: Context, floatTag: String?) {
            // 先清除当条浮窗信息
            if (floatMap.isNotEmpty()) floatMap.remove(floatTag)
            // 如有没有其他浮窗存在，关闭Service
            if (floatMap.isEmpty()) context.stopService(Intent(context, HandyFloatService::class.java))
        }

        /**
         * 设置浮窗可见性，hide or show
         */
        fun setVisible(context: Context, isShow: Boolean, tag: String? = null) =
                context.sendBroadcast(
                        Intent().setAction(FLOAT_ACTION)
                                .putExtra(FLOAT_VISIBLE, isShow)
                                .putExtra(FLOAT_TAG, tag)
                )

        /**
         * 关闭系统浮窗
         */
        fun dismiss(context: Context, tag: String? = null) =
                context.sendBroadcast(
                        Intent().setAction(FLOAT_ACTION)
                                .putExtra(FLOAT_DISMISS, true)
                                .putExtra(FLOAT_TAG, tag)
                )
    }

    // 通过广播，接收一些指令（关闭浮窗、设置可见性）
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 过滤掉不满足的条件
            if (intent.action != FLOAT_ACTION || floatMap.isNullOrEmpty()) return
            val tag = intent.getStringExtra(FLOAT_TAG) ?: DEFAULT_TAG
            when {
                // 关闭系统浮窗
                intent.getBooleanExtra(FLOAT_DISMISS, false) -> floatMap[tag]?.exit()

                // 设置浮窗可见
                intent.getBooleanExtra(FLOAT_VISIBLE, true) ->
                    floatMap[tag]?.setVisible(VISIBLE)

                // 设置浮窗不可见
                else -> floatMap[tag]?.setVisible(GONE)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 注册动态广播接收器
        registerReceiver(receiver, IntentFilter().apply { addAction(FLOAT_ACTION) })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (checkTag()) {
            // 通过floatManager创建浮窗，并将floatManager添加到map中
            floatMap[config.floatTag!!] = WindowFloatManager(this, config).apply { createFloat() }
        } else {
            config.floatCallbacks?.createdResult(false, "请为系统浮窗设置不同的tag", null)
        }
        return START_NOT_STICKY
    }

    /**
     * 检测浮窗的tag是否有效，不同的浮窗必须设置不同的tag
     */
    private fun checkTag(): Boolean {
        // 如果未设置tag，设置默认tag
        config.floatTag = config.floatTag ?: DEFAULT_TAG
        // map为空使用默认值，有效
        if (floatMap.isEmpty()) return true
        // map不为空，tag比对，存在相同的无效
        floatMap.forEach { (tag, _) -> run { if (tag == config.floatTag) return false } }
        return true
    }

    override fun onDestroy() {
        // 取消广播接收
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}

