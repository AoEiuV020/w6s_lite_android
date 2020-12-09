package com.foreveross.atwork.modules.device.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult
import com.foreveross.atwork.modules.device.fragment.LoginDeviceAuthFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class LoginDeviceAuthActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return LoginDeviceAuthFragment()
    }


    companion object {

        const val DATA_LOGIN_DEVICE_NEED_AUTH_RESULT = "DATA_LOGIN_DEVICE_NEED_AUTH_RESULT"

        @JvmStatic
        fun getIntent(context: Context, loginDeviceNeedAuthResult: LoginDeviceNeedAuthResult): Intent {
            val intent = Intent(context, LoginDeviceAuthActivity::class.java)
            intent.putExtra(DATA_LOGIN_DEVICE_NEED_AUTH_RESULT, loginDeviceNeedAuthResult)
            return intent
        }

    }

}