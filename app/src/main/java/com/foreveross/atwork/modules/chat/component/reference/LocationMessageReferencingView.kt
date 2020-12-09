package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.ILocationChatView
import com.foreveross.atwork.modules.chat.presenter.LocationChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_location.view.*


class LocationMessageReferencingView(context: Context) : RefreshReferencingBasicView<ShareChatMessage>(context), ILocationChatView {


    lateinit var vRoot: View

    init {
        presenter = LocationChatViewRefreshUIPresenter(this)
    }

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_location, this)
    }

    override fun getReferencingAuthorView(): TextView = tvTitle

    override fun descView(): TextView = tvDesc

    override fun coverView(): ImageView = ivCover

}