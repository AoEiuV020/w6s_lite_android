package com.foreveross.atwork.modules.workbench.adapter.admin

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.utils.StringUtils


class WorkbenchAdminAddCardListAdapter(dataList: List<WorkbenchCardType>): BaseQuickAdapter<WorkbenchCardType, WorkbenchAdminAddCardListItemViewHolder>(R.layout.component_workbench_item_card_admin_add, dataList) {

    var selectCardPos = 0

    override fun convert(helper: WorkbenchAdminAddCardListItemViewHolder, item: WorkbenchCardType) {
        helper.tvName.text = getCardName(mContext, item)

        helper.ivSelect.isVisible = selectCardPos == helper.adapterPosition
    }




    companion object {
        fun getCardName(context: Context, item: WorkbenchCardType):String = when (item) {
            WorkbenchCardType.SEARCH -> context.getString(R.string.admin_add_card_name_search)
            WorkbenchCardType.BANNER -> context.getString(R.string.admin_add_card_name_banner)
            WorkbenchCardType.APP_MESSAGES -> context.getString(R.string.admin_add_card_name_summery)
            WorkbenchCardType.APP_CONTAINER_0 -> context.getString(R.string.admin_add_card_name_app_container_0)
            WorkbenchCardType.APP_CONTAINER_1 -> context.getString(R.string.admin_add_card_name_app_container_1)
            WorkbenchCardType.COMMON_APP -> context.getString(R.string.admin_add_card_name_app_common)
            else -> StringUtils.EMPTY
        }

    }

}

class WorkbenchAdminAddCardListItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val ivPreview: ImageView = itemView.findViewById(R.id.ivPreview)
    val tvName: TextView = itemView.findViewById(R.id.tvCardName)
    val ivSelect: ImageView = itemView.findViewById(R.id.tvSelect)

}