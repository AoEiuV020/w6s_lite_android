package com.foreveross.atwork.modules.chat.component.reference

import android.widget.TextView
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.manager.model.SetReadableNameParams
import com.foreveross.atwork.utils.ContactShowNameHelper

interface IRefreshReferencingView<T : ChatPostMessage> {

     fun getReferencingAuthorView(): TextView

     fun refreshUI(message: T) {
        val setReadableNameParams = SetReadableNameParams.newSetReadableNameParams()
                .setDiscussionId(message.to)
                .setTextView(getReferencingAuthorView())
                .setUserId(message.from)
                .setDomainId(message.mFromDomain)

        ContactShowNameHelper.setReadableNames(setReadableNameParams)
    }
}