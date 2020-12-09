package com.w6s.handyfloat.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.w6s.handyfloat.FloatConfig
import com.w6s.handyfloat.enums.SeamMode
import java.lang.Math.min

/**
 *  悬浮窗点击工具类
 *  create by reyzhang22 at 2019-08-16
 */

internal class WindowFloatTouchUtils(var config: FloatConfig) {


    // 窗口所在的矩形
    private var parentRect: Rect = Rect()
    // 悬浮的父布局高度、宽度
    private var parentHeight = 0
    private var parentWidth = 0
    // 起点坐标
    private var lastX = 0f
    private var lastY = 0f
    // 浮窗各边距离父布局的距离
    private var leftDistance = 0
    private var rightDistance = 0
    private var topDistance = 0
    private var bottomDistance = 0
    // x轴、y轴的最小距离值
    private var minX = 0
    private var minY = 0

    /**
     * 根据吸附模式，实现相应的拖拽效果
     */
    fun updateFloat(
            view: View,
            event: MotionEvent,
            windowManager: WindowManager,
            params: WindowManager.LayoutParams
    ) {
        config.floatCallbacks?.touchEvent(view, event)
        // 不可拖拽、或者正在执行动画，不做处理
        if (!config.dragEnable ) {
            config.draging = false
            return
        }

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                config.draging = false
                // 记录触摸点的位置
                lastX = event.rawX
                lastY = event.rawY
                windowManager.defaultDisplay.getRectSize(parentRect)
                parentWidth = parentRect.width()
                parentHeight = parentRect.height()
            }

            MotionEvent.ACTION_MOVE -> {
                // 移动值 = 本次触摸值 - 上次触摸值
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY
                // 忽略过小的移动，防止点击无效
                if (!config.draging && dx * dx + dy * dy < 81) return
                config.draging = true

                var x = params.x + dx.toInt()
                var y = params.y + dy.toInt()
                // 检测浮窗是否到达边缘
                x = when {
                    x < 0 -> 0
                    x > parentWidth - view.width -> parentWidth - view.width
                    else -> x
                }
                y = when {
                    y < 0 -> 0
                    y > parentHeight - view.height - statusBarHeight(view) ->
                        parentHeight - view.height - statusBarHeight(view)
                    else -> y
                }

                when (config.seamMode) {
                    SeamMode.Left -> x = 0
                    SeamMode.Right -> x = parentWidth - view.width
                    SeamMode.Top -> y = 0
                    SeamMode.Bottom -> y = parentHeight - view.height

                    SeamMode.AutoHorizontal ->
                        x = if (event.rawX * 2 > parentWidth) parentWidth - view.width else 0

                    SeamMode.AutoVertical ->
                        y = if ((event.rawY - parentRect.top) * 2 > parentHeight)
                            parentHeight - view.height else 0

                    SeamMode.AutoSize -> {
                        leftDistance = event.rawX.toInt()
                        rightDistance = parentWidth - event.rawX.toInt()
                        topDistance = event.rawY.toInt() - parentRect.top
                        bottomDistance = parentHeight + parentRect.top - event.rawY.toInt()

                        minX = min(leftDistance, rightDistance)
                        minY = min(topDistance, bottomDistance)
                        if (minX < minY) {
                            x = if (leftDistance == minX) 0 else parentWidth - view.width
                        } else {
                            y = if (topDistance == minY) 0 else parentHeight - view.height
                        }
                    }
                    else -> {
                    }
                }

                // 重新设置坐标信息
                params.x = x
                params.y = y
                windowManager.updateViewLayout(view, params)
                config.floatCallbacks?.drag(view, event)
                // 更新上次触摸点的数据
                lastX = event.rawX
                lastY = event.rawY
            }

            MotionEvent.ACTION_UP -> {
                if (!config.draging) return
                when (config.seamMode) {
                    SeamMode.ResultLeft,
                    SeamMode.ResultRight,
                    SeamMode.ResultTop,
                    SeamMode.ResultBottom,
                    SeamMode.ResultHorizontal,
                    SeamMode.ResultVertical,
                    SeamMode.ResultSide -> {
                        //动画
                    }
                    else -> config.floatCallbacks?.dragEnd(view)
                }
            }

            else -> return
        }
    }


    /**
     * 计算一些边界距离数据
     */
    private fun initDistanceValue(params: WindowManager.LayoutParams, floatingView: View) {
        leftDistance = params.x
        rightDistance = parentWidth - (leftDistance + floatingView.right)
        topDistance = params.y
        bottomDistance =
                parentRect.bottom - statusBarHeight(floatingView) - (params.y + floatingView.bottom)

        minX = min(leftDistance, rightDistance)
        minY = min(topDistance, bottomDistance)
    }

    private fun statusBarHeight(view: View) = ScreenUtils.getStatusBarHeight(view.context.applicationContext)

}
