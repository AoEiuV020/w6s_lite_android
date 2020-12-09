package com.w6s.handyfloat

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.w6s.handyfloat.component.ActivityFloatingView
import com.w6s.handyfloat_lib.R

/**
 *  Activity悬浮窗管理类，处理悬浮窗创建，销毁等
 *  create by reyzhang22 at 2019-08-16
 */
class ActivityFloatManager(val activity: Activity) {

    // 通过DecorView 获取屏幕底层FrameLayout，即activity的根布局，作为浮窗的父布局
    private var parentFrame: FrameLayout =
            activity.window.decorView.findViewById(android.R.id.content)

    /**
     * 创建Activity浮窗
     */
    fun createActivityFloatView(config: FloatConfig) {
        // 获取可拖拽浮窗的外壳
        val view = LayoutInflater.from(activity).inflate(R.layout.float_layout, parentFrame, false)
        // 为浮窗打上tag，如果未设置tag，使用类名作为tag
        view.tag = config.floatTag ?: activity.componentName.className
        // 默认wrap_content，会导致子view的match_parent无效，所以手动设置params
        view.layoutParams = FrameLayout.LayoutParams(
                if (config.matchWidth) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT,
                if (config.matchHeight) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            // 如若未设置固定坐标，设置浮窗Gravity
            if (config.locationPair == Pair(0, 0)) gravity = config.gravity
        }
        // 将浮窗外壳添加到根布局中
        parentFrame.addView(view)

        // 获取浮窗对象，即自定义的FloatingView
        val floatingView: ActivityFloatingView = view.findViewById<ActivityFloatingView>(R.id.floatingView).also {
            // 同步配置
            it.config = config
            // 设置浮窗的布局文件，即我们传递过来的xml布局文件
            it.setLayoutId(config.layoutId!!)
            // 设置空点击事件，用于接收触摸事件
            it.setOnClickListener {}
        }

        // 设置Callbacks
        config.floatCallbacks?.createdResult(true, null, floatingView)
    }

    /**
     * 关闭activity浮窗
     */
    fun dismiss(tag: String?) {
        val view = getShellyView(tag) ?: return
        val floatingView: ActivityFloatingView = view.findViewById(R.id.floatingView)
        floatingView.exit()
    }

    /**
     * 设置浮窗的可见性
     */
    fun setVisibility(tag: String?, visibility: Int) {
        val view = getShellyView(tag) ?: return
        view.visibility = visibility
        val floatingView: ActivityFloatingView? = view.findViewById(R.id.floatingView)
        if (visibility == View.GONE) {
            floatingView?.config?.floatCallbacks?.hide(floatingView)
        } else {
            floatingView?.config?.floatCallbacks?.show(floatingView)
        }
    }

    /**
     * 获取浮窗是否显示
     */
    fun isShow(tag: String? = null): Boolean {
        val view = getShellyView(tag) ?: return false
        return view.visibility == View.VISIBLE
    }

    /**
     * 设置是否可拖拽
     */
    fun setDragEnable(dragEnable: Boolean, tag: String? = null) {
        val view = getShellyView(tag) ?: return
        view.findViewById<ActivityFloatingView>(R.id.floatingView).also {
            it.config?.dragEnable = dragEnable
        }
    }

    /**
     * 获取浮窗的外壳view
     */
    private fun getShellyView(tag: String?): View? = parentFrame.findViewWithTag(getTag(tag))

    private fun getTag(tag: String?) = tag ?: activity.componentName.className

}