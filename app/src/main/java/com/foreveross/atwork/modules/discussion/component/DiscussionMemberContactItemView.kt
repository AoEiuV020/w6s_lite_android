package com.foreveross.atwork.modules.discussion.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil
import kotlinx.android.synthetic.main.item_view_discussion_member.view.*

class DiscussionMemberContactItemView : FrameLayout {

    constructor(context: Context) : super(context) {
        findViews()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_view_discussion_member, this)

    }

    fun refreshView(contact: ShowListItem, remove: Boolean) {

//        mShowContact = contact
        if (remove) {
            ivRemoveMember.visibility = View.VISIBLE
        } else {
            ivRemoveMember.visibility = View.GONE
        }
        ContactInfoViewUtil.dealWithContactInitializedStatus(ivAvatar, tvName, contact, true, true)
        tvName.visibility = View.VISIBLE
    }


    fun refreshLocal(@DrawableRes iconId: Int) {
        ivAvatar.setImageResource(iconId)
        tvName.visibility = View.INVISIBLE
        ivRemoveMember.visibility = View.GONE
    }
}