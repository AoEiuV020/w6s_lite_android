package com.foreveross.atwork.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.foreveross.atwork.R

class CommonPopMainAndSubItemView : LinearLayout {

    lateinit var tvItemMainContent: TextView
    lateinit var tvItemSubContent: TextView

    constructor(context: Context?) : super(context) {
        findViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.item_common_pop_main_and_sub, this)

        tvItemMainContent = view.findViewById(R.id.tv_item_main_content)
        tvItemSubContent = view.findViewById(R.id.tv_item_sub_content)
    }

    fun setMainContent(content: String) {
        tvItemMainContent.text = content
    }

    fun setSubContent(content: String) {
        tvItemSubContent.text = content
    }

}