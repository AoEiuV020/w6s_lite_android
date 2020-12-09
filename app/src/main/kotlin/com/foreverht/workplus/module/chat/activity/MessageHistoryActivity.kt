package com.foreverht.workplus.module.chat.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.chat.fragment.MessageHistoryFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class MessageHistoryActivity: SingleFragmentActivity() {


    override fun createFragment(): Fragment {

        return MessageHistoryFragment()
    }

    companion object {

        const val DATA_MESSAGE_HISTORY_VIEW_ACTION = "DATA_MESSAGE_HISTORY_VIEW_ACTION"

        fun getIntent(ctx: Context, messageHistoryViewAction: BaseMessageHistoryActivity.MessageHistoryViewAction): Intent {
            val intent = Intent(ctx, MessageHistoryActivity::class.java)
            intent.putExtra(DATA_MESSAGE_HISTORY_VIEW_ACTION, messageHistoryViewAction)

            return intent
        }
    }
}
