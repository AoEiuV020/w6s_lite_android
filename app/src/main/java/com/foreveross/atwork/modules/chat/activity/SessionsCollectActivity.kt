package com.foreveross.atwork.modules.chat.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.chat.fragment.SessionsCollectFragment
import com.foreveross.atwork.support.SingleFragmentActivity
import java.io.Serializable

class SessionsCollectActivity: SingleFragmentActivity() {


    override fun createFragment(): Fragment = SessionsCollectFragment()

    companion object {

        const val DATA_COLLECT_TYPE = "DATA_COLLECT_TYPE"

        @JvmStatic
        fun getIntent(context: Context, collectType: CollectType): Intent {
            val intent = Intent(context, SessionsCollectActivity::class.java)
            intent.putExtra(DATA_COLLECT_TYPE, collectType)
            return intent
        }
    }
}



enum class CollectType: Serializable {

    DISCUSSION_HELPER,

    APP_ANNOUNCE
}