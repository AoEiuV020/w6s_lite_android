package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData

class EndHolder (itemView: View) : RvBaseHolder(itemView){
    private var loadingView = itemView.findViewById<LinearLayout>(R.id.load_more_loading_view)
    private var failView = itemView.findViewById<FrameLayout>(R.id.load_more_load_fail_view)
            private var endView = itemView.findViewById<RelativeLayout>(R.id.load_more_load_end_view)
    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        loadingView.visibility = View.GONE
        failView.visibility = View.GONE
        endView.visibility = View.VISIBLE
    }

    fun showLoading(isShow: Boolean){
        if(isShow){
            loadingView.visibility = View.VISIBLE
            endView.visibility = View.GONE
        }else {
            loadingView.visibility = View.GONE
            endView.visibility = View.VISIBLE
        }
    }
}