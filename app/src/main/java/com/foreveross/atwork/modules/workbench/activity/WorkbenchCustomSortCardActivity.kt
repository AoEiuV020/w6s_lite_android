package com.foreveross.atwork.modules.workbench.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.workbench.fragment.WorkbenchCustomSortCardFragment
import com.foreveross.atwork.support.SingleFragmentActivity


class WorkbenchCustomSortCardActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return WorkbenchCustomSortCardFragment()
    }


    companion object {

        val DATA_MODE = "DATA_MODE"

        fun getIntent(context: Context, mode: Int): Intent {
            val intent = Intent(context, WorkbenchCustomSortCardActivity::class.java)
            intent.putExtra(DATA_MODE, mode)
            return intent
        }
    }

}