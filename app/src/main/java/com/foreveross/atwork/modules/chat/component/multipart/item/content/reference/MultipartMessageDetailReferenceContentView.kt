package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.widget.TextView
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.manager.model.SetReadableNameParams
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailBasicContentView
import com.foreveross.atwork.modules.chat.presenter.IChatViewRefreshUIPresenter
import com.foreveross.atwork.utils.ContactShowNameHelper

abstract class MultipartMessageDetailReferenceContentView<T: ChatPostMessage>(context: Context): MultipartMessageDetailBasicContentView<ReferenceMessage>(context) {

    protected var chatViewRefreshUIPresenter: IChatViewRefreshUIPresenter<T>? = null

    abstract fun authorNameView(): TextView

    abstract fun replyView(): TextView

    override fun refreshUI(message: ReferenceMessage) {
        setAuthorNameView(message)
        replyView().text = message.mReply

        chatViewRefreshUIPresenter?.refreshItemView(message.mReferencingMessage as T)
    }

    private fun setAuthorNameView(message: ReferenceMessage) {
        val setReadableNameParams = SetReadableNameParams.newSetReadableNameParams()
                .setDiscussionId(message.mReferencingMessage.to)
                .setTextView(authorNameView())
                .setUserId(message.mReferencingMessage.from)
                .setDomainId(message.mReferencingMessage.mFromDomain)

        ContactShowNameHelper.setReadableNames(setReadableNameParams)

    }

}