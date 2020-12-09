package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IMicroVideoChatView
import com.foreveross.atwork.modules.chat.presenter.MicroVideoChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_micro_video.view.*


class MicroVideoMessageReferencingView(context: Context) : RefreshReferencingBasicView<MicroVideoChatMessage>(context), IMicroVideoChatView {


    lateinit var vRoot: View

    init {
        presenter = MicroVideoChatViewRefreshUIPresenter(context, this)
    }

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_micro_video, this)
    }



    override fun coverView(): ImageView = ivVideoCover

    override fun getReferencingAuthorView(): TextView = tvTitle


}