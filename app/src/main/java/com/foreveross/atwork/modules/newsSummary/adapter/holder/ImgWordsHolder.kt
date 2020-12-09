package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.R
import com.foreveross.atwork.component.listview.InnerListView
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.chat.adapter.MultiImageListAdapter
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager.updateClick
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.ImageCacheHelper
import com.foreveross.atwork.utils.TimeViewUtil
import com.foreveross.atwork.utils.img.round.RoundedImageView
import com.nostra13.universalimageloader.core.DisplayImageOptions

class ImgWordsHolder (itemView: View) : RvBaseHolder(itemView){

    private var imageArticle: RoundedImageView = itemView.findViewById(R.id.multi_image_article_cover)
    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var tvTitleNTime: TextView = itemView.findViewById(R.id.tvTitleNTime)
    private var tvContent: TextView = itemView.findViewById(R.id.tvContent)
    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var articleListView: InnerListView = itemView.findViewById(R.id.multi_image_article_list_view)
    private var multiImageListAdapter:MultiImageListAdapter? = null
    private var articleList: ArrayList<ArticleItem> = ArrayList()
    private var articleChatMessage: ArticleChatMessage? = null

    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        val margin = DensityUtil.dip2px(40f)
        val width = ScreenUtils.getScreenWidth(context) - margin
        imageArticle.layoutParams.width = width
        imageArticle.layoutParams.height = width * 5 / 9

        imageArticle.setOnClickListener {
            val articleItem: ArticleItem = articleList[0]
            //先搜索出服务号的id
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
            if(sessionList.size > 0) {
                val session = sessionList[0]
                ArticleItemHelper.startWebActivity(context, session, articleChatMessage, articleItem)
                CheckUnReadUtil.CompareTime(session.identifier,articleChatMessage!!.deliveryTime)
                //更新点击率
                updateClick(session.identifier, Type.NEWS_SUMMARY)
            }
        }
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }
        tvTitleNTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, rvData.chatPostMessage!!.deliveryTime)
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.chatPostMessage!!.from, rvData.chatPostMessage!!.mOrgId, true, true)
        if (rvData.chatPostMessage is ArticleChatMessage) {
            articleChatMessage = rvData.chatPostMessage as ArticleChatMessage
            tvContent.text = articleChatMessage!!.articles[0].title
            articleList.clear()
            articleList.addAll(articleChatMessage!!.articles)
            ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(articleChatMessage!!.articles[0]), imageArticle, getDisplayImageOptions())

            //先搜索出服务号的id
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
            if(sessionList.size > 0) {
                multiImageListAdapter = MultiImageListAdapter(context, sessionList[0])
                articleListView.adapter = multiImageListAdapter
                multiImageListAdapter!!.setArticleItems(articleChatMessage, articleList)
            }
        }

        ivAvatar.setOnClickListener {
            dataCallBack.onResult(rvData)
        }

    }

    private fun getDisplayImageOptions(): DisplayImageOptions? {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        builder.showImageOnLoading(R.mipmap.loading_cover_size)
        builder.showImageForEmptyUri(R.mipmap.loading_cover_size)
        builder.showImageOnFail(R.mipmap.loading_cover_size)
        return builder.build()
    }

}