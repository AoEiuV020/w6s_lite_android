package com.foreveross.atwork.modules.workbench.adapter.admin.provider

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.utils.AppIconHelper

class WorkbenchAdminAppEntryChildNodeProvider: WorkbenchAdminAppEntryNodeProvider() {

    override val itemViewType: Int = 1

    override val layoutId: Int = R.layout.component_workbench_admin_item_app_entry_child

    init {
        addChildClickViewIds(R.id.tvDesc)
    }


    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val childNode = item as AppEntryChildNode

        helper.setText(R.id.tvName, childNode.appEntry.getTitleI18n(BaseApplicationLike.baseContext))
        AppIconHelper.setAppIcon(context, childNode.appEntry, helper.getView(R.id.ivAppCover), true)
        helper.getView<ImageView>(R.id.ivSelect).isVisible =  childNode.select
        helper.getView<ImageView>(R.id.tvDesc).isVisible = shouldShowSetScopeDesc(childNode)

    }

    override fun onChildClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        when (view.id) {
            R.id.tvDesc -> routeAdminAppModifyUrl(data)
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        handleClick(data)
    }



    private fun shouldShowSetScopeDesc(parentNode: AppEntryChildNode) = parentNode.isScopeEmpty() ?: false

}