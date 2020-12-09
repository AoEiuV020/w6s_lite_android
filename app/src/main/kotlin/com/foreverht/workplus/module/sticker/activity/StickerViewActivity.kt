package com.foreverht.workplus.module.sticker.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreverht.workplus.module.sticker.activity.fragment.StickerViewFragment
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage
import com.foreveross.atwork.support.SingleFragmentActivity
import java.io.Serializable

class StickerViewActivity : SingleFragmentActivity() {

    companion object {
        val STICKER_MESSAGE = "STICKER_MESSAGE"
        fun getIntent(activity: Context, stickerChatMessage: StickerChatMessage): Intent {
            val intent = Intent()
            intent.putExtra(STICKER_MESSAGE, stickerChatMessage as Serializable)
            intent.setClass(activity, StickerViewActivity::class.java)
            return intent;
        }
    }

    override fun createFragment(): Fragment {
        val stickerItem = getIntent().getSerializableExtra(STICKER_MESSAGE)
        val bundle = Bundle()
        bundle.putSerializable(STICKER_MESSAGE, stickerItem)
        val fragment = StickerViewFragment()
        fragment.arguments = bundle
        return fragment
    }

}