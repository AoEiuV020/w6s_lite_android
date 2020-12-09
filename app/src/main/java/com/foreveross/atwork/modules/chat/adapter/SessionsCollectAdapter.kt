package com.foreveross.atwork.modules.chat.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.modules.chat.component.SessionItemView

class SessionsCollectAdapter(dataList: MutableList<Session>): BaseQuickAdapter<Session, SessionsCollectAdapter.CollectSessionItemViewHolder>(dataList) {


    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): CollectSessionItemViewHolder {
        return CollectSessionItemViewHolder(SessionItemView(mContext))
    }

    override fun convert(helper: CollectSessionItemViewHolder, item: Session) {
        helper.sessionItemView.refresh(item)
    }


    class CollectSessionItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var sessionItemView: SessionItemView = itemView as SessionItemView

    }

}