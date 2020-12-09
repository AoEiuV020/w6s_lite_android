package com.foreveross.atwork.modules.workbench.component.skeleton

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.modules.workbench.component.IWorkbenchCardRefreshView

abstract class WorkbenchBasicSkeletonView: FrameLayout, IWorkbenchSkeletonFailedView, IWorkbenchSkeletonEmptyView {

    var workbenchCardRefreshView: IWorkbenchCardRefreshView<out WorkbenchCard>? = null

    constructor(context: Context) : super(context) {
        findViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
        registerListener()
    }

    open fun findViews() {

    }

    fun registerListener() {


        if(null == cardRefreshView()) {
            return
        }

        cardRefreshView()!!.setOnClickListener {

            workbenchCardRefreshView?.let {
                it.refreshContentRemote(true)
            }
        }
    }

    open fun cardRefreshView(): View? {
        return null
    }


    open fun cardEmptyView(): View? {
        return null
    }

    open fun cardSkeletonView(): View? {
        return null
    }

    override fun showFailView() {
        if (-1f != AtworkConfig.WORKBENCH_CONFIG.noDataViewHeight) {
            cardSkeletonView()?.updateLayoutParams {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        cardSkeletonView()?.isVisible = true


        cardRefreshView()?.isVisible = true

        cardEmptyView()?.isVisible = false

    }

    override fun showEmptyView() {

        if (-1f != AtworkConfig.WORKBENCH_CONFIG.noDataViewHeight) {
            cardSkeletonView()?.updateLayoutParams {
                height = DensityUtil.dip2px(AtworkConfig.WORKBENCH_CONFIG.noDataViewHeight)
            }
        }


        cardSkeletonView()?.isInvisible = true

        cardRefreshView()?.isVisible = false

        cardEmptyView()?.isVisible = true
    }

    open fun showLoadingView() {
        cardSkeletonView()?.isVisible = true

        cardRefreshView()?.isVisible = false

        cardEmptyView()?.isVisible = false
    }


}