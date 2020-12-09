package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IBusinessCardChatView
import com.foreveross.atwork.modules.chat.presenter.BusinessCardChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_business_card.view.*
import kotlinx.android.synthetic.main.component_message_referencing_file.view.*
import kotlinx.android.synthetic.main.component_message_referencing_file.view.ivCover
import kotlinx.android.synthetic.main.component_message_referencing_file.view.tvName
import kotlinx.android.synthetic.main.component_message_referencing_text.view.tvTitle

class BusinessCardMessageReferencingView(context: Context) : RefreshReferencingBasicView<ShareChatMessage>(context), IBusinessCardChatView {


    lateinit var vRoot: View

    init {
        presenter = BusinessCardChatViewRefreshUIPresenter(this)
    }

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_business_card, this)
    }

    override fun getReferencingAuthorView(): TextView = tvTitle


    override fun coverView(): ImageView = ivCover

    override fun titleView(): TextView = tvName

    override fun genderView(): ImageView = ivGender

    override fun jobTitleView(): TextView = tvJobTitle

    override fun signatureView(): TextView? = null



}