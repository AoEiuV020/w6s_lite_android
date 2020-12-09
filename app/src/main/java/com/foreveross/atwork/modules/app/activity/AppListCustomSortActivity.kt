package com.foreveross.atwork.modules.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper
import com.foreveross.atwork.R
import com.foreveross.atwork.modules.app.fragment.AppCustomSortFragment
import com.foreveross.atwork.support.SingleFragmentActivity


class AppListCustomSortActivity : SingleFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createFragment(): Fragment {
        return AppCustomSortFragment()
    }

    override fun changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, true)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, AppListCustomSortActivity::class.java)
        }
    }

}
