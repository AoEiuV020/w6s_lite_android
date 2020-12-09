package com.foreverht.workplus.module.discussion.viewholder

import android.view.View
import android.widget.TextView
import com.foreveross.atwork.R

class GridViewHolder(itemView: View): BaseViewHolder(itemView) {
    val tvMemberName = itemView.findViewById<TextView>(R.id.tv_member_name)
}