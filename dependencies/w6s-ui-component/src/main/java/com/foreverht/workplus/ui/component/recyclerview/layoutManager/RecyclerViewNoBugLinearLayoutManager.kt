package com.foreverht.workplus.ui.component.recyclerview.layoutManager

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewNoBugLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        super.onLayoutChildren(recycler, state)

        try {
            //try catch一下
            super.onLayoutChildren(recycler, state)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}