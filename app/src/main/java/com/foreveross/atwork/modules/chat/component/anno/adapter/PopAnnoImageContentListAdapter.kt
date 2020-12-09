package com.foreveross.atwork.modules.chat.component.anno.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.file.MediaItem
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions

class PopAnnoImageContentListAdapter(var context: Context, mediaItems: List<MediaItem>):  BaseQuickAdapter<MediaItem, PopAnnoImageContentListAdapter.PopAnnoImageViewHolder>(mediaItems){

    var itemCLickListener: OnItemClickListener? = null

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): PopAnnoImageViewHolder {
        val rootView = LayoutInflater.from(context).inflate(R.layout.item_anno_image_data_holding, parent, false)

        val annoImageView = PopAnnoImageViewHolder(rootView)

        annoImageView.flRemove.setOnClickListener {

            itemCLickListener?.onRemove(annoImageView.realPosition)
        }

        return annoImageView
    }

    override fun convert(imageViewHolder: PopAnnoImageViewHolder, item: MediaItem) {

        ImageCacheHelper.displayImage(item.filePath, imageViewHolder.ivContent, getLoadingOptions())
    }


    private fun getLoadingOptions(): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        builder.showImageForEmptyUri(R.mipmap.loading_icon_square)
        builder.showImageOnLoading(R.mipmap.loading_icon_square)
        builder.showImageOnFail(R.mipmap.loading_icon_square)
        return builder.build()
    }


    interface OnItemClickListener {
        fun onRemove(position: Int)
    }


    class PopAnnoImageViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var ivContent: ImageView = itemView.findViewById(R.id.iv_content)
        var flRemove: FrameLayout = itemView.findViewById(R.id.fl_remove)

    }
}
