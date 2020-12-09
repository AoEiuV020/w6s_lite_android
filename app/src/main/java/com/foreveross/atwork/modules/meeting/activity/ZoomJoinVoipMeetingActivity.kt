package com.foreveross.atwork.modules.meeting.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.meeting.fragment.ZoomJoinVoipMeetingFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class ZoomJoinVoipMeetingActivity: SingleFragmentActivity() {
    override fun createFragment(): Fragment = ZoomJoinVoipMeetingFragment()

    companion object {

        @JvmStatic
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, ZoomJoinVoipMeetingActivity::class.java)
            return intent
        }

    }
}