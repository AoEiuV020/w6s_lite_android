package com.foreveross.atwork.modules.group.adaptar

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.modules.contact.component.ContactListItemView

class RecentContactAdapter(sessionList: List<ShowListItem>) : BaseQuickAdapter<ShowListItem, RecentContactAdapter.ContactItemViewHolder>(sessionList) {

    var singleMode = true

    override fun convert(helper: ContactItemViewHolder?, item: ShowListItem?) {

        helper?.contactListItemView?.setSelectedMode(!singleMode)
        helper?.contactListItemView?.refreshSessionView(item)
    }



    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): ContactItemViewHolder {
        val contactListItemView = ContactListItemView(mContext)
        return ContactItemViewHolder(contactListItemView)
    }

    class ContactItemViewHolder(view: View) : BaseViewHolder(view) {

        var contactListItemView: ContactListItemView

        init {
            contactListItemView = view as ContactListItemView
        }


    }
}