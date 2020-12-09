package com.foreveross.atwork.modules.chat.presenter

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.chat.component.chat.definition.IGifChatView
import com.foreveross.atwork.utils.ImageChatHelper
import pl.droidsonroids.gif.GifDrawable

class GifChatViewRefreshUIPresenter(private val gifChatView: IGifChatView): IChatViewRefreshUIPresenter<ImageChatMessage>(gifChatView) {

    override fun refreshItemView(chatPostMessage: ImageChatMessage) {

        if (chatPostMessage.deliveryId == gifChatView.contentView().tag) {


            val drawable = gifChatView.contentView().drawable

            if (null != drawable && drawable is GifDrawable) {
                if (!drawable.isPlaying) {
                    gifChatView.contentView().setImageDrawable(drawable)
                }

                return
            }
        }

        val size = DensityUtil.dip2px(50f)
        ViewUtil.setSize(gifChatView.contentView(), size, size)

        gifChatView.contentView().tag = chatPostMessage.deliveryId
        ImageChatHelper.showGif(gifChatView.contentView().context, gifChatView.contentView(), gifChatView.gifTagView(), chatPostMessage, false)
    }



}