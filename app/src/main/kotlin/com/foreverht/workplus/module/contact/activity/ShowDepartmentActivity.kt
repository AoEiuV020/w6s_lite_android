package com.foreverht.workplus.module.contact.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreverht.workplus.module.contact.fragment.ShowDepartmentFragment
import com.foreveross.atwork.infrastructure.model.orgization.Department
import com.foreveross.atwork.support.SingleFragmentActivity

const val KEY_DEPARTMENT = "KEY_DEPARTMENT"

class ShowDepartmentActivity: SingleFragmentActivity() {

    companion object {
        @JvmStatic
        fun startActivity(activity: Activity, department: Department) {
            val intent = Intent(activity, ShowDepartmentActivity::class.java)
            intent.putExtra(KEY_DEPARTMENT, department)
            activity.startActivity(intent)
        }
    }

    override fun createFragment(): Fragment {
        val fragment = ShowDepartmentFragment()
        fragment.arguments = intent.extras
        return fragment
    }

}