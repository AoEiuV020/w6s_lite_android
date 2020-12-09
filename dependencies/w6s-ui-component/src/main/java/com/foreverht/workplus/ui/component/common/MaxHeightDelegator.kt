package com.foreverht.workplus.ui.component.common

import android.view.View.MeasureSpec

class MaxHeightDelegator {

    var maxHeight = -1

    fun getHeightMeasureSpec(heightMeasureSpecReceived: Int): Int {
        var heightMeasureSpec = heightMeasureSpecReceived
        if (maxHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight,
                    MeasureSpec.AT_MOST)
        }

        return heightMeasureSpec
    }

}