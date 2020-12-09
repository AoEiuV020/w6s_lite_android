package com.foreveross.atwork.modules.discussion.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberTagItemInfo
import com.foreveross.atwork.modules.discussion.model.IDiscussionMemberItemDisplay
import kotlinx.android.synthetic.main.item_view_discussion_member_tag_title.view.*

class DiscussionMemberTagItemTitleView: FrameLayout {

    constructor(context: Context) : super(context) {
        findViews()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_view_discussion_member_tag_title, this)

        ViewUtil.setWidth(clRoot, ScreenUtils.getScreenWidth(context))
    }

    fun refreshView(itemDisplay: IDiscussionMemberItemDisplay) {
        itemDisplay.asType<DiscussionMemberTagItemInfo>()?.let {
            tvName.text = it.title
            tvSum.text = it.sumLabel
        }
    }
}