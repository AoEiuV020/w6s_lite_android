package com.foreverht.workplus.ui.component.pullToRefresh.smartRefreshLayout

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible

import com.foreverht.workplus.ui.component.R
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.util.DensityUtil

import pl.droidsonroids.gif.GifImageView

class CustomHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs, 0), RefreshHeader {

    private val mHeaderText: TextView//标题文本
    private val mArrowView: ImageView//下拉箭头

    private var maxHeaderTextWidth = 0

    init {
        gravity = Gravity.CENTER
        mHeaderText = TextView(context)
        mArrowView = GifImageView(context)
        mArrowView.setImageResource(R.mipmap.loading_pull_to_refresh)

        mHeaderText.setTextColor(ContextCompat.getColor(context, R.color.common_text_color))

        addView(mArrowView, DensityUtil.dp2px(28f), DensityUtil.dp2px(28f))
        //        addView(mArrowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(Space(context), DensityUtil.dp2px(13f), DensityUtil.dp2px(20f))
        addView(mHeaderText, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        minimumHeight = DensityUtil.dp2px(80f)

    }


    override fun getView(): View {
        return this//真实的视图就是自己，不能返回null
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate//指定为平移，不能null
    }

    override fun onStartAnimator(layout: RefreshLayout, height: Int, maxDragHeight: Int) {}

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        //        mArrowView.setVisibility(GONE);

        if (success) {
            setHeaderText(R.string.finished_pull_to_refresh)
        } else {
            setHeaderText(R.string.failed_pull_to_refresh)
        }
        return 500//延迟500毫秒之后再弹回
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                setHeaderText(R.string.start_pull_to_refresh)

                mArrowView.visibility = View.VISIBLE
            }
            RefreshState.Refreshing -> setHeaderText(R.string.handling_pull_to_refresh)
            RefreshState.ReleaseToRefresh -> setHeaderText(R.string.release_pull_to_refresh)
        }
    }

    private fun setHeaderText(res: Int) {
        setHeaderText(context.getString(res))
    }

    private fun setHeaderText(text: String) {
        mHeaderText.doOnPreDraw {
            if (maxHeaderTextWidth < it.width) {
                maxHeaderTextWidth = it.width
            }

            mHeaderText.minWidth = maxHeaderTextWidth
        }


        mHeaderText.text = text


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
