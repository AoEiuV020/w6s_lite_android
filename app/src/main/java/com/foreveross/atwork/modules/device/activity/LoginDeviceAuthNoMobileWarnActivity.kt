package com.foreveross.atwork.modules.device.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.device.fragment.LoginDeviceAuthNoMobileWarnFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class LoginDeviceAuthNoMobileWarnActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return LoginDeviceAuthNoMobileWarnFragment()
    }

    companion object {


        @JvmStatic
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, LoginDeviceAuthNoMobileWarnActivity::class.java)
            return intent
        }

    }


}