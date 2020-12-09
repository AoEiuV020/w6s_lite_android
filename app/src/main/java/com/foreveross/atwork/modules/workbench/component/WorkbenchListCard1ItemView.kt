package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper
import kotlinx.android.synthetic.main.component_workbench_item_list_1.view.*

class WorkbenchListCard1ItemView: RelativeLayout {

    constructor(context: Context?) : super(context) {
        findViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_item_list_1, this)

        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivIcon)

    }

    fun refreshView(workbenchListItem: WorkbenchListItem) {
        tvTitle.text = workbenchListItem.title
        if(StringUtils.isEmpty(workbenchListItem.subTitle)) {
            tvSubTitle.visibility = View.GONE

        } else {
            tvSubTitle.text = workbenchListItem.subTitle
            tvSubTitle.visibility = View.VISIBLE
        }

        refreshIconView(ivIcon, workbenchListItem.iconType, workbenchListItem.iconValue, holdingRes = R.mipmap.appstore_loading_icon_size)

    }


}