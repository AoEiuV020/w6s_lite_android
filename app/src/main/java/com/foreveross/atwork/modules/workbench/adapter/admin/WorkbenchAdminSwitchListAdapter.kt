package com.foreveross.atwork.modules.workbench.adapter.admin

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager


class WorkbenchAdminSwitchListAdapter(dataList: List<WorkbenchData>): BaseQuickAdapter<WorkbenchData, WorkbenchAdminSwitchListItemViewHolder>(R.layout.component_workbench_item_workbench_admin_switch, dataList) {

    var selectIdShouldSubmit = WorkbenchAdminManager.getCurrentAdminWorkbench(AtworkApplicationLike.baseContext)

    override fun convert(helper: WorkbenchAdminSwitchListItemViewHolder, item: WorkbenchData) {
        helper.tvName.text = item.getNameI18n(AtworkApplicationLike.baseContext)
        if (helper.tvName.text.isNotEmpty()) {
            helper.tvCover.text = helper.tvName.text.toString().substring(0, 1)
        } else {
            helper.tvCover.text = StringUtils.EMPTY
        }

        val tvCoverBg = helper.tvCover.background
        if(tvCoverBg is GradientDrawable) {
            when(helper.adapterPosition % 3) {
                1 -> tvCoverBg.setColor(Color.parseColor("#5F9E19"))
                2 -> tvCoverBg.setColor(Color.parseColor("#8952BA"))
                else -> tvCoverBg.setColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
            }

        }

        helper.tvDesc.text = item.remarks
        helper.tvDesc.isVisible = !StringUtils.isEmpty(helper.tvDesc.text.toString())



        if(getSelectWorkbenchId() == item.id) {
            helper.tvSelect.isVisible = true

        } else {
            helper.tvSelect.isInvisible = true
        }


    }

    fun getSelectWorkbenchId(): Long {
        if(-1L != selectIdShouldSubmit) {
            return selectIdShouldSubmit
        }

        return WorkbenchAdminManager.getCurrentAdminWorkbench(AtworkApplicationLike.baseContext)
    }

}

class WorkbenchAdminSwitchListItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val tvCover: TextView = itemView.findViewById(R.id.tvCover)
    val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
    val tvName: TextView = itemView.findViewById(R.id.tvName)
    val tvSelect: ImageView = itemView.findViewById(R.id.tvSelect)

}