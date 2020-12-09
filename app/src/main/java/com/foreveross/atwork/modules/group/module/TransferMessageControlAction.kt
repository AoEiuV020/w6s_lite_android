package com.foreveross.atwork.modules.group.module

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransferMessageControlAction(

        var sendMode: TransferMessageMode = TransferMessageMode.FORWARD,

        var sendMessageList: List<ChatPostMessage>? = null,

        var isNeedCreateDiscussion: Boolean = false,

        override var max: Int = -1

): SelectToHandleAction(max) {

    override fun getActionIntent(context: Context): Intent {
        return TransferMessageActivity.getIntent(context, this)
    }
}