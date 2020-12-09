package com.foreveross.atwork.modules.newsSummary.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.db.service.repository.ClickStatisticsRepository
import com.foreverht.db.service.repository.MessageAppRepository
import com.foreverht.db.service.repository.UnreadSubcriptionMRepository
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.R
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest
import com.foreveross.atwork.modules.chat.util.AudioRecord
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager
import com.foreveross.atwork.modules.newsSummary.activity.NewsSummarySearchActivity
import com.foreveross.atwork.modules.newsSummary.adapter.NewsSummaryRvAdapter
import com.foreveross.atwork.modules.newsSummary.data.CompareData
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.util.NewsSummaryHelper
import com.foreveross.atwork.modules.search.model.NewSearchControlAction
import com.foreveross.atwork.modules.search.model.SearchContent
import com.foreveross.atwork.support.BackHandledFragment
import java.util.*
import kotlin.collections.ArrayList

class NewsSummaryFragment : BackHandledFragment() {

    private lateinit var rvNewsSummary: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var rlSearch: RelativeLayout
    private lateinit var newsSummaryRvAdapter: NewsSummaryRvAdapter
    private val newsSummaryRvList = ArrayList<NewsSummaryRVData>()
    private val newsSummaryTotleRvList = ArrayList<NewsSummaryRVData>()
    private var page = 0
    private var isFinishLoading = false
    //控制是否是添加或刷新
    private var canAdd = true
    private val messageList = ArrayList<NewsSummaryPostMessage>()
    var progressDialogHelper: ProgressDialogHelper? = null

    companion object {
        const val NEWS_SUMMARY_CLICK = "news_summary_click"
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        registerListener()
        getAppData()
    }

