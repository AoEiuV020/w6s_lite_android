package com.foreveross.atwork.modules.setting.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.setting.fragment.EmpIncomingCallSettingFragment
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2019-08-21
 */
class EmpIncomingCallSettingActivity: SingleFragmentActivity() {

    companion object {
        @JvmStatic
        fun startActivity(context: Context) {
            val intent = Intent(context, EmpIncomingCallSettingActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun createFragment(): Fragment {
        return EmpIncomingCallSettingFragment()
    }


}