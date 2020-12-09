package com.w6s.handyfloat.component

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.w6s.handyfloat.FloatConfig
import com.w6s.handyfloat.enums.SeamMode
import java.lang.Math.min

/**
 *  create by reyzhang22 at 2019-08-16
 */
abstract class AbstractDragFloatView(
        context: Context,
        attrs: AttributeSet? = null,
        defaultStyleSet: Int = 0
): FrameLayout(context, attrs, defaultStyleSet ) {

    abstract fun getLayoutId(): Int?

    abstract fun renderView(view: View)

    lateinit var config: FloatConfig

    // 悬浮的父布局高度、宽度
    private var parentHeight = 0
    private var parentWidth = 0
    // 终点坐标
    private var lastX = 0
    private var lastY = 0
    // 浮窗各边距离父布局的距离
    private var leftDistance = 0
    private var rightDistance = 0
    private var topDistance = 0
    private var bottomDistance = 0
    private var minX = 0
    private var minY = 0
    private var parentRect = Rect()
    private var floatRect = Rect()
    private lateinit var parentView: ViewGroup
    private var isCreated = false

    init {
        FrameLayout(context, attrs, defaultStyleSet)
        config = FloatConfig()
        initView(context)
    }

    protected fun initView(context: Context) {
        if (getLayoutId() == null) {
            return
        }
        val view: View = LayoutInflater.from(context).inflate(getLayoutId()!!, this)
        renderView(view)
        config.setContentViewInterface?.setContentView(view)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (!isCreated) {
            isCreated = true
            // 有固定坐标使用固定坐标，没有固定坐标设置偏移量
            if (config.locationPair != Pair(0, 0)) {
                x = config.locationPair.first.toFloat()
                y = config.locationPair.second.toFloat()
            } else {
                x += config.offsetPair.first
                y += config.offsetPair.second
            }

            initParent()
            initDistanceValue()
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) updateView(event)
        // 是拖拽事件就进行拦截，反之不拦截
        // ps：拦截后将不再回调该方法，所以后续事件需要在onTouchEvent中回调
        return config.draging || super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // updateView(event)是拖拽功能的具体实现
        if (event != null) updateView(event)
        // 如果是拖拽，这消费此事件，否则返回默认情况，防止影响子View事件的消费
        return config.draging || super.onTouchEvent(event)
    }

    private fun initParent() {
        if (parent != null && parent is ViewGroup) {
            parentView = parent as ViewGroup
            parentHeight = parentView.height
            parentWidth = parentView.width
            parentView.getGlobalVisibleRect(parentRect)
        }
    }

    /**
     * 更新位置信息
     */
    private fun updateView(event: MotionEvent) {
        config.floatCallbacks?.touchEvent(this, event)
        // 关闭拖拽/执行动画阶段，不可拖动
        if (!config.dragEnable) {
            config.draging = false
            isPressed = true
            return
        }

        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                // 默认是点击事件，而非拖拽事件
                config.draging = false
                isPressed = true
                lastX = rawX
                lastY = rawY
                // 父布局不要拦截子布局的监听
                parent.requestDisallowInterceptTouchEvent(true)
                initParent()
            }

            MotionEvent.ACTION_MOVE -> {
                // 只有父布局存在才可以拖动
                if (parentHeight <= 0 || parentWidth <= 0) return

                val dx = rawX - lastX
                val dy = rawY - lastY
                // 忽略过小的移动，防止点击无效
                if (!config.draging && dx * dx + dy * dy < 81) return
                config.draging = true

                var tempX = x + dx
                var tempY = y + dy
                // 检测是否到达边缘
                tempX = when {
                    tempX < 0 -> 0f
                    tempX > parentWidth - width -> parentWidth - width.toFloat()
                    else -> tempX
                }
                tempY = when {
                    tempY < 0 -> 0f
                    tempY > parentHeight - height -> parentHeight - height.toFloat()
                    else -> tempY
                }

                when (config.seamMode) {
                    SeamMode.Left -> tempX = 0f
                    SeamMode.Right -> tempX = parentRect.right - width.toFloat()
                    SeamMode.Top -> tempY = 0f
                    SeamMode.Bottom -> tempY = parentRect.bottom - height.toFloat()

                    SeamMode.AutoHorizontal ->
                        tempX = if (rawX * 2 - parentRect.left > parentRect.right)
                            parentRect.right - width.toFloat() else 0f

                    SeamMode.AutoVertical ->
                        tempY = if (rawY - parentRect.top > parentRect.bottom - rawY)
                            parentRect.bottom - height.toFloat() else 0f

                    SeamMode.AutoSize -> {
                        leftDistance = rawX - parentRect.left
                        rightDistance = parentRect.right - rawX
                        topDistance = rawY - parentRect.top
                        bottomDistance = parentRect.bottom - rawY

                        minX = min(leftDistance, rightDistance)
                        minY = min(topDistance, bottomDistance)

                        val pair = sideForLatest(tempX, tempY)
                        tempX = pair.first
                        tempY = pair.second
                    }
                    else -> {
                    }
                }
                // 更新位置
                x = tempX
                y = tempY
                lastX = rawX
                lastY = rawY
                config.floatCallbacks?.drag(this, event)
            }

            MotionEvent.ACTION_UP -> {
                // 如果是拖动状态下即非点击按压事件
                isPressed = !config.draging

                when (config.seamMode) {
                    SeamMode.ResultLeft,
                    SeamMode.ResultRight,
                    SeamMode.ResultTop,
                    SeamMode.ResultBottom,
                    SeamMode.ResultSide ->{
                        // 动画
                    }
                    else -> if (config.draging) touchOver()
                }
            }

            else -> return
        }
    }

    /**
     * 拖拽结束或者吸附动画执行结束，更新配置
     */
    private fun touchOver() {
        config.draging = false
        config.floatCallbacks?.dragEnd(this)
    }

    /**
     * 吸附在距离最近的那个边
     */
    private fun sideForLatest(x: Float, y: Float): Pair<Float, Float> {
        var x1 = x
        var y1 = y
        if (minX < minY) {
            x1 = if (leftDistance == minX) 0f else parentWidth - width.toFloat()
        } else {
            y1 = if (topDistance == minY) 0f else parentHeight - height.toFloat()
        }
        return Pair(x1, y1)
    }

    /**
     * 计算一些边界距离数据
     */
    private fun initDistanceValue() {
        // 获取 floatingView 所显示的矩形
        getGlobalVisibleRect(floatRect)

        leftDistance = floatRect.left - parentRect.left
        rightDistance = parentRect.right - floatRect.right
        topDistance = floatRect.top - parentRect.top
        bottomDistance = parentRect.bottom - floatRect.bottom

        minX = min(leftDistance, rightDistance)
        minY = min(topDistance, bottomDistance)
    }

    internal fun exit() {
        config.floatCallbacks?.dismiss()
        parentView.removeView(this)
    }

}