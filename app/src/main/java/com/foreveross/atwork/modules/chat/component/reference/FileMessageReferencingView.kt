package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IFileChatView
import com.foreveross.atwork.modules.chat.presenter.FileChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_file.view.*
import kotlinx.android.synthetic.main.component_message_referencing_text.view.tvTitle

class FileMessageReferencingView(context: Context) : RefreshReferencingBasicView<FileTransferChatMessage>(context), IFileChatView {


    lateinit var vRoot: View

    init {
        presenter = FileChatViewRefreshUIPresenter(this)
    }

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_file, this)
    }

    override fun getReferencingAuthorView(): TextView = tvTitle

    override fun titleView(): TextView = tvName

    override fun coverView(): ImageView = ivCover

}