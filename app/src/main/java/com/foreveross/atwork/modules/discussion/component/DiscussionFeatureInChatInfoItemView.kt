package com.foreveross.atwork.modules.discussion.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.infrastructure.utils.extension.ctxApp
import com.foreveross.atwork.infrastructure.utils.extension.getColorCompat
import com.foreveross.atwork.modules.discussion.util.refreshDiscussionFeatureIcon
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_chat_info.view.ivIcon
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_chat_info.view.tvName

class DiscussionFeatureInChatInfoItemView: RelativeLayout {

    constructor(context: Context) : super(context) {
        findViews()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_view_discussion_feature_entry_in_chat_info, this)

        setBackgroundResource(R.drawable.bg_item_common_selector)

    }

    fun refreshItem(discussionFeature: DiscussionFeature) {

        if(DiscussionFeature.ID_MORE == discussionFeature.id) {
            tvName.text = context.getString(R.string.show_more_discussion_apps)
            tvName.setTextColor(ctxApp.getColorCompat(R.color.alarm_text))
            return
        }

        tvName.text = discussionFeature.name
        tvName.setTextColor(ctxApp.getColorCompat(R.color.common_list_item_left))

        refreshDiscussionFeatureIcon(ivIcon, discussionFeature)

    }
}