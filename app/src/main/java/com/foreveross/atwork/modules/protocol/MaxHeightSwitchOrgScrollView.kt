package com.foreveross.atwork.modules.protocol

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView

class MaxHeightSwitchOrgScrollView : ScrollView {


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val context = context
        if (null != context) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(dip2px(context, 350f), View.MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    companion object{
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context?.resources?.displayMetrics?.density
            return (dpValue * scale!! + 0.5f).toInt()
        }
    }
}