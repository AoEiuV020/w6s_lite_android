package com.foreveross.atwork.modules.discussion.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.R
import com.foreveross.atwork.modules.common.component.CommonItemView
import com.foreveross.atwork.modules.discussion.model.DiscussionManagerModule

class DiscussionManagerModulesAdapter(dataList: MutableList<DiscussionManagerModule>): BaseQuickAdapter<DiscussionManagerModule, DiscussionManagerModulesAdapter.DiscussionModuleViewHolder>(-1, dataList) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): DiscussionModuleViewHolder {
        val itemView = CommonItemView(context)
        return DiscussionModuleViewHolder(itemView)
    }

    override fun convert(holder: DiscussionModuleViewHolder, item: DiscussionManagerModule) {
        when(item) {
            DiscussionManagerModule.TAG -> holder.moduleItemView.setCommonName(context.getString(R.string.discussion_member_tag_manager))
            DiscussionManagerModule.ENTRY -> holder.moduleItemView.setCommonName(context.getString(R.string.discussion_app_manager))
        }
    }

    class DiscussionModuleViewHolder (itemView: View) : BaseViewHolder(itemView) {
        var moduleItemView: CommonItemView = itemView as CommonItemView
    }


}