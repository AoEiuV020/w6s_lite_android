package com.foreveross.atwork.modules.lite.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.lite.fragment.LiteBindEntryFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class LiteBindEntryActivity: SingleFragmentActivity(){

    override fun createFragment(): Fragment  = LiteBindEntryFragment()


    companion object {

        @JvmStatic
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, LiteBindEntryActivity::class.java)
            return intent
        }
    }
}