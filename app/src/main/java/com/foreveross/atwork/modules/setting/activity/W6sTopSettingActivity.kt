package com.foreveross.atwork.modules.setting.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.setting.fragment.W6sTopSettingFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class W6sTopSettingActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return W6sTopSettingFragment()
    }

    companion object {

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, W6sTopSettingActivity::class.java)
        }

    }
}