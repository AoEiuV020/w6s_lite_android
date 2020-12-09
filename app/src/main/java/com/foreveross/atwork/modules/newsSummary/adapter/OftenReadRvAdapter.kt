package com.foreveross.atwork.modules.newsSummary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.db.service.repository.UnreadSubcriptionMRepository
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.utils.AvatarHelper

class OftenReadRvAdapter: RecyclerView.Adapter<OftenReadRvAdapter.ViewHolder> {
    companion object {
        const val TYPE_IN = 0
        const val TYPE_OUT = 1
        const val TYPE_INT_OTHER = 2
        const val USE_RESON = "立即使用"
        const val HAS_GET = "已领取"
    }

    var items: List<App>? = null
    var context: Context? = null
    var itemClick: ItemClick? = null

    constructor(items: List<App>, context: Context) {
        this.items = items
        this.context = context
    }

    fun setItemClickListener(itemClick: ItemClick) {
        this.itemClick = itemClick
    }

    fun updateData(items: List<App>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (this.items!![position].mAppName != null) {
            holder.excText.text = this.items!![position].mAppName
        }
        if (this.items!![position].mAppId != null) {
            AvatarHelper.setAppAvatarById(holder.image, this.items!![position].mAppId, this.items!![position].mOrgId, true, true)
            val dataList = UnreadSubcriptionMRepository.getInstance().queryByAppId(this.items!![position].mAppId)
            if(dataList.size > 0){
                holder.ivPointUnread.visibility = View.VISIBLE
            }else{
                holder.ivPointUnread.visibility = View.GONE
            }
        }

        holder.rlJump.setOnClickListener {
            itemClick!!.OnItemClick(position,this.items!![position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_news_summary_often_read, parent, false) as LinearLayout
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var image: ImageView = itemView!!.findViewById(R.id.imgBody) as ImageView
        var ivPointUnread: ImageView = itemView!!.findViewById(R.id.ivPointUnread) as ImageView
        var excText: TextView = itemView!!.findViewById(R.id.excText) as TextView
        var rlJump: LinearLayout = itemView!!.findViewById(R.id.rlJump) as LinearLayout
    }

    interface ItemClick {
        fun OnItemClick(position: Int, app: App)
    }

}