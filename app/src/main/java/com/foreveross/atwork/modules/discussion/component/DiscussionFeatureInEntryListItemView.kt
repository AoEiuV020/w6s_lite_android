package com.foreveross.atwork.modules.discussion.component

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.graphics.toColorLong
import androidx.core.view.isVisible
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.infrastructure.utils.extension.*
import com.foreveross.atwork.modules.discussion.util.refreshDiscussionFeatureIcon
import kotlinx.android.synthetic.main.item_view_discussion_feature_entry_in_entry_list.view.*

class DiscussionFeatureInEntryListItemView: LinearLayout {

    constructor(context: Context) : super(context) {
        findViews()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_view_discussion_feature_entry_in_entry_list, this)

    }

    fun refreshItem(discussionFeature: DiscussionFeature) {

        tvName.text = discussionFeature.name
        tvDesc.text = discussionFeature.definitions?.desc
        tvDesc.isVisible = tvDesc.hasText()

        refreshDiscussionFeatureIcon(ivIcon, discussionFeature)

        tvRoute.background.asType<GradientDrawable>()?.apply {
            mutate()
            cornerRadius = 180f.dp2px.toFloat()
            setStroke(0.8f.dp2px, ctxApp.getColorCompat(R.color.common_blue_bg))
        }
    }
}