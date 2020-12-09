package com.foreveross.atwork.modules.chat.presenter

import android.view.View
import android.widget.ImageView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.component.chat.definition.IBusinessCardChatView
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions

class BusinessCardChatViewRefreshUIPresenter(private val businessCardChatView: IBusinessCardChatView): IChatViewRefreshUIPresenter<ShareChatMessage>(businessCardChatView) {

    private var coverAvatar: String? = null


    override fun refreshItemView(chatPostMessage: ShareChatMessage) {
        setCardAvatarByAvaId(businessCardChatView.coverView(), chatPostMessage.content.mShareUserAvatar)

        if (!StringUtils.isEmpty(chatPostMessage.content.mShareUserName)) {
            businessCardChatView.titleView().text = chatPostMessage.content.mShareUserName
        }

        if (!StringUtils.isEmpty(chatPostMessage.content.mShareUserGender)) {

            var setGender = false
            if ("male".equals(chatPostMessage.content.mShareUserGender, ignoreCase = true)) {
                businessCardChatView.genderView().setImageResource(R.mipmap.icon_gender_male)
                setGender = true
            }

            if ("female".equals(chatPostMessage.content.mShareUserGender, ignoreCase = true)) {
                businessCardChatView.genderView().setImageResource(R.mipmap.icon_gender_female)

                setGender = true

            }

            if (!setGender) {
                businessCardChatView.genderView().setImageResource(0)

            }


        }

        if (!StringUtils.isEmpty(chatPostMessage.content.mShareUserJobTitle)) {
            businessCardChatView.jobTitleView().text = chatPostMessage.content.mShareUserJobTitle
            businessCardChatView.jobTitleView().visibility = View.VISIBLE
        } else {
            businessCardChatView.jobTitleView().visibility = View.GONE
        }

        businessCardChatView.signatureView()?.apply {
            if (!StringUtils.isEmpty(chatPostMessage.content.mShareUserSignature)) {
                text = chatPostMessage.content.mShareUserSignature
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE

            }
        }

    }

    private fun setCardAvatarByAvaId(avatarView: ImageView, avatar: String) {
        val isViewNeedReset = isViewNeedReset(avatar)

        coverAvatar = avatar

        if (StringUtils.isEmpty(avatar)) {
            ImageCacheHelper.setImageResource(avatarView, R.mipmap.icon_default_user_square)
            return
        }


        var loadingId = -1
        if (isViewNeedReset) {
            loadingId = R.mipmap.icon_default_user_square
        }

        val options = getRectOptions(R.mipmap.icon_default_user_square, loadingId)
        ImageCacheHelper.displayImageByMediaId(avatar, avatarView, options)

    }

    private fun isViewNeedReset(avatar: String): Boolean {
        var viewNeedReset = true
        if (null != this.coverAvatar && this.coverAvatar == avatar) {
            viewNeedReset = false
        }
        return viewNeedReset
    }

    private fun getRectOptions(resId: Int, loadingId: Int): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        if (-1 == loadingId) {
            builder.showImageOnLoading(null)
        } else {
            builder.showImageOnLoading(loadingId)
        }

        if (-1 != resId) {
            builder.showImageForEmptyUri(resId)
            builder.showImageOnFail(resId)
        }

        return builder.build()
    }

}