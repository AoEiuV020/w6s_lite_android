package com.foreveross.atwork.modules.workbench.component

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.foreverht.db.service.repository.MessageAppRepository
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchNewsSummaryCard
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.modules.newsSummary.activity.NewsSummaryActivity
import com.foreveross.atwork.modules.newsSummary.adapter.WorkBenchNewsSummaryAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions
import kotlinx.android.synthetic.main.component_workbench_news_summary.view.*

class WorkBenchNewsSummaryView : WorkbenchBasicCardView<WorkbenchNewsSummaryCard> {

    override lateinit var workbenchCard: WorkbenchNewsSummaryCard

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var messageList = ArrayList<ChatPostMessage>()

    private lateinit var newsSummaryRvAdapter: WorkBenchNewsSummaryAdapter

    private var listCount = 0

    private lateinit var mContext: Context
    private var page = 0
    private var totleDataList: ArrayList<NewsSummaryPostMessage> = ArrayList()

    override fun findViews(context: Context) {
        mContext = context
        LayoutInflater.from(context).inflate(R.layout.component_workbench_news_summary, this)
        rlNewSummary.setOnClickListener {
            if(newsSummaryRvAdapter.listSize() > 0) {
                //跳转到消息汇总详情
                routeNewsSummary(context)
            }
        }
        val layoutManager = LinearLayoutManager(this.context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        lvNewsSummary.layoutManager = layoutManager
//        rvNewsSummary.setItemViewCacheSize(10)
//        rvNewsSummary.setHasFixedSize(true)
        lvNewsSummary.isNestedScrollingEnabled = false
        messageList = ArrayList()
        newsSummaryRvAdapter = WorkBenchNewsSummaryAdapter(context, messageList)
        lvNewsSummary.adapter = newsSummaryRvAdapter
        lvNewsSummary.setOnClickListener {
            if(newsSummaryRvAdapter.listSize() > 0) {
                //跳转到消息汇总详情
                routeNewsSummary(context)
            }
        }
    }

    private fun routeNewsSummary(context: Context) {

        WorkbenchManager.handleClickAction(workbenchCard) {
            val intent = NewsSummaryActivity.getIntent(context)
            context.startActivity(intent)
        }

    }

    @SuppressLint("StaticFieldLeak")
    private fun getData(limit: Int, offset: Int){
        object : AsyncTask<Void?, Void?, Boolean>() {
            var dataList: ArrayList<ChatPostMessage> = ArrayList()
            override fun doInBackground(vararg p0: Void?): Boolean {
                val orgId = PersonalShareInfo.getInstance().getCurrentOrg(context)
                val data = MessageAppRepository.getInstance().queryTotleMessages(context,limit,offset,orgId)
                totleDataList.addAll(data)
                totleDataList.sortWith(Comparator { chatPostMessage, t1 -> t1.getChatPostMessage()!!.deliveryTime.compareTo(chatPostMessage.getChatPostMessage()!!.deliveryTime) })
                if(data.size >= MessageAppRepository.LIMIT_DEFAULT){
                    page += MessageAppRepository.LIMIT_DEFAULT
                    getData(MessageAppRepository.LIMIT_DEFAULT,page)
                    return false
                }

                if(totleDataList.size >= listCount) {
                    for (i in 0 until listCount) {
                        dataList.add(totleDataList[i].getChatPostMessage())
                    }
                }else {
                    for (i in 0 until totleDataList.size) {
                        dataList.add(totleDataList[i].getChatPostMessage())
                    }
                }
                return dataList.isEmpty()
            }

            override fun onPostExecute(isFail: Boolean) {
                if(!isFail){
                    tvNullData.visibility = View.GONE
                    if(dataList.size > 2) {
                        ivJump.visibility = View.GONE
                    }else{
                        ivJump.visibility = View.VISIBLE
                    }

                    //超过3个数据，改变列表长度为自适应
                    val lp = rlNewSummary.layoutParams as ViewGroup.LayoutParams
                    if(dataList.size > 2) {
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }else{
                        lp.height = DensityUtil.dip2px(80f)
                    }

                    rlNewSummary.layoutParams = lp
                }else{
                    tvNullData.visibility = View.VISIBLE
                    ivJump.visibility = View.GONE
                }
                newsSummaryRvAdapter.notifyData(dataList)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    override fun refresh(workbenchCard: WorkbenchNewsSummaryCard) {
        this.workbenchCard = workbenchCard
        if (::newsSummaryRvAdapter.isInitialized) {
            newsSummaryRvAdapter.workbenchCard = workbenchCard
        }
        listCount = workbenchCard.listCount
        totleDataList.clear()
        page = 0
        getData(MessageAppRepository.LIMIT_DEFAULT,page)
        if(!TextUtils.isEmpty(workbenchCard.singleIcon)){
            val url = ImageCacheHelper.getMediaUrl(workbenchCard.singleIcon)
            Glide.with(context)
                    .load(url)
                    .into(ivIcon)
//            AvatarHelper.setAppAvatarByAvaId(ivIcon, workbenchCard.singleIcon, true, true)
        }
    }

    override fun refreshView(workbenchCard: WorkbenchNewsSummaryCard) {
    }

    override fun getViewType(): Int = WorkbenchCardType.APP_MESSAGES.hashCode()

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