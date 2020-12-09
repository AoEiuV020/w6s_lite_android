package com.foreverht.workplus.module.file_share.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreverht.workplus.module.file_share.fragment.FileShareResultFragment
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareFileResponseJson
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2019-09-02
 */
const val INTENT_KEY_SHARE_FILE_RESULT = "INTENT_KEY_SHARE_FILE_RESULT"
const val INTENT_KEY_IS_SHARE_FILE = "INTENT_KEY_IS_SHARE_FILE"
class FileShareResultActivity : SingleFragmentActivity() {

    companion object {
        fun startActivity(context: Context, result: ShareFileResponseJson.Result, isFileShare: Boolean) {
            val intent = Intent(context, FileShareResultActivity::class.java)
            intent.putExtra(INTENT_KEY_SHARE_FILE_RESULT, result)
            intent.putExtra(INTENT_KEY_IS_SHARE_FILE, isFileShare)
            context.startActivity(intent)
        }
    }

    override fun createFragment(): Fragment {
        val fragment = FileShareResultFragment()
        fragment.arguments = intent.extras
        return fragment
    }

}