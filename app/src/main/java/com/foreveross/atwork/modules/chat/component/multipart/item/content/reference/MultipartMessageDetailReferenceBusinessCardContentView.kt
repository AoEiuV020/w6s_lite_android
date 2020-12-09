package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IBusinessCardChatView
import com.foreveross.atwork.modules.chat.presenter.BusinessCardChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_business_card.view.*

class MultipartMessageDetailReferenceBusinessCardContentView(context: Context) : MultipartMessageDetailReferenceContentView<ShareChatMessage>(context), IBusinessCardChatView {



    init {
        chatViewRefreshUIPresenter = BusinessCardChatViewRefreshUIPresenter(this)
    }


    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply



    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_business_card, this)

    }


    override fun coverView(): ImageView = ivCover

    override fun titleView(): TextView = tvName

    override fun genderView(): ImageView = ivGender

    override fun jobTitleView(): TextView = tvJobTitle

    override fun signatureView(): TextView? = tvSignature


}