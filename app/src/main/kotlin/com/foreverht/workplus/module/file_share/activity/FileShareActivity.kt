package com.foreverht.workplus.module.file_share.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreverht.workplus.module.file_share.FileShareAction
import com.foreverht.workplus.module.file_share.fragment.FileShareFragment
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2019-08-29
 */
const val INTENT_FILE_SHARE_ACTION = "INTENT_FILE_SHARE_ACTION"

class FileShareActivity : SingleFragmentActivity() {

    companion object {
        fun startActivity(context: Context, fileShareAction: FileShareAction?) {
            val intent = Intent(context, FileShareActivity::class.java)
            intent.putExtra(INTENT_FILE_SHARE_ACTION, fileShareAction)
            context.startActivity(intent)
        }
    }

    override fun createFragment(): Fragment {
        val fragment = FileShareFragment()
        fragment.arguments = intent.extras
        return fragment
    }

}