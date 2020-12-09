package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.db.service.repository.UnreadSubcriptionMRepository
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest
import com.foreveross.atwork.modules.newsSummary.adapter.OftenReadRvAdapter
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.fragment.NewsSummaryFragment

class OftenReadHolder (itemView: View) : RvBaseHolder(itemView){

    private var recyclerView: RecyclerView = itemView.findViewById(R.id.rvOftenRead)
    private var mContext: Context? = null
    private var mClickListener: ClickListener? = null
    private lateinit var mAdapter: OftenReadRvAdapter
    private var items: ArrayList<App> = ArrayList()

    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        mContext = context
        recyclerView.isNestedScrollingEnabled = false
        this.items.clear()
        if(rvData.getAppList() != null){
            this.items.addAll(rvData.getAppList()!!)
        }
        if (recyclerView.adapter == null) {
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager.initialPrefetchItemCount = 4
            recyclerView.layoutManager = layoutManager
            recyclerView.isFocusableInTouchMode = false
            mAdapter = OftenReadRvAdapter(this.items, context)
            recyclerView.adapter = mAdapter
        } else {
            mAdapter.notifyDataSetChanged()
        }

        mAdapter.setItemClickListener(object : OftenReadRvAdapter.ItemClick {
            override fun OnItemClick(position: Int, app: App) {
                val entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Service, app).setOrgId(app.mAppId)
                ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest)
                val intent = ChatDetailActivity.getIntent(context, app.mAppId)
                intent.putExtra(ChatDetailFragment.RETURN_BACK, true)
                intent.putExtra(NewsSummaryFragment.NEWS_SUMMARY_CLICK,true)
                context.startActivity(intent)
                UnreadSubcriptionMRepository.getInstance().removeByAppId(app.mAppId)
            }
        })
    }

    interface ClickListener{
        fun onClick(fileTransferChatMessage: FileTransferChatMessage, id: String)
    }

    fun setClickListener(clickListener: ClickListener){
        mClickListener = clickListener
    }

    private fun showFileStatusFragment(fileTransferChatMessage: FileTransferChatMessage) {

    }
}