    override fun onResume() {
        super.onResume()
        getOftenReadData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_summary, container, false)
    }

    override fun onStop() {
        super.onStop()
        AudioRecord.stopPlaying()
    }

    override fun onPause() {
        super.onPause()
        AudioRecord.stopPlaying()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.tv_select_title)
        ivBack = view.findViewById(R.id.title_bar_news_summary_back)
        rvNewsSummary = view.findViewById(R.id.rvNewsSummary)
        rlSearch = view.findViewById(R.id.rl_search)
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioRecord.stopPlaying()
    }

    private fun initUi() {

        progressDialogHelper = ProgressDialogHelper(activity)
        newsSummaryRvList.clear()
        newsSummaryTotleRvList.clear()

        val layoutManager = LinearLayoutManager(this.context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvNewsSummary.layoutManager = layoutManager
//        rvNewsSummary.setItemViewCacheSize(10)
//        rvNewsSummary.setHasFixedSize(true)
        rvNewsSummary.isNestedScrollingEnabled = false
        newsSummaryRvAdapter = NewsSummaryRvAdapter(context!!, this@NewsSummaryFragment,newsSummaryTotleRvList)
        newsSummaryRvAdapter.setOnKotlinItemClickListener(object : NewsSummaryRvAdapter.IKotlinItemClickListener {
            override fun onItemClickListener(newsSummaryRVData: NewsSummaryRVData) {
                NewsSummaryHelper.selectApp(mActivity,newsSummaryRVData,object : NewsSummaryHelper.CallBack{
                    override fun onResult(mSession: Session?, mApp: App?) {
                        if(mSession != null){
                            doRouteChatDetailView(newsSummaryRVData, mSession)
                            return
                        }
                        if(mApp != null){
                            doRouteChatDetailView(newsSummaryRVData, mApp)
                            return
                        }
                        if(isAdded) {
                            toast(getString(R.string.session_invalid_app_no))
                        }
                    }
                })
            }
        })
        rvNewsSummary.adapter = newsSummaryRvAdapter
        rvNewsSummary.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            //标记是否为向上滑动
            var isSlidingUpward = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isSlidingUpward = dy > 0
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val manager = recyclerView.layoutManager as LinearLayoutManager
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val lastPosition = manager.findLastCompletelyVisibleItemPosition()
                    val itemCount = manager.itemCount
                    if(lastPosition == (itemCount - 1) && isSlidingUpward && !isFinishLoading){
                        newsSummaryRvAdapter.setEndLoading(true)
                        getMessageData(MessageAppRepository.LIMIT_DEFAULT,page)
                    }
                }
            }

        })
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }
        rlSearch.setOnClickListener {
            val searchContentList: MutableList<SearchContent> = java.util.ArrayList()
            searchContentList.add(SearchContent.SEARCH_NS_SERVICE)
            searchContentList.add(SearchContent.SEARCH_NS_ARTICLE)
            val newSearchControlAction = NewSearchControlAction()
            newSearchControlAction.searchContentList = searchContentList.toTypedArray()

            val intent = NewsSummarySearchActivity.getIntent(context, newSearchControlAction)
            startActivity(intent)
        }
    }

    private fun doRouteChatDetailView(newsSummaryRVData: NewsSummaryRVData,mSession: Session) {
        if (isAdded) {
            val entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Service, mSession).setOrgId(mSession.orgId)
            ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest)
            val intent = ChatDetailActivity.getIntent(context, newsSummaryRVData.getChatId())
            intent.putExtra(ChatDetailFragment.RETURN_BACK, true)
            intent.putExtra(NEWS_SUMMARY_CLICK,true)
            startActivity(intent)
            UnreadSubcriptionMRepository.getInstance().removeByAppId(mSession.identifier)
            //更新点击率
            ClickStatisticsManager.updateClick(mSession.identifier, Type.NEWS_SUMMARY)
        }
    }

    private fun doRouteChatDetailView(newsSummaryRVData: NewsSummaryRVData,mApp: App) {
        if (isAdded) {
            val entrySessionRequest = EntrySessionRequest.newRequest(SessionType.Service, mApp).setOrgId(mApp.mOrgId)
            ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest)
            val intent = ChatDetailActivity.getIntent(context, newsSummaryRVData.getChatId())
            intent.putExtra(ChatDetailFragment.RETURN_BACK, true)
            intent.putExtra(NEWS_SUMMARY_CLICK,true)
            startActivity(intent)
            UnreadSubcriptionMRepository.getInstance().removeByAppId(mApp.id)
            //更新点击率
            ClickStatisticsManager.updateClick(mApp.id, Type.NEWS_SUMMARY)
        }
    }

    private fun getAppData(){
        newsSummaryRvList.clear()
        newsSummaryTotleRvList.clear()
        progressDialogHelper?.show()
        page = MessageAppRepository.OFFSET_DEFAULT
        getMessageData(MessageAppRepository.LIMIT_DEFAULT,page)

    }

    @SuppressLint("StaticFieldLeak")
    private fun getMessageData(limit: Int, offset: Int) {
        val newsSummaryRvList = ArrayList<NewsSummaryRVData>()
        object : AsyncTask<Void?, Void?, Int>() {

            override fun doInBackground(vararg p0: Void?): Int {
                messageList.clear()
                for (newsSummary in MessageAppRepository.getInstance().queryTotleMessages(context,limit,offset,"")) {
                    messageList.add(newsSummary)
                }
                for (i in 0 until messageList.size) {
                    val newsSummaryRVData = NewsSummaryRVData()
                    when(messageList[i].getChatPostMessage().chatType){
                        ChatPostMessage.ChatType.Text -> {
                            newsSummaryRVData.type = NewsSummaryRVData.WORDS
                            newsSummaryRVData.chatPostMessage = messageList[i].getChatPostMessage()
                            newsSummaryRVData.chatId = messageList[i].chatId
                            newsSummaryRvList.add(newsSummaryRVData)
                        }
                        ChatPostMessage.ChatType.Article -> {
                            newsSummaryRVData.type = NewsSummaryRVData.IMG_WORDS
                            newsSummaryRVData.chatPostMessage = messageList[i].getChatPostMessage()
                            newsSummaryRVData.chatId = messageList[i].chatId
                            newsSummaryRvList.add(newsSummaryRVData)
                        }
                        ChatPostMessage.ChatType.File -> {
                            newsSummaryRVData.type = NewsSummaryRVData.FILE
                            newsSummaryRVData.chatPostMessage = messageList[i].getChatPostMessage()
                            newsSummaryRVData.chatId = messageList[i].chatId
                            newsSummaryRvList.add(newsSummaryRVData)
                        }
                        ChatPostMessage.ChatType.Voice -> {
                            newsSummaryRVData.type = NewsSummaryRVData.VOICE
                            newsSummaryRVData.chatPostMessage = messageList[i].getChatPostMessage()
                            newsSummaryRVData.chatId = messageList[i].chatId
                            newsSummaryRvList.add(newsSummaryRVData)
                        }
                        ChatPostMessage.ChatType.MicroVideo -> {
                            newsSummaryRVData.type = NewsSummaryRVData.VIDEO
                            newsSummaryRVData.chatPostMessage = messageList[i].getChatPostMessage()
                            newsSummaryRVData.chatId = messageList[i].chatId
                            newsSummaryRvList.add(newsSummaryRVData)
                        }
                        ChatPostMessage.ChatType.Image -> {
                            newsSummaryRVData.type = NewsSummaryRVData.IMG_WORD
                            newsSummaryRVData.chatPostMessage = messageList[i].getChatPostMessage()
                            newsSummaryRVData.chatId = messageList[i].chatId
                            newsSummaryRvList.add(newsSummaryRVData)
                        }
                    }
                }
                if(newsSummaryTotleRvList.size > 0 && newsSummaryTotleRvList[newsSummaryTotleRvList.size-1].getType() == NewsSummaryRVData.END){
                    newsSummaryTotleRvList.remove(newsSummaryTotleRvList[newsSummaryTotleRvList.size-1])
                }
                newsSummaryTotleRvList.addAll(newsSummaryTotleRvList.size,newsSummaryRvList)
                return messageList.size
            }

            override fun onPostExecute(listSize: Int) {
                progressDialogHelper?.dismiss()
                if(listSize > 0){
                    if(listSize >= MessageAppRepository.LIMIT_DEFAULT){
//                        newsSummaryRvAdapter.notifyDataSetChanged()
                        page += MessageAppRepository.LIMIT_DEFAULT
                        isFinishLoading = false
//                        return
                    }else{
                        isFinishLoading = true
                    }
                }

                val endRVData = NewsSummaryRVData()
                endRVData.type = NewsSummaryRVData.END
                newsSummaryTotleRvList.add(newsSummaryTotleRvList.size,endRVData)
                newsSummaryRvAdapter.notifyDataSetChanged()
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    @SuppressLint("StaticFieldLeak")
    private fun getOftenReadData(){
        object : AsyncTask<Void?, Void?, ArrayList<CompareData>>() {

            override fun doInBackground(vararg p0: Void?): ArrayList<CompareData> {
                val allClickEvent = ClickStatisticsRepository.getInstance().queryClickEventsForNS(Type.NEWS_SUMMARY)
                val compareList: ArrayList<CompareData> = ArrayList()
                for (clickEvent in allClickEvent) {
                    var isNewData = true
                    val compareData = CompareData()
                    if(compareList.size == 0){
                        compareData.clickCount = 1
                        compareData.id = clickEvent.id
                        compareList.add(compareData)
                    }else {
                        for (data in compareList) {
                            if (data.id == clickEvent.id) {
                                data.clickCount = data.clickCount + 1
                                isNewData = false
                            }
                        }
                        if(isNewData){
                            compareData.clickCount = 1
                            compareData.id = clickEvent.id
                            compareList.add(compareData)
                        }
                    }
                }
                compareList.sortWith(Comparator { t2, t1 -> t1.clickCount.compareTo(t2.clickCount) })
                return compareList
            }

            override fun onPostExecute(compareList: ArrayList<CompareData>) {
                val oftenReadList: ArrayList<App> = ArrayList()
                for (appId in compareList) {
                    AppManager.getInstance().queryApp(context, appId.id, "", object : AppManager.GetAppFromMultiListener {
                        override fun onSuccess(app: App) {
                            oftenReadList.add(app)
                            val oftenReadRVData = NewsSummaryRVData()
                            oftenReadRVData.type = NewsSummaryRVData.OFTEN_READ
                            oftenReadRVData.setAppList(oftenReadList)
                            if(canAdd) {
                                canAdd = false
                                newsSummaryTotleRvList.add(0, oftenReadRVData)
                            }else{
                                newsSummaryTotleRvList[0].setAppList(oftenReadList)
                            }
                            newsSummaryRvAdapter.notifyItemChanged(0)
                        }

                        override fun networkFail(errorCode: Int, errorMsg: String?) {
                        }

                    })

                }
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

}