package com.foreverht.workplus.module.chat.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreverht.workplus.module.chat.fragment.MessageByTagFragment

class MessageByTagActivity: BaseMessageHistoryActivity() {

    companion object {
        fun getIntent(context: Context,  messageHistoryViewAction: MessageHistoryViewAction): Intent {
            val intent = Intent(context, MessageByTagActivity::class.java)
            intent.putExtra(DATA_MESSAGE_HISTORY_VIEW_ACTION, messageHistoryViewAction)
            return intent
        }
    }

    override fun createFragment(): Fragment {
        return MessageByTagFragment()
    }
}