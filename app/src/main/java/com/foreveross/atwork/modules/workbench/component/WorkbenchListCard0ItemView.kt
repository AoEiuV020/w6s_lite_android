package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import kotlinx.android.synthetic.main.component_workbench_item_list_0.view.*

class WorkbenchListCard0ItemView: RelativeLayout {

    constructor(context: Context?) : super(context) {
        findViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_item_list_0, this)

    }

    fun refreshView(workbenchListItem: WorkbenchListItem) {
        tvTitle.text = workbenchListItem.title
        tvSource.text = workbenchListItem.source
        ViewUtil.setVisible(tvSource, !StringUtils.isEmpty(workbenchListItem.source))


//        val time = workbenchListItem.getDateTime()
        if(StringUtils.isEmpty(workbenchListItem.dateTime)) {
            tvTime.visibility = View.GONE

        } else {
//            val showTime = TimeUtil.getStringForMillis(time, TimeUtil.getTimeFormat2(AtworkApplicationLike.baseContext))
            tvTime.text = workbenchListItem.dateTime
            tvTime.visibility = View.VISIBLE

        }



    }
}