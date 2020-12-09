package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.modules.workbench.component.skeleton.WorkbenchNews1CardSkeletonView
import kotlinx.android.synthetic.main.component_workbench_news_card_list.view.*

class WorkbenchNews1CardView: WorkbenchNewsListCardView {

    var skeletonView: WorkbenchNews1CardSkeletonView? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getCardType(): WorkbenchCardType = WorkbenchCardType.NEWS_1

    override fun getViewType(): Int = WorkbenchCardType.NEWS_1.hashCode()


    override fun skeletonView(): View? {
        if(null == skeletonView) {
            skeletonView = WorkbenchNews1CardSkeletonView(context)
        }

        return skeletonView
    }

    override fun contentView(): View? = rvList
}