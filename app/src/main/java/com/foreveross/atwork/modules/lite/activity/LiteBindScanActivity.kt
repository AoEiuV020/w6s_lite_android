package com.foreveross.atwork.modules.lite.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.utils.extension.putParcelableDirectly
import com.foreveross.atwork.modules.lite.fragment.LiteBindScanFragment
import com.foreveross.atwork.modules.lite.module.LiteBindConfig
import com.foreveross.atwork.support.SingleFragmentActivity

class LiteBindScanActivity: SingleFragmentActivity(){

    override fun createFragment(): Fragment  = LiteBindScanFragment()


    companion object {

        @JvmStatic
        fun getIntent(context: Context, liteBindConfig: LiteBindConfig): Intent {
            val intent = Intent(context, LiteBindScanActivity::class.java)
            intent.putParcelableDirectly(liteBindConfig)
            return intent
        }
    }
}