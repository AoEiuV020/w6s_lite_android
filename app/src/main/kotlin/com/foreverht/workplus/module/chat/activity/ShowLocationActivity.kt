package com.foreverht.workplus.module.chat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreverht.workplus.module.chat.fragment.ShowLocationFragment
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.support.SingleFragmentActivity

/**
 *  create by reyzhang22 at 2020-01-15
 */
const val BUNDLE_LOCATION_INFO = "BUNDLE_LOCATION_INFO"
class ShowLocationActivity : SingleFragmentActivity() {

    private  var bundle: Bundle? = null

    companion object {
        fun startActivity(context: Context, locationBody: ShareChatMessage.LocationBody) {
            val intent = Intent(context, ShowLocationActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_LOCATION_INFO, locationBody)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        bundle = intent.extras
        super.onCreate(savedInstanceState)

    }

    override fun createFragment(): Fragment {
        val fragment = ShowLocationFragment()
        fragment.arguments = bundle
        return fragment
    }



}