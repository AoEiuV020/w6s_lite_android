package com.foreverht.workplus.ui.component.scrollView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import com.foreverht.workplus.ui.component.Util.Util

/**
 * Created by wuzejie on 2019/10/29.
 */

class MaxHeightSwitchOrgScrollView : ScrollView {


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val context = context
        if (null != context) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(Util.dip2px(context, 400f), View.MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}