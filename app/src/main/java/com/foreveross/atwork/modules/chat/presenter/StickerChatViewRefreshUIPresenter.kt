package com.foreveross.atwork.modules.chat.presenter

import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.chat.component.chat.definition.IStickerChatView

class StickerChatViewRefreshUIPresenter(private val stickerChatView: IStickerChatView): IChatViewRefreshUIPresenter<StickerChatMessage>(stickerChatView) {

    override fun refreshItemView(chatPostMessage: StickerChatMessage) {

        val size = DensityUtil.dip2px(50f)
        ViewUtil.setSize(stickerChatView.contentView(), size, size)

        refreshGif(chatPostMessage)
    }

    private fun refreshGif(chatPostMessage: StickerChatMessage) {
        val options = RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fallback(R.mipmap.loading_gray_holding)
                .error(R.mipmap.loading_gray_holding)
        var loadUrl = ""
        if (BodyType.Sticker == chatPostMessage.mBodyType) {
            var name = chatPostMessage.stickerName
            if (name.contains(".")) {
                name = name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            }
            loadUrl = AtWorkDirUtils.getInstance().getAssetStickerUri(chatPostMessage.themeName, name)
        } else {
            loadUrl = chatPostMessage.getChatStickerUrl(stickerChatView.contentView().context, UrlConstantManager.getInstance().stickerImageUrl)
        }
        Log.e("loadurl ", "left load url = $loadUrl")
        Glide.with(stickerChatView.contentView()).load(loadUrl).apply(options).into(stickerChatView.contentView())
    }


}