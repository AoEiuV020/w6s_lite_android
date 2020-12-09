package com.foreveross.atwork.modules.chat.presenter

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.chat.component.chat.definition.IImageChatView
import com.foreveross.atwork.utils.ImageChatHelper

class ImageChatViewRefreshUIPresenter(private val imageChatView: IImageChatView): IChatViewRefreshUIPresenter<ImageChatMessage>(imageChatView) {

    override fun refreshItemView(chatPostMessage: ImageChatMessage) {
        val size = DensityUtil.dip2px(50f)
        ViewUtil.setSize(imageChatView.contentView(), size, size)
        ImageChatHelper.initImageContent(chatPostMessage, imageChatView.contentView(), R.mipmap.loading_icon_square, false)
    }

}