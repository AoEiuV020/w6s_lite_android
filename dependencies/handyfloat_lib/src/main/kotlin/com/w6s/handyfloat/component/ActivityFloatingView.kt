package com.w6s.handyfloat.component

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 *  Activity拖拽悬浮窗
 *  create by reyzhang22 at 2019-08-16
 */

class ActivityFloatingView(context: Context, attrs: AttributeSet? = null): AbstractDragFloatView(context, attrs)  {

    fun setLayoutId(layoutId: Int) {
        config.layoutId = layoutId
        initView(context)
        requestLayout()
    }

    override fun getLayoutId(): Int? = config.layoutId

    override fun renderView(view: View) { }


}