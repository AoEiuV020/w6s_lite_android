package com.foreveross.atwork.modules.discussion.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.modules.discussion.util.refreshDiscussionFeatureIcon
import com.foreveross.atwork.utils.ImageCacheHelper
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_chat_detail.view.*

class DiscussionFeatureInChatDetailItemView: LinearLayout {

    constructor(context: Context) : super(context) {
        findViews()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_view_discussion_feature_entry_in_chat_detail, this)

    }

    fun refreshItem(discussionFeature: DiscussionFeature) {
        if(DiscussionFeature.ID_MORE == discussionFeature.id) {
            tvName.text = context.getString(R.string.more)
            ImageCacheHelper.setImageResource(ivIcon, R.mipmap.icon_feature_more)
            return
        }


        tvName.text = discussionFeature.name

        refreshDiscussionFeatureIcon(ivIcon, discussionFeature)
    }
}