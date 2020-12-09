package com.foreveross.atwork.modules.chat.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.file.MediaItem
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity
import com.foreveross.atwork.modules.image.adapter.MediaSelectAdapter
import com.foreveross.atwork.modules.image.component.MediaSelectItemView
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment
import com.foreveross.atwork.utils.AtworkToast
import kotlinx.android.synthetic.main.item_search_chat_medias.view.*

/**
 *  create by reyzhang22 at 2020-02-11
 */
class SearchMediasItemView(context: Context?): LinearLayout(context) {


    private lateinit var selectedMap: MutableMap<String, MediaItem>
    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.item_search_chat_medias, this)
    }


    @SuppressLint("SetTextI18n")
    fun setMediaItems(params: MediaItemParams) {
        this.selectedMap = params.selectMap
        gv_medias.apply {
            adapter = MediaSelectAdapter(context, params.itemList, selectedMap.values.toList()).apply {
                setSelectMode(params.selectMode)
            }
            setOnItemClickListener { _, view, position, _ ->
                val mediaItem = params.itemList[position]
                if (!params.selectMode) {
                    refreshImageChatMessageList(params.itemList)
                    val count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(mediaItem.message)
                    val intent = Intent()
                    intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count)
                    intent.setClass(AtworkApplicationLike.baseContext, ImageSwitchInChatActivity::class.java)
                    context.startActivity(intent)
                    return@setOnItemClickListener
                }
                val item = view as MediaSelectItemView

                mediaItem.isSelected = !mediaItem.isSelected
                if (selectedMap.containsKey(mediaItem.identifier)) {
                    selectedMap.remove(mediaItem.identifier)
                    item.setChecked(false)
                    return@setOnItemClickListener
                }
                if (selectedMap.size >= 9) {
                    AtworkToast.showToast(context.getString(R.string.max_select_media, "9"))
                    return@setOnItemClickListener
                }
                selectedMap.put(mediaItem.identifier, mediaItem)
                item.setChecked(true)
            }
        }
    }

    protected fun refreshImageChatMessageList(mediaItem: MutableList<MediaItem>) {
        ImageSwitchInChatActivity.sImageChatMessageList.clear()

        for (item in mediaItem) {
            ImageSwitchInChatActivity.sImageChatMessageList.add(item.message)

        }
        ImageSwitchInChatActivity.sImageChatMessageList
    }

    data class MediaItemParams(
            var itemList: MutableList<MediaItem>  = mutableListOf(),
            var selectMap: MutableMap<String, MediaItem>  = mutableMapOf(),
            var selectMode: Boolean = false
    )

}