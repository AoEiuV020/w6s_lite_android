package com.foreveross.atwork.component.floatView.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.component.floatView.WorkplusFloatView
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ServiceCompat
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.web.component.WebActionFloatViewFloatImpl
import com.foreveross.atwork.modules.web.component.WebUrlHookingFloatView
import com.foreveross.atwork.utils.FloatWinHelper


class WorkplusFloatService : Service() {





    private var floatView: WorkplusFloatView? = null

    private var wm: WindowManager? = null

    private val floatViewBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_CREATE_FLOAT_VIEW == action) {

                if(!FloatWinHelper.canPopFloatWin(this@WorkplusFloatService)) {
                    return
                }

                val type = intent.getIntExtra(TYPE_FLOAT_WINDOW, -1)

                when(type) {
                    WebUrlHookingFloatView.TYPE -> createUrlHookFloatingWindow()
                    WebActionFloatViewFloatImpl.TYPE -> createWebActionFloatingWindow()

                }



            } else if (ACTION_KILL_FLOAT_VIEW == action) {
                removeFloatView()
            }
        }
    }





    private fun createUrlHookFloatingWindow() {

        if (null == floatView || !floatView!!.isShown) {

            val wmParams = WindowManager.LayoutParams()

            floatView = WebUrlHookingFloatView(this)

            val screenWidth = wm!!.defaultDisplay.width
            val screenHeight = wm!!.defaultDisplay.height

            // 设置悬浮窗类型
            FloatWinHelper.setFloatType(wmParams)


            // 设置背景透明
            wmParams.format = PixelFormat.RGBA_8888

            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

            // 确定悬浮窗的对齐方式
            wmParams.gravity = Gravity.LEFT or Gravity.TOP
            // 设置悬浮层初始位置
            wmParams.x = screenWidth
            wmParams.y = screenHeight / 2

            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT

            floatView!!.setParams(wmParams)

            // show float view
            wm!!.addView(floatView, wmParams)

        }
    }


    private fun createWebActionFloatingWindow() {

        if (null == floatView || !floatView!!.isShown) {

            val wmParams = WindowManager.LayoutParams()

            floatView = WebActionFloatViewFloatImpl(this)

            val screenWidth = wm!!.defaultDisplay.width
            val screenHeight = wm!!.defaultDisplay.height

            // 设置悬浮窗类型
            FloatWinHelper.setFloatType(wmParams)


            // 设置背景透明
            wmParams.format = PixelFormat.RGBA_8888

            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

            // 确定悬浮窗的对齐方式
            wmParams.gravity = Gravity.LEFT or Gravity.TOP
            // 设置悬浮层初始位置
            val initX = screenWidth - DensityUtil.dip2px(20f + 86f)
            val initY = screenHeight - DensityUtil.dip2px(72f + 86f)

            wmParams.x = initX
            wmParams.y = initY

            floatView!!.setInitXY(initX.toFloat(), initY.toFloat())

            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT

            floatView!!.setParams(wmParams)

            // show float view
            wm!!.addView(floatView, wmParams)

        }
    }






    private fun removeFloatView() {
        try {
            if (null != floatView) {
                wm!!.removeView(floatView)
                floatView = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate() {
        super.onCreate()

        wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val filter = IntentFilter()
        filter.addAction(ACTION_CREATE_FLOAT_VIEW)
        filter.addAction(ACTION_KILL_FLOAT_VIEW)

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(floatViewBroadcastReceiver, filter)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    companion object {

        const val ACTION_CREATE_FLOAT_VIEW = "ACTION_CREATE_FLOAT_VIEW"

        const val ACTION_KILL_FLOAT_VIEW = "ACTION_KILL_FLOAT_VIEW"

        private const val TYPE_FLOAT_WINDOW = "TYPE_FLOAT_WINDOW"

        fun hasUrlHooking(): Boolean {
            return !StringUtils.isEmpty(PersonalShareInfo.getInstance().getUrlHookingForFloat(BaseApplicationLike.baseContext))
        }


        fun init() {
            val theAppContext = BaseApplicationLike.baseContext
            ServiceCompat.startServiceCompat(theAppContext, WorkplusFloatService::class.java)

        }


        fun checkWebUrlHookingFloat() {
            if (hasUrlHooking()) {
                sendCreateFloatingWindow(WebUrlHookingFloatView.TYPE)
            }
        }

        fun sendCreateFloatingWindow(type: Int) {
            val intent = Intent(ACTION_CREATE_FLOAT_VIEW)
            intent.putExtra(TYPE_FLOAT_WINDOW, type)
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent)
        }

        fun sendRemoveFloatingWindow(type: Int) {
            val intent = Intent(ACTION_KILL_FLOAT_VIEW)
            intent.putExtra(TYPE_FLOAT_WINDOW, type)
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent)
        }

        fun sendAllRemoveFloatingWindow() {
            val intent = Intent(ACTION_KILL_FLOAT_VIEW)
            intent.putExtra(TYPE_FLOAT_WINDOW, -1)
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent)
        }
    }
}