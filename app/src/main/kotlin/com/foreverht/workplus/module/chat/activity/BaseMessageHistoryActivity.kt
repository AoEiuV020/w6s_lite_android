package com.foreverht.workplus.module.chat.activity

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.support.SingleFragmentActivity
import com.w6s.module.MessageTags
import kotlinx.android.parcel.Parcelize

abstract class BaseMessageHistoryActivity: SingleFragmentActivity() {
    companion object {

        const val DATA_MESSAGE_HISTORY_VIEW_ACTION = "DATA_MESSAGE_HISTORY_VIEW_ACTION"
        const val MESSAGE_HISTORY_TYPE = "MESSAGE_HISTORY_TYPE"
    }

    @Parcelize
    class MessageHistoryViewAction (

            var app: App,

            var selectedTag: MessageTags?

    ): Parcelable

}