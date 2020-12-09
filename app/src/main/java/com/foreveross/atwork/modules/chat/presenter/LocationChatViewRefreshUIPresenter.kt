package com.foreveross.atwork.modules.chat.presenter

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.ILocationChatView

class LocationChatViewRefreshUIPresenter(private val locationChatView: ILocationChatView): IChatViewRefreshUIPresenter<ShareChatMessage>(locationChatView) {

    override fun refreshItemView(shareChatMessage: ShareChatMessage) {
        val locationChatBody = shareChatMessage.chatBody[ShareChatMessage.SHARE_MESSAGE] as ShareChatMessage.LocationBody
        locationChatView.descView().text = locationChatBody.mName

    }

    val options = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .fallback(R.mipmap.loading_gray_holding)
            .error(R.mipmap.loading_gray_holding)
}