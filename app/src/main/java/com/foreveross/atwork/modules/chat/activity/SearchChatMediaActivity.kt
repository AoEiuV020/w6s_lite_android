package com.foreveross.atwork.modules.chat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.chat.fragment.SearchChatMediaFragment
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2020-02-11
 */
class SearchChatMediaActivity : SingleFragmentActivity() {

    var bundle: Bundle? = null

    companion object {
        fun startActivity(context: Context, sessionId: String) {
            val bundle = Bundle()
            bundle.putString(INTENT_SESSION_ID, sessionId)
            val intent = Intent(context, SearchChatMediaActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        bundle = intent.extras
        super.onCreate(savedInstanceState)
    }

    override fun createFragment(): Fragment {
        val fragment = SearchChatMediaFragment()
        fragment.arguments = bundle
        return fragment
    }

}