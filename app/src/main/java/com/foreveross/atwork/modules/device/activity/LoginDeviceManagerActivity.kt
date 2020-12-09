package com.foreveross.atwork.modules.device.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.device.fragment.LoginDeviceManagerFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class LoginDeviceManagerActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return LoginDeviceManagerFragment()
    }

    companion object {


        @JvmStatic
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, LoginDeviceManagerActivity::class.java)
            return intent
        }

    }

}