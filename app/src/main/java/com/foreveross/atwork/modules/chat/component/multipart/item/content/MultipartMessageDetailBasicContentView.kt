package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage

abstract class MultipartMessageDetailBasicContentView<T: ChatPostMessage>: FrameLayout {



    constructor(context: Context) : super(context) {
        findViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
    }


    abstract fun findViews(context: Context)

    abstract fun refreshUI(message: T)

}