package com.foreveross.atwork.modules.chat.component.anno

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.file.FileData
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IFileChatView
import com.foreveross.atwork.modules.chat.presenter.FileChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_anno_file_holding.view.*
import kotlinx.android.synthetic.main.component_message_referencing_file.view.ivCover
import kotlinx.android.synthetic.main.component_message_referencing_file.view.tvName

class AnnoFileDataHoldingView(context: Context) : FrameLayout(context), IFileChatView {


    lateinit var vRoot: View
    var presenter: FileChatViewRefreshUIPresenter = FileChatViewRefreshUIPresenter(this)

    init {
        findViews(context)
    }

    private fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_anno_file_holding, this)
    }

    fun refreshUI(fileData: FileData) {
        val fakeFileMessage = FileTransferChatMessage.getFIleTransferChatMessageFromFileData(fileData)
        presenter.refreshItemView(fakeFileMessage)
    }



    override fun titleView(): TextView = tvName

    override fun coverView(): ImageView = ivCover

    override fun sizeView(): TextView? = tvSize

}