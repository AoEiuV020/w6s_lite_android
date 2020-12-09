package com.foreverht.workplus.module.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.foreveross.atwork.R
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

class MessageTagAdapter(context: Context, inflater: LayoutInflater, datas: List<SearchMessageType>?, tagFlowLayout: TagFlowLayout?) : TagAdapter<SearchMessageType>(datas) {
    var context:        Context?        = null
    var inflater:       LayoutInflater? = null
    var tagFlowLayout:  TagFlowLayout?  = null
    var selectedList = mutableListOf<SearchMessageType>()

    init {
        this.tagFlowLayout = tagFlowLayout
        this.inflater =inflater
        this.context = context
    }

    fun setTagSelected(type: SearchMessageType) {
        if (selectedList.contains(type)) {
            selectedList.remove(type)
        } else {
            selectedList.add(type)
        }
    }

    override fun getView(parent: FlowLayout?, position: Int, type: SearchMessageType?): View {
        val tv = inflater?.inflate(R.layout.flag_adapter,
                tagFlowLayout, false) as TextView
        var showName = ""
        when(type) {
            SearchMessageType.article -> showName = context!!.getString(R.string.article)
            SearchMessageType.video -> showName = context!!.getString(R.string.video2)
            SearchMessageType.file -> showName = context!!.getString(R.string.file)
            SearchMessageType.image -> showName = context!!.getString(R.string.image)
            SearchMessageType.text -> showName = context!!.getString(R.string.text)
            SearchMessageType.voice -> showName = context!!.getString(R.string.audio2)
        }
        tv.setText(showName)
        tv.setBackgroundResource(if (selectedList.contains(type)) R.mipmap.icon_favorite_tag_selected else R.mipmap.icon_favorite_all_tag_bg)
        tv.setTextColor(context!!.resources!!.getColor(R.color.black))
        return tv
    }
}