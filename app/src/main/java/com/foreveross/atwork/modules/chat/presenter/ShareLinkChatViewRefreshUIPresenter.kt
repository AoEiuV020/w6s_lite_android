package com.foreveross.atwork.modules.chat.presenter

import android.text.TextUtils
import android.view.View
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.component.chat.definition.IShareLinkChatView
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions

class ShareLinkChatViewRefreshUIPresenter(private val shareLinkChatView: IShareLinkChatView): IChatViewRefreshUIPresenter<ShareChatMessage>(shareLinkChatView) {


    override fun refreshItemView(chatPostMessage: ShareChatMessage) {


        ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(chatPostMessage.content), shareLinkChatView.coverView(), getRectOptions())

        if (!TextUtils.isEmpty(chatPostMessage.content.title)) {
            shareLinkChatView.titleView().text = chatPostMessage.content.title
        } else {
            shareLinkChatView.titleView().text = chatPostMessage.content.url
        }

        //            if (!TextUtils.isEmpty(shareChatMessage.getContent().summary)){
        //                summaryView.setText(shareChatMessage.getContent().summary);
        //            }else {
        //                summaryView.setVisibility(GONE);
        //            }
        if (!TextUtils.isEmpty(chatPostMessage.content.title)) {
            shareLinkChatView.titleView().text = chatPostMessage.content.title
        } else {
            shareLinkChatView.titleView().text = chatPostMessage.content.url
        }

        var summary = chatPostMessage.mArticleItem.summary

        if (TextUtils.isEmpty(summary)) {
            summary = chatPostMessage.content.url
        }

        shareLinkChatView.summaryView().text = summary
        if (StringUtils.isEmpty(summary)) {
            shareLinkChatView.summaryView().visibility = View.GONE
        } else {
            shareLinkChatView.summaryView().visibility = View.VISIBLE

        }


    }

    fun getRectOptions(): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        builder.showImageOnLoading(R.mipmap.default_link)
        builder.showImageForEmptyUri(R.mipmap.default_link)
        builder.showImageOnFail(R.mipmap.default_link)

        return builder.build()
    }
}