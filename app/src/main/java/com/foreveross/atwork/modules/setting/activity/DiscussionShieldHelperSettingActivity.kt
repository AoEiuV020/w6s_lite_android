package com.foreveross.atwork.modules.setting.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.setting.fragment.DiscussionShieldHelperSettingFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class DiscussionShieldHelperSettingActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return DiscussionShieldHelperSettingFragment()
    }


    companion object {

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, DiscussionShieldHelperSettingActivity::class.java)
        }

    }

}