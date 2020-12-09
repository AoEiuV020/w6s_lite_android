package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.utils.ImageChatHelper
import kotlinx.android.synthetic.main.component_message_referencing_image.view.*
import kotlinx.android.synthetic.main.component_message_referencing_text.view.tvTitle

class ImageMessageReferencingView(context: Context) : RefreshReferencingBasicView<ImageChatMessage>(context) {

    lateinit var vRoot: View

    override fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_referencing_image, this)
    }

    override fun getReferencingAuthorView(): TextView = tvTitle


    override fun refreshUI(message: ImageChatMessage) {
        super.refreshUI(message)

        tvTitle.doOnPreDraw {
            val size = vRoot.height - it.height - DensityUtil.dip2px(5f)
            ViewUtil.setSize(ivContent, size, size)
            ImageChatHelper.initImageContent(message, ivContent, R.mipmap.loading_icon_square, false)

        }

    }
}