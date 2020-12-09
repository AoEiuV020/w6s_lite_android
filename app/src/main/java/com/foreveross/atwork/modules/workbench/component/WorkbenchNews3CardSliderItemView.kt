package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper
import kotlinx.android.synthetic.main.component_workbench_news_item_list.view.*

class WorkbenchNews3CardSliderItemView: LinearLayout {

    constructor(context: Context?) : super(context) {
        findViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_slider_item_news_3, this)

        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivCover)
    }

    fun refreshViews(workbenchListItem: WorkbenchListItem) {

        tvTitle.text = workbenchListItem.title

        refreshIconView(ivCover, workbenchListItem.iconType, workbenchListItem.iconValue, R.mipmap.loading_workbench_news_slider_item_cover)
    }
}