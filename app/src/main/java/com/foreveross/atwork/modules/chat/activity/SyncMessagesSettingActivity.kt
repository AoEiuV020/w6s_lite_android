package com.foreveross.atwork.modules.chat.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.chat.fragment.SyncMessagesSettingFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class SyncMessagesSettingActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return SyncMessagesSettingFragment()
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, SyncMessagesSettingActivity::class.java)
        }
    }
}