package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IVoiceChatView
import com.foreveross.atwork.modules.chat.presenter.VoiceChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_voice.view.*

class MultipartMessageDetailReferenceVoiceContentView(context: Context) : MultipartMessageDetailReferenceContentView<VoiceChatMessage>(context), IVoiceChatView {



    init {
        chatViewRefreshUIPresenter = VoiceChatViewRefreshUIPresenter(context, this)
    }


    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun voiceBgFlView(): FrameLayout = flVoiceBg

    override fun voiceView(): ImageView = ivVoice

    override fun durationView(): TextView = tvDuration

    fun getPresenter() : VoiceChatViewRefreshUIPresenter = chatViewRefreshUIPresenter as VoiceChatViewRefreshUIPresenter

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_voice, this)

    }


}