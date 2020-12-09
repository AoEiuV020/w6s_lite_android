package com.foreveross.atwork.modules.discussion.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.discussion.util.refreshDiscussionFeatureIcon
import com.foreveross.atwork.utils.ImageCacheHelper
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_chat_detail.view.*
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_manager.view.*
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_manager.view.ivIcon
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_manager.view.tvName

class DiscussionFeatureInManagerItemView: FrameLayout {

    constructor(context: Context) : super(context) {
        findViews()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_view_discussion_feature_entry_in_manager, this)

        ViewUtil.setWidth(clRoot, ScreenUtils.getScreenWidth(context))


    }

    fun refreshItem(discussionFeature: DiscussionFeature) {
        tvName.text = discussionFeature.name

        refreshDiscussionFeatureIcon(ivIcon, discussionFeature)

    }
}