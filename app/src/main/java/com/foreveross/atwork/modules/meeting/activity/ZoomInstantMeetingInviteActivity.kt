package com.foreveross.atwork.modules.meeting.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.meeting.fragment.ZoomInstantMeetingInviteFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class ZoomInstantMeetingInviteActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return ZoomInstantMeetingInviteFragment()
    }

    companion object {

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, ZoomInstantMeetingInviteActivity::class.java)
        }

    }
}