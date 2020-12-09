package com.foreverht.workplus.module.chat.activity

import android.content.Context
import android.content.Intent
import com.foreverht.workplus.module.chat.SearchMessageType
import com.foreverht.workplus.module.chat.fragment.MessageByTypeFragment

class MessageByTypeActivity: BaseMessageHistoryActivity() {

    companion object {
        fun getIntent(context: Context, type: SearchMessageType, messageHistoryViewAction: MessageHistoryViewAction?): Intent {
            val intent = Intent(context, MessageByTypeActivity::class.java)
            intent.putExtra(MESSAGE_HISTORY_TYPE, type)
            intent.putExtra(DATA_MESSAGE_HISTORY_VIEW_ACTION, messageHistoryViewAction)
            return intent
        }
    }

    override fun createFragment(): MessageByTypeFragment {
        return MessageByTypeFragment()
    }


}