package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.chat.component.chat.definition.IStickerChatView
import com.foreveross.atwork.modules.chat.presenter.StickerChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.component_message_referencing_image.view.*
import kotlinx.android.synthetic.main.component_message_referencing_text.view.tvTitle

class StickerMessageReferencingView(context: Context) : RefreshReferencingBasicView<StickerChatMessage>(context), IStickerChatView {


    lateinit var vRoot: View

    init {
        presenter = StickerChatViewRefreshUIPresenter(this)

    }

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_sticker, this)
    }

    override fun getReferencingAuthorView(): TextView = tvTitle

    override fun contentView(): ImageView = ivContent


    override fun refreshUI(message: StickerChatMessage) {

        tvTitle.doOnPreDraw {
            val size = vRoot.height - it.height - DensityUtil.dip2px(5f)
            ViewUtil.setSize(ivContent, size, size)

            super.refreshUI(message)


        }


    }
}