package com.foreveross.atwork.modules.chat.component.chat

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.util.DensityUtil

class ChatDetailUpFetchCustomHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs, 0), RefreshHeader {

    private val progressBar: ProgressBar


    init {
        gravity = Gravity.CENTER
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleSmall)

        addView(progressBar, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        minimumHeight = DensityUtil.dp2px(40f)
    }


    override fun getView(): View {
        return this//真实的视图就是自己，不能返回null
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate//指定为平移，不能null
    }

    override fun onStartAnimator(layout: RefreshLayout, height: Int, maxDragHeight: Int) {}

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {


        return 250
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {

        }
    }



    override fun setPrimaryColors(vararg colors: Int) {

    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {

    }

    //        @Override
    //        public void onPulling(float percent, int offset, int height, int maxDragHeight) {
    //
    //        }
    //        @Override
    //        public void onReleasing(float percent, int offset, int height, int maxDragHeight) {
    //
    //        }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {

    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {

    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }
}
