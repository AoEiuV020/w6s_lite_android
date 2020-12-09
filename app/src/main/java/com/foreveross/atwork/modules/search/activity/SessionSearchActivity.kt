package com.foreveross.atwork.modules.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.search.fragment.SessionSearchFragment
import com.foreveross.atwork.modules.search.model.SearchMessageItem
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2020-02-05
 */
const val INTENT_KEY_SEARCH_MESSAGE_ITEMS = "INTENT_KEY_SEARCH_MESSAGE_ITEMS"
const val INTENT_KEY_SEARCH_MESSAGE_KEY = "INTENT_KEY_SEARCH_MESSAGE_KEY"

class SessionSearchActivity : SingleFragmentActivity() {

    private  var bundle: Bundle? = null

    companion object{
        fun startActivity(context: Context, searchKey: String, messageItems: ArrayList<SearchMessageItem> ) {

            val intent = Intent(context, SessionSearchActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList(INTENT_KEY_SEARCH_MESSAGE_ITEMS, messageItems)
            bundle.putString(INTENT_KEY_SEARCH_MESSAGE_KEY, searchKey)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        bundle = intent.extras
        super.onCreate(savedInstanceState)
    }

    override fun createFragment(): Fragment {
        val fragment = SessionSearchFragment()
        fragment.arguments = bundle
        return fragment
    }

}