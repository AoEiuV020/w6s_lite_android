package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData

abstract class RvBaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindHolder(siftRVData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>)
}