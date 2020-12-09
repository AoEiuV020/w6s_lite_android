package com.foreveross.atwork.modules.newsSummary.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.newsSummary.fragment.NewsSummaryFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class NewsSummaryActivity: SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return NewsSummaryFragment()
    }

    companion object {
        const val LIST_COUNT = "LIST_COUNT"
        fun getIntent(context: Context): Intent {
            return Intent(context, NewsSummaryActivity::class.java)
        }
    }
}