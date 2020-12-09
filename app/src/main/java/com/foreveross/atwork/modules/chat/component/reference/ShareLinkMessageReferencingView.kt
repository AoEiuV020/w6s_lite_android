package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IShareLinkChatView
import com.foreveross.atwork.modules.chat.presenter.ShareLinkChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_share_link.view.*
import kotlinx.android.synthetic.main.component_message_referencing_text.view.tvTitle

class ShareLinkMessageReferencingView(context: Context) : RefreshReferencingBasicView<ShareChatMessage>(context), IShareLinkChatView {


    lateinit var vRoot: View

    init {
        presenter = ShareLinkChatViewRefreshUIPresenter(this)
    }

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_share_link, this)
    }


    override fun titleView(): TextView = tvLinkTitle

    override fun summaryView(): TextView = tvLinkDesc

    override fun coverView(): ImageView = ivCover

    override fun getReferencingAuthorView(): TextView = tvTitle


}