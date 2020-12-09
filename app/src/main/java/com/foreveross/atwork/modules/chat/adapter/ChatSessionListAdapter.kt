package com.foreveross.atwork.modules.chat.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.modules.chat.component.SessionItemView
import java.util.*

class ChatSessionListAdapter(sessionList: MutableList<Session>): BaseQuickAdapter<Session, SessionItemHolder>(-1, sessionList) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): SessionItemHolder {
        val commonItemView = SessionItemView(context)
        val settingItemViewHolder = SessionItemHolder(commonItemView)

        return settingItemViewHolder
    }

    override fun convert(sessionItemHolder: SessionItemHolder, sessionItem: Session) {
        sessionItemHolder.sessionItemView.refresh(sessionItem)
    }

    override fun getItemId(position: Int): Long {

        return getItemOrNull(position - headerLayoutCount)?.identifier?.hashCode()?.toLong()?: position.toLong()
    }

}

class SessionItemHolder(itemView: View) : BaseViewHolder(itemView) {
    var sessionItemView: SessionItemView = itemView as SessionItemView
}