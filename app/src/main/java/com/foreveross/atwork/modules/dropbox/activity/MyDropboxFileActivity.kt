package com.foreveross.atwork.modules.dropbox.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment
import com.foreveross.atwork.support.AtWorkFragmentManager

/**
 *  create by reyzhang22 at 2019-11-12
 */
class MyDropboxFileActivity : DropboxBaseActivity() {

    companion object {
        fun startActivity(context: Context, sourceType: Dropbox.SourceType, sourceId: String, domainId: String) {
            val intent = Intent(context, MyDropboxFileActivity::class.java)
            intent.putExtra(KEY_INTENT_SOURCE_TYPE, sourceType)
            intent.putExtra(KEY_INTENT_SOURCE_ID, sourceId)
            intent.putExtra(KEY_INTENT_DOMAIN_ID, domainId)
            intent.putExtra(KEY_INTENT_TITLE, context.getString(R.string.my_file))
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment()
        hideBottomLayout()
    }

    protected fun initFragment() {
        mFragmentManager = AtWorkFragmentManager(this, R.id.dropbox_layout)
        if (mUserDropboxFragment == null) {
            mUserDropboxFragment = UserDropboxFragment()
        }
        mCurrentFragment = mUserDropboxFragment
        mFragmentManager.addFragmentAndAdd2BackStack(mCurrentFragment, UserDropboxFragment::class.java.simpleName)
    }


    override fun onBackPressed() {
        finish()
    }
}