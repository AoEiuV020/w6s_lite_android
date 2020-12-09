package com.foreveross.atwork.modules.chat.component.history

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.utils.TimeViewUtil

abstract class BasicHistoryItemView : RelativeLayout {

    constructor(context: Context) : super(context) {
        findViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
    }

    abstract fun getTimeView(): TextView

    abstract fun findViews(context: Context)

    open fun refreshView(message: ChatPostMessage) {
        getTimeView().text = TimeViewUtil.getMultipartItemViewTime(BaseApplicationLike.baseContext, message.deliveryTime)
    }

    companion object {
        fun getViewTypeCount(): Int = 7
    }
}