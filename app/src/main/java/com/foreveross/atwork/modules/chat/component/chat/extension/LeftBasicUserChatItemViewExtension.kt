package com.foreveross.atwork.modules.chat.component.chat.extension

import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService
import com.foreveross.atwork.infrastructure.model.discussion.Discussion
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionMemberTag
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.infrastructure.utils.extension.ctxApp
import com.foreveross.atwork.infrastructure.utils.extension.dp2px
import com.foreveross.atwork.infrastructure.utils.extension.getColorCompat
import com.foreveross.atwork.modules.chat.component.chat.LeftBasicUserChatItemView
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.modules.discussion.util.RefreshMemberTagViewCarrier
import com.foreveross.atwork.modules.discussion.util.refreshMemberTagView

fun LeftBasicUserChatItemView.doRefreshTagView(message: ChatPostMessage) {

    val tagLinerLayoutHandling = this.tagLinerLayout ?: return

    refreshMemberTagView(tagLinerLayoutHandling, RefreshMemberTagViewCarrier(message.deliveryId, message.discussionId, message.from, this.tvTagList)) {
        if (!shouldShowAvatarInDiscussionChat(message)) {
            tagLinerLayoutHandling.isGone = true
            true

        } else {
            false

        }

    }


}

