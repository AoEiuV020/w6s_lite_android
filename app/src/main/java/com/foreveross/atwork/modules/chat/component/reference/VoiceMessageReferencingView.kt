package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IVoiceChatView
import com.foreveross.atwork.modules.chat.presenter.VoiceChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_text.view.tvTitle
import kotlinx.android.synthetic.main.component_message_referencing_voice.view.*

class VoiceMessageReferencingView(context: Context) : RefreshReferencingBasicView<VoiceChatMessage>(context), IVoiceChatView {


    init {
        presenter = VoiceChatViewRefreshUIPresenter(context, this)
    }

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_voice, this)
    }

    override fun getReferencingAuthorView(): TextView = tvTitle


    override fun refreshUI(message: VoiceChatMessage) {
        super.refreshUI(message)

    }


    override fun voiceBgFlView(): FrameLayout = flVoiceBg

    override fun voiceView(): ImageView = ivVoice

    override fun durationView(): TextView = tvDuration

}