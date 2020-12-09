package com.foreveross.atwork.modules.newsSummary.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.newsSummary.fragment.NewsSummarySearchFragment
import com.foreveross.atwork.modules.search.model.NewSearchControlAction
import com.foreveross.atwork.support.SingleFragmentActivity

class NewsSummarySearchActivity : SingleFragmentActivity() {

    companion object {
        val DATA_NEW_SEARCH_CONTROL_ACTION = "DATA_NEW_SEARCH_CONTROL_ACTION"

        val DATA_SEARCH_SELECT_RESULT = "DATA_SEARCH_SELECT_RESULT"

        fun getIntent(context: Context?, searchControlAction: NewSearchControlAction?): Intent? {
            val intent = Intent(context, NewsSummarySearchActivity::class.java)
            intent.putExtra(DATA_NEW_SEARCH_CONTROL_ACTION, searchControlAction)
            return intent
        }
    }

    override fun createFragment(): Fragment? {
        return NewsSummarySearchFragment()
    }
}