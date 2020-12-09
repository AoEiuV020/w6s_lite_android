package com.foreveross.atwork.modules.newsSummary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchNewsSummaryCard
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.modules.newsSummary.activity.NewsSummaryActivity
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.utils.TimeViewUtil

class WorkBenchNewsSummaryAdapter (context: Context, list: ArrayList<ChatPostMessage>) :  RecyclerView.Adapter<WorkBenchNewsSummaryItemViewHolder>() {

    private var mContext: Context? = context
    private var dataList: ArrayList<ChatPostMessage> = list
    var workbenchCard: WorkbenchNewsSummaryCard? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkBenchNewsSummaryItemViewHolder {
        val cardItemView =  LayoutInflater.from(mContext).inflate(R.layout.holder_new_summary_workbench, parent, false)
        return WorkBenchNewsSummaryItemViewHolder(cardItemView)
    }

    override fun getItemCount(): Int{
        return dataList.size
    }

    override fun onBindViewHolder(helper: WorkBenchNewsSummaryItemViewHolder, pos: Int) {
        if (dataList[pos] is ArticleChatMessage) {
            helper.tvTitle.text = (dataList[pos] as ArticleChatMessage).articles[0].title
        }
        if (dataList[pos] is TextChatMessage) {
            helper.tvTitle.text = (dataList[pos] as TextChatMessage).text
        }
        if (dataList[pos] is ImageChatMessage) {
            helper.tvTitle.text = "["+mContext!!.getString(R.string.message_type_image)+"]"
        }
        if (dataList[pos] is MicroVideoChatMessage) {
            helper.tvTitle.text = "["+mContext!!.getString(R.string.label_micro_video_chat_pop)+"]"
        }
        if (dataList[pos] is VoiceChatMessage) {
            helper.tvTitle.text = "["+mContext!!.getString(R.string.audio3)+"]"
        }
        if (dataList[pos] is FileTransferChatMessage) {
            helper.tvTitle.text = "["+mContext!!.getString(R.string.file)+"]"+(dataList[pos] as FileTransferChatMessage).name
        }
        helper.tvTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, dataList[pos].deliveryTime)
        helper.rlItem.setOnClickListener {
            routeSummary()
        }
    }

    private fun routeSummary() {
        WorkbenchManager.handleClickAction(workbenchCard) {
            val intent = NewsSummaryActivity.getIntent(mContext!!)
            mContext!!.startActivity(intent)
        }


    }

    fun notifyData(list: ArrayList<ChatPostMessage>){
        dataList = list
        notifyDataSetChanged()
    }

    fun listSize(): Int{
        return dataList.size
    }
}

class WorkBenchNewsSummaryItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
    var tvTime = itemView.findViewById(R.id.tvTime) as TextView
    var rlItem = itemView.findViewById(R.id.rlItem) as RelativeLayout
}