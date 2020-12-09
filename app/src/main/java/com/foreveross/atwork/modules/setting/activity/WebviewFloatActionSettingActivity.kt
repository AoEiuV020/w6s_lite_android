package com.foreveross.atwork.modules.setting.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.setting.fragment.WebviewFloatActionSettingFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class WebviewFloatActionSettingActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return WebviewFloatActionSettingFragment()
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, WebviewFloatActionSettingActivity::class.java)
        }
    }
}