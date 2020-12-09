package com.foreveross.atwork.modules.chat.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import kotlinx.android.synthetic.main.component_chat_detail_pop_function_area.view.*

class PopChatDetailFunctionAreaView: FrameLayout {


    constructor(context: Context) : super(context) {
        findViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.component_chat_detail_pop_function_area, this)

    }

    fun getRootBgView(): View {
        return flRoot
    }

    fun getCallView(): View {
        return llCall
    }

    fun getStarView(): View {
        return llStar
    }

    fun getStarIvView(): ImageView {
        return ivStar
    }

    fun getMoreView(): View {
        return llMore
    }

    fun getMoreTv(): TextView {
        return tvMore
    }

    fun getStarTv(): TextView {
        return tvStar
    }

    fun getCallTv(): TextView {
        return tvCall
    }
}