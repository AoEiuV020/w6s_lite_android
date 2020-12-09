package com.foreverht.workplus.module.chat.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreverht.workplus.module.chat.fragment.ShareLocationFragment
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2020-01-02
 */
class ShareLocationActivity : SingleFragmentActivity() {

    companion object {
        fun getIntent(context: Activity?): Intent {
            return Intent(context, ShareLocationActivity::class.java)
        }
    }

    private lateinit var shareLocationFragment: Fragment


    override fun createFragment(): Fragment {
        shareLocationFragment = ShareLocationFragment()
        return shareLocationFragment
    }

}