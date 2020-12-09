package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminSwitchFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminSwitchActivity: SingleFragmentTransparentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun createFragment(): Fragment {
        return WorkbenchAdminSwitchFragment()
    }


    companion object {

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, WorkbenchAdminSwitchActivity::class.java)
            return intent
        }
    }

}