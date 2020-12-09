package com.foreverht.workplus.ui.component.dialogFragment

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.foreverht.workplus.ui.component.R
/**
 * Created by wuzejie on 2019/10/29.
 */
class W6sDialogItemView : LinearLayout {

    lateinit var tvItemContent: TextView
    lateinit var ivTick: ImageView

    //构造函数
    constructor(context: Context?) : super(context) {
        findViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.w6s_dialog_item_view, this)

        tvItemContent = view.findViewById(R.id.tv_item_content)
        ivTick = view.findViewById(R.id.iv_tick)
    }

    fun setContent(content: String) {
        tvItemContent.text = content
    }

    /**
     * Description:设置文本的颜色
     * @param：color 所设置的颜色
     */
    fun setTextColor(color: String?) {
        tvItemContent.setTextColor(Color.parseColor(color))
    }

    /**
     * Description:设置文本居中
     */
    fun setTextContentCenter() {
        var layoutParams : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        tvItemContent.layoutParams = layoutParams
    }

    fun select() {
        tvItemContent.setTextColor(ContextCompat.getColor(context, R.color.common_blue_bg))
        ivTick.setImageResource(R.mipmap.icon_dialog_item_selected)
        ivTick.visibility = View.VISIBLE
    }

    fun unselect() {
        tvItemContent.setTextColor(ContextCompat.getColor(context, R.color.common_text_color))
        ivTick.visibility = View.INVISIBLE
    }
}