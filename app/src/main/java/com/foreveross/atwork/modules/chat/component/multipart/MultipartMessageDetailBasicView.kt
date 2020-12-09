package com.foreveross.atwork.modules.chat.component.multipart

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.TimeViewUtil

abstract class MultipartMessageDetailBasicView<T : ChatPostMessage> : FrameLayout {


    constructor(context: Context) : super(context) {
        findViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
    }


    abstract fun findViews(context: Context)

    abstract fun nameView(): TextView

    abstract fun avatarView(): ImageView

    abstract fun timeView(): TextView

    abstract fun bottomLineView(): View


    open fun refreshItemView(chatPostMessage: T, position: Int, messageList: List<ChatPostMessage>) {

        refreshAvatar(chatPostMessage, position, messageList)

        if (!StringUtils.isEmpty(chatPostMessage.mMyNameInDiscussion)) {
            nameView().text = chatPostMessage.mMyNameInDiscussion
        } else {
            nameView().text = chatPostMessage.mMyName

        }

        timeView().text = TimeViewUtil.getMultipartItemViewTime(BaseApplicationLike.baseContext, chatPostMessage.deliveryTime)

        if (position == messageList.size - 1) {
            bottomLineView().visibility = View.GONE
        } else {
            bottomLineView().visibility = View.VISIBLE

        }


    }

    private fun refreshAvatar(chatPostMessage: ChatPostMessage, position: Int, messageList: List<ChatPostMessage>) {
        var shouldHideAvatar = false
        if (0 < position) { //have last
            val lastChatPostMessage = messageList[position - 1]
            if (lastChatPostMessage.from == chatPostMessage.from) {
                shouldHideAvatar = true
            }

        }

        if (shouldHideAvatar) {
            avatarView().visibility = View.INVISIBLE
        } else {
            AvatarHelper.setUserAvatarByAvaId(chatPostMessage.mMyAvatar, avatarView(), true, true)

            avatarView().visibility = View.VISIBLE

        }
    }


}