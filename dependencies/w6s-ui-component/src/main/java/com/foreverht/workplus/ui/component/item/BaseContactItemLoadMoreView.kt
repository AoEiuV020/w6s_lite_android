package com.foreverht.workplus.ui.component.item

import com.foreverht.workplus.ui.component.R
import com.foreverht.workplus.ui.component.recyclerview.loadmore.LoadMoreView

class BaseContactItemLoadMoreView: LoadMoreView() {
    override fun getLayoutId(): Int {
        return R.layout.quick_view_load_more
    }

    override fun getLoadingViewId(): Int {
        return R.id.load_more_loading_view
    }

    override fun getLoadFailViewId(): Int {
        return R.id.load_more_load_fail_view
    }

    override fun getLoadEndViewId(): Int {
        return R.id.load_more_load_empty_view
    }

}