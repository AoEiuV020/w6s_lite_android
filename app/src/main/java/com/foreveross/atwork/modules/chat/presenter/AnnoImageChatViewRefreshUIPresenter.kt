package com.foreveross.atwork.modules.chat.presenter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageContentInfo
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.component.chat.definition.IAnnoImageChatView
import com.foreveross.atwork.utils.ImageChatHelper


class AnnoImageChatViewRefreshUIPresenter(private val context : Context, private val annoImageChatView: IAnnoImageChatView): IChatViewRefreshUIPresenter<AnnoImageChatMessage>(annoImageChatView) {

    private var mediaContentList: ArrayList<ImageContentInfo> = arrayListOf()
    var adapter = AnnoImageContentInChatAdapter(context, mediaContentList)
    private val gridLayoutManager: GridLayoutManager

    init {
        adapter.setHasStableIds(true)
        val mediaContentRecyclerView = annoImageChatView.mediaContentView()
        mediaContentRecyclerView.adapter = adapter
        gridLayoutManager = GridLayoutManager(context, 3)

        mediaContentRecyclerView.layoutManager = gridLayoutManager
        mediaContentRecyclerView.addItemDecoration(GridLayoutSpaceItemDecoration(mediaContentList))

        (mediaContentRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mediaContentRecyclerView.isNestedScrollingEnabled = false
    }



    override fun refreshItemView(chatPostMessage: AnnoImageChatMessage) {
        mediaContentList.clear()
        mediaContentList.addAll(chatPostMessage.contentInfos)

        if(2 < mediaContentList.size) {
            gridLayoutManager.spanCount = 3

        } else if(1 <=  mediaContentList.size){
            gridLayoutManager.spanCount = mediaContentList.size

        }

        adapter.annoImageChatMessage = chatPostMessage
        adapter.notifyDataSetChanged()

        annoImageChatView.mediaContentView().isVisible = !ListUtil.isEmpty(mediaContentList)

        annoImageChatView.commentView().text = chatPostMessage.comment
        annoImageChatView.commentView().isVisible = !StringUtils.isEmpty(chatPostMessage.comment)
    }
}

class AnnoImageContentInChatAdapter(val context: Context, val mediaContentList: List<ImageContentInfo>): BaseQuickAdapter<ImageContentInfo, AnnoImageItemViewHolder>(mediaContentList) {

    var annoImageChatMessage: AnnoImageChatMessage? = null

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): AnnoImageItemViewHolder {
        val rootView = LayoutInflater.from(context).inflate(R.layout.item_anno_image_content_in_chat, parent, false)

        val annoImageView = AnnoImageItemViewHolder(rootView)



        return annoImageView
    }

    override fun convert(helper: AnnoImageItemViewHolder, item: ImageContentInfo) {


        ImageChatHelper.initImageContent(item.transformImageChatMessage(annoImageChatMessage), helper.ivContent, R.mipmap.loading_gray_holding, false)

        if(FileStatus.SENDING == annoImageChatMessage?.fileStatus
                && ChatStatus.Sending == annoImageChatMessage?.chatStatus) {
            progress(helper, item.progress)

        } else {
            helper.ivContent.setAlpha(255)
            helper.tvProgress.visibility = View.GONE
        }
    }

    override fun getItemId(position: Int): Long {
        return mediaContentList[position].deliveryId.hashCode().toLong()
    }

    /**
     * 更新图片上传进度
     *
     * @param progress
     */
    private fun progress(helper: AnnoImageItemViewHolder, progress: Int) {
        val alpha = progress * 2 + 50
        //        mIvContent.getBackground().setAlpha(alpha);
        helper.ivContent.setAlpha(alpha)
        if (100 == progress || 0 == progress) {
            helper.tvProgress.visibility = View.GONE
        } else {
            helper.tvProgress.visibility = View.VISIBLE
            helper.tvProgress.text = progress.toString()
        }
    }


}

class GridLayoutSpaceItemDecoration(val mediaContentList: ArrayList<ImageContentInfo>) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {


        val divider = DensityUtil.dip2px(2f)
        outRect.top = divider
        outRect.left = divider
        outRect.right = divider
        outRect.bottom = divider


    }

}

class AnnoImageItemViewHolder(itemView: View) : BaseViewHolder(itemView) {

    var ivContent: ImageView = itemView.findViewById(R.id.iv_content)
    var tvProgress: TextView = itemView.findViewById(R.id.tv_progress)

}