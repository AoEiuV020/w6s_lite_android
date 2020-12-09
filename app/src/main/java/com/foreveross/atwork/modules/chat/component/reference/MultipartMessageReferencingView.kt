package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IMultipartChatView
import com.foreveross.atwork.modules.chat.presenter.MultipartChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_multipart.view.*
import kotlinx.android.synthetic.main.component_message_referencing_text.view.tvTitle

class MultipartMessageReferencingView(context: Context) : RefreshReferencingBasicView<MultipartChatMessage>(context), IMultipartChatView {


    lateinit var vRoot: View

    init {
        presenter = MultipartChatViewRefreshUIPresenter(this)
    }

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_multipart, this)
    }


    override fun titleView(): TextView = tvMultipartTitle

    override fun descView(): TextView = tvDesc

    override fun getReferencingAuthorView(): TextView = tvTitle


}