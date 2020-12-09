package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.ethanhua.skeleton.ViewReplacer
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.workbench.component.skeleton.WorkbenchBasicSkeletonView

abstract class WorkbenchBasicCardView<T : WorkbenchCard> : RelativeLayout, IWorkbenchCardRefreshView<T> {

    var viewReplacer: ViewReplacer? = null

    constructor(context: Context) : super(context) {
        findViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
    }

    abstract fun findViews(context: Context)



    private fun initViewReplacer(): Boolean {
        val contentView = contentView()
        if (null == contentView) {
            return false
        }

        if (null == viewReplacer) {
            viewReplacer = ViewReplacer(contentView)
        }
        return true
    }

    protected fun showLoading() {

        if (workbenchCard.isContentLegal()) {
            return
        }

        if (!initViewReplacer()) return


        val skeletonView = skeletonView()
        if (null == skeletonView) {
            return
        }

        if(skeletonView is WorkbenchBasicSkeletonView) {
            skeletonView.showLoadingView()
        }

        LogUtil.e("restoreHolderView~~~~~~~~~~ showLoading")


        viewReplacer!!.replace(skeletonView)
    }


    fun showFail() {

        LogUtil.e("restoreHolderView~~~~~~~~~~ showFail")

        if (workbenchCard.isContentLegal()) {
            return
        }

        if (!initViewReplacer()) return

        val viewReplacer = viewReplacer!!

        val skeletonView = skeletonView()
        if(null != skeletonView && skeletonView is WorkbenchBasicSkeletonView) {
            skeletonView.showFailView()
            skeletonView.workbenchCardRefreshView = this
            viewReplacer.replace(skeletonView)


            return
        }

        val failView = WorkbenchLoadedFailCardView(context)
        failView.workbenchCardRefreshView = this

        viewReplacer.replace(failView)

    }

    fun showEmpty() {
        if (!initViewReplacer()) return


        val skeletonView = skeletonView()
        if (null == skeletonView) {
            return
        }

        if(skeletonView is WorkbenchBasicSkeletonView) {
            skeletonView.showEmptyView()
        }

        LogUtil.e("restoreHolderView~~~~~~~~~~ showEmpty")


        viewReplacer!!.replace(skeletonView)
    }


    protected fun restoreHolderView() {

        if (!workbenchCard.isContentLegal()) {
            return
        }

        LogUtil.e("restoreHolderView~~~~~~~~~~")

        viewReplacer?.let {
            if(null != it.targetView) {
                it.restore()
            }

        }
    }


    protected open fun skeletonView(): View? {
        return null
    }

    protected open fun contentView(): View? {
        return null
    }


}