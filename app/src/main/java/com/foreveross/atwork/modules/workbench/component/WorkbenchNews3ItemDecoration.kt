package com.foreveross.atwork.modules.workbench.component

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.utils.DensityUtil

class WorkbenchNews3ItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = DensityUtil.dip2px(10f)
    }
}