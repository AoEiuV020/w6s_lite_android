package com.foreveross.atwork.modules.discussion.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.modules.discussion.component.DiscussionFeatureInManagerItemView


class DiscussionFeaturesInManagerAdapter(featureList: MutableList<DiscussionFeature>) : BaseQuickAdapter<DiscussionFeature, DiscussionFeaturesInManagerAdapter.DiscussionFeatureViewHolder>(-1, featureList), DraggableModule {


    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): DiscussionFeatureViewHolder {
        val itemView = DiscussionFeatureInManagerItemView(context)

        return DiscussionFeatureViewHolder(itemView)
    }

    override fun convert(helper: DiscussionFeatureViewHolder, item: DiscussionFeature) {
        helper.featureItemView.refreshItem(item)
    }

    class DiscussionFeatureViewHolder (itemView: View) : BaseViewHolder(itemView) {
        var featureItemView: DiscussionFeatureInManagerItemView = itemView as DiscussionFeatureInManagerItemView
    }

}