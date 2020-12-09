package com.foreveross.atwork.modules.device.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo
import com.foreveross.atwork.modules.device.fragment.LoginDeviceModifyInfoFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class LoginDeviceModifyInfoActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return LoginDeviceModifyInfoFragment()
    }


    companion object {

        const val DATA_LOGIN_DEVICE_DETAIL = "DATA_LOGIN_DEVICE_DETAIL"

        @JvmStatic
        fun getIntent(context: Context, loginDeviceInfo: LoginDeviceInfo): Intent {
            val intent = Intent(context, LoginDeviceModifyInfoActivity::class.java)
            intent.putExtra(LoginDeviceDetailActivity.DATA_LOGIN_DEVICE_DETAIL, loginDeviceInfo)
            return intent
        }

    }

}