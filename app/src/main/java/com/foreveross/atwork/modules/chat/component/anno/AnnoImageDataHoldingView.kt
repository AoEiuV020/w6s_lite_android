package com.foreveross.atwork.modules.chat.component.anno

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.file.MediaItem
import com.foreveross.atwork.modules.chat.component.IPopChatDataHolder
import com.foreveross.atwork.modules.chat.component.anno.adapter.PopAnnoImageContentListAdapter
import kotlinx.android.synthetic.main.component_message_anno_image_holding.view.*

class AnnoImageDataHoldingView(context: Context, private val popChatDataHolder: IPopChatDataHolder?) : FrameLayout(context) {


    lateinit var vRoot: View
    private val mediaItems: ArrayList<MediaItem> = arrayListOf()

    val adapter = PopAnnoImageContentListAdapter(context, mediaItems)

    init {
        findViews(context)
        initData()
        registerListener()

    }

    private fun findViews(context: Context) {
        vRoot = LayoutInflater.from(context).inflate(R.layout.component_message_anno_image_holding, this)
    }

    private fun initData() {

        rvContentList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvContentList.adapter = adapter
    }

    private fun registerListener() {
        adapter.itemCLickListener = object: PopAnnoImageContentListAdapter.OnItemClickListener {

            override fun onRemove(position: Int) {
               popChatDataHolder?.onRemoveMediaItem(position)
                mediaItems.removeAt(position)
                adapter.notifyDataSetChanged()

            }

        }

    }

    fun refreshUI(mediaItems: List<MediaItem>) {
        this.mediaItems.clear()
        this.mediaItems.addAll(mediaItems)
        adapter.notifyDataSetChanged()
    }




}