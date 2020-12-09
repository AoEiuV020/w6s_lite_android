package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.ITextChatView
import com.foreveross.atwork.modules.chat.presenter.TextChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_text.view.*

class TextMessageReferencingView(context: Context) : RefreshReferencingBasicView<ChatPostMessage>(context), ITextChatView {


    init {
        presenter = TextChatViewRefreshUIPresenter(this)
    }

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_text, this)
    }

    override fun getReferencingAuthorView(): TextView = tvTitle


    override fun refreshUI(message: ChatPostMessage) {
        super.refreshUI(message)

    }

    override fun contentView(): TextView  = tvContent
}