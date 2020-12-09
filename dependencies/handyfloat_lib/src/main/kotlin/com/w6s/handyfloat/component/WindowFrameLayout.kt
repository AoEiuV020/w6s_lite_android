package com.w6s.handyfloat.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.w6s.handyfloat.FloatConfig
import com.w6s.handyfloat.interfaces.OnFloatTouchListener

/**
 *  create by reyzhang22 at 2019-08-16
 */
internal class WindowFrameLayout(
        context: Context,
        private val config: FloatConfig,
        attrs: AttributeSet? = null,
        defStyleArr: Int = 0
): FrameLayout(context, attrs, defStyleArr) {

    var touchListener: OnFloatTouchListener? = null
    var layoutListener: OnLayoutListener? = null
    private var isCreated = false

    // 布局绘制完成的接口，用于通知外部做一些View操作，不然无法获取view宽高
    interface OnLayoutListener {
        fun onLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 初次绘制完成的时候，需要设置对齐方式、坐标偏移量、入场动画
        if (!isCreated) {
            isCreated = true
            layoutListener?.onLayout()
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) touchListener?.onTouch(event)
        // 是拖拽事件就进行拦截，反之不拦截
        // ps：拦截后将不再回调该方法，会交给该view的onTouchEvent进行处理，所以后续事件需要在onTouchEvent中回调
        return config.draging || super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) touchListener?.onTouch(event)
        return config.draging || super.onTouchEvent(event)
    }

}