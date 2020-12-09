package com.foreveross.atwork.modules.discussion.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.modules.discussion.component.DiscussionFeatureInManagerItemView
import com.foreveross.atwork.modules.discussion.component.DiscussionFeatureInSelectAppItemView


class DiscussionFeaturesInSelectAppAdapter(featureList: MutableList<DiscussionFeature>) : BaseQuickAdapter<DiscussionFeature, DiscussionFeaturesInSelectAppAdapter.DiscussionFeatureViewHolder>(-1, featureList) {

    var selectFeature: DiscussionFeature? = null

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): DiscussionFeatureViewHolder {
        val itemView = DiscussionFeatureInSelectAppItemView(context)

        return DiscussionFeatureViewHolder(itemView)
    }

    override fun convert(helper: DiscussionFeatureViewHolder, item: DiscussionFeature) {
        helper.featureItemView.refreshItem(item, selectFeature)
    }

    class DiscussionFeatureViewHolder (itemView: View) : BaseViewHolder(itemView) {
        var featureItemView: DiscussionFeatureInSelectAppItemView = itemView as DiscussionFeatureInSelectAppItemView
    }

}