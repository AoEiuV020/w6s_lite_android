package com.foreveross.atwork.modules.discussion.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.modules.discussion.component.DiscussionTemplateItemView
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionTemplate


class DiscussionTemplatesAdapter(templates: MutableList<DiscussionTemplate>) : BaseQuickAdapter<DiscussionTemplate, DiscussionTemplatesAdapter.DiscussionTemplateViewHolder>(-1, templates) {


    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): DiscussionTemplateViewHolder {
        val itemView = DiscussionTemplateItemView(context)
        return DiscussionTemplateViewHolder(itemView)
    }

    override fun convert(helper: DiscussionTemplateViewHolder, item: DiscussionTemplate) {
        helper.templateView.refreshItem(item)
    }

    class DiscussionTemplateViewHolder (itemView: View) : BaseViewHolder(itemView) {
        var templateView: DiscussionTemplateItemView = itemView as DiscussionTemplateItemView
    }

}