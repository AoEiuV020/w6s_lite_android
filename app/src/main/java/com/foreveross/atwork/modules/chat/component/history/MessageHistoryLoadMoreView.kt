package com.foreveross.atwork.modules.chat.component.history

import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.loadmore.LoadMoreView

class MessageHistoryLoadMoreView: LoadMoreView() {
    override fun getLayoutId(): Int {
        return R.layout.message_history_view_load_more
    }

    override fun getLoadingViewId(): Int {
        return R.id.load_more_loading_view
    }

    override fun getLoadFailViewId(): Int {
        return R.id.load_more_load_fail_view
    }

    override fun getLoadEndViewId(): Int {
        return R.id.load_more_load_end_view
    }
}