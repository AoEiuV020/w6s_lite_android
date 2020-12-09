package com.foreveross.atwork.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foreveross.atwork.R

class CommonPopSelectWithTitleItemView : LinearLayout {

    lateinit var tvItemContent: TextView
    lateinit var ivTick: ImageView

    constructor(context: Context?) : super(context) {
        findViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.item_common_pop_select_with_title, this)

        tvItemContent = view.findViewById(R.id.tv_item_content)
        ivTick = view.findViewById(R.id.iv_tick)
    }

    fun setContent(content: String) {
        tvItemContent.text = content
    }

    fun select() {
//        tvItemContent.setTextColor(ContextCompat.getColor(context, R.color.common_blue_bg))
        ivTick.setImageResource(R.mipmap.icon_common_tick)
        ivTick.visibility = View.VISIBLE
    }

    fun unselect() {
//        tvItemContent.setTextColor(ContextCompat.getColor(context, R.color.common_text_color))
        ivTick.visibility = View.INVISIBLE
    }
}