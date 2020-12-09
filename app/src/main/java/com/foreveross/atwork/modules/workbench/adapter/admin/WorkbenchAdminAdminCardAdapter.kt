package com.foreveross.atwork.modules.workbench.adapter.admin

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.component.IWorkbenchCardRefreshView
import com.foreveross.atwork.modules.workbench.component.admin.WorkbenchAdminAdminContentCardView


class WorkbenchAdminAdminCardAdapter(var workbenchData: WorkbenchData?, dataList: MutableList<WorkbenchCard>) : BaseQuickAdapter<WorkbenchCard, WorkbenchAdminAdminContentCardItemViewHolder>(dataList) {

    var onAdminActionListener: ((position: Int, view: View, event: MotionEvent)-> Unit)? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchAdminAdminContentCardItemViewHolder {

        val workbenchAdminAdminContentCardItemViewHolder = WorkbenchAdminAdminContentCardItemViewHolder(WorkbenchAdminAdminContentCardView(mContext, viewType))

        val gestureDetector = GestureDetector(mContext, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                onAdminActionListener?.invoke(workbenchAdminAdminContentCardItemViewHolder.adapterPosition, workbenchAdminAdminContentCardItemViewHolder.cardItemView.ivAdminAction,  event)
                return true
            }
        })

        workbenchAdminAdminContentCardItemViewHolder.cardItemView.ivAdminAction.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            return@setOnTouchListener true


        }
        return workbenchAdminAdminContentCardItemViewHolder
    }





    override fun getDefItemViewType(position: Int): Int {
        val msg = getItem(position)
        msg?.let {
            return it.type.hashCode()
        }

        return WorkbenchCardType.UNKNOWN.hashCode()
    }


    override fun convert(helper: WorkbenchAdminAdminContentCardItemViewHolder, item: WorkbenchCard) {
        val cardView = helper.cardItemView.cardView

        (cardView as IWorkbenchCardRefreshView<WorkbenchCard>).refresh(item)

        workbenchData?.let {
            (cardView as IWorkbenchCardRefreshView<WorkbenchCard>).refreshWorkbenchData(it)
        }
    }


}

class WorkbenchAdminAdminContentCardItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView: WorkbenchAdminAdminContentCardView = itemView as WorkbenchAdminAdminContentCardView
}