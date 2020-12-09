package com.foreveross.atwork.modules.chat.component.reference

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.chat.presenter.IChatViewRefreshUIPresenter

abstract class RefreshReferencingBasicView<T: ChatPostMessage>: FrameLayout, IRefreshReferencingView<T> {

    var presenter: IChatViewRefreshUIPresenter<T>? = null

    constructor(context: Context) : super(context) {
        findViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
    }


    abstract fun findViews(context: Context)

    override fun refreshUI(message: T) {
        super.refreshUI(message)

        presenter?.refreshItemView(message)

    }


}