package com.foreveross.atwork.modules.dropbox.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.dropbox.fragment.DropboxShareSearchFragment
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2019-11-12
 */
class DropboxShareSearchActivity : SingleFragmentActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, DropboxShareSearchActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun createFragment(): Fragment {
        return DropboxShareSearchFragment()
    }


}