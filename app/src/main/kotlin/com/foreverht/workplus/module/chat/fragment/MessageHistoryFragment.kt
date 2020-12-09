package com.foreveross.atwork.modules.chat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.module.chat.MessageTagsLoader
import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity
import com.foreverht.workplus.module.chat.activity.MessageByTagActivity
import com.foreverht.workplus.module.chat.fragment.BaseHistoryMessageFragment
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService
import com.foreveross.atwork.component.ExpandView
import com.foreveross.atwork.component.ExpandView.ALL_TAG_ID
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.chat.adapter.MessageHistoryAdapter
import com.foreveross.atwork.modules.chat.component.history.MessageHistoryLoadMoreView
import com.foreveross.atwork.modules.chat.dao.ChatDaoService
import com.w6s.module.MessageTags

const val REQUEST_LIMIT = 10


class MessageHistoryFragment : BaseHistoryMessageFragment(), ExpandView.OnMessageTagSelectListener {


    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var ivSearch: ImageView
    private lateinit var rlExpandTitle: RelativeLayout
    private lateinit var rvMessageList: RecyclerView

    private lateinit var nothingView: View

    private lateinit var expandTitle: TextView
    private lateinit var expandView:  ExpandView
    private lateinit var downArrow: ImageView
    private lateinit var collapseView: ImageView

    val messageTagLoader = MessageTagLoaderManager()

    var allTagList = mutableListOf<MessageTags>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViewUI()
        registerListener()
        if (messageHistoryViewAction?.selectedTag != null) {
            onTagSelected(messageHistoryViewAction?.selectedTag )
        } else {
            startFetchMessageData()
        }


    }

    override fun onResume() {
        super.onResume()

    }


    override fun findViews(view: View) {
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivSearch = view.findViewById(R.id.title_bar_common_right_img)
        rlExpandTitle = view.findViewById(R.id.expand_title_layout)
        rvMessageList = view.findViewById(R.id.rv_message_list)
        progressDialogHelper = ProgressDialogHelper(activity)
        nothingView = view.findViewById(R.id.layout_no_history_message)
        expandTitle = view.findViewById(R.id.tag_selected_title)
        expandView = view.findViewById(R.id.view_expand)
        expandView.setOnTagSelectListener(this)
        downArrow = view.findViewById(R.id.down_arrow)
        collapseView = view.findViewById(R.id.collapse_iv)
    }

    override fun getNoMessagesView(): View {
        return nothingView
    }

    override fun getMessageListView(): RecyclerView {
        return rvMessageList
    }

    private fun initData() {
        val bundle = arguments
        bundle?.apply {
            messageHistoryViewAction = getParcelable(BaseMessageHistoryActivity.DATA_MESSAGE_HISTORY_VIEW_ACTION)

        }

        messageHistoryAdapter = MessageHistoryAdapter(messageList)
        messageHistoryAdapter.setOnLoadMoreListener(this, rvMessageList)
        messageHistoryAdapter.setLoadMoreView(MessageHistoryLoadMoreView())
        rvMessageList.adapter = messageHistoryAdapter


        if(AtworkConfig.SERVICE_APP_HISTORICAL_MESSAGE_CONFIG.tags) {
            rlExpandTitle.isVisible = true

            loaderManager.initLoader(0,null, messageTagLoader).forceLoad()
            getRemoteTags()
        }
    }

    private fun initViewUI() {
        tvTitle.setText(R.string.history_messages)

        if (AtworkConfig.SERVICE_APP_HISTORICAL_MESSAGE_CONFIG.searchable) {
            ivSearch.visibility = View.VISIBLE
            ivSearch.background = ContextCompat.getDrawable(AtworkApplicationLike.baseContext, R.mipmap.icon_search_dark)
        }
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        messageHistoryAdapter.setOnItemClickListener { adapter, view, position ->
            val msg = messageList[position]
            when(msg) {
                is ImageChatMessage -> showMediaSwitchFragment(msg)
                is FileTransferChatMessage -> handleShowFile(msg)
                is ArticleChatMessage -> showArticleItem(msg)
                is MicroVideoChatMessage ->  handleShowVideo(msg)
                is VoiceChatMessage -> handlePlayVoice(msg, position)

            }
        }

        ivSearch.setOnClickListener {
            startActivity(MessageByTagActivity.getIntent(context!!, messageHistoryViewAction!!))
        }

        expandTitle.setOnClickListener {
            if (expandView.isExpand) {
                collapse()
            } else {
                expand()
            }
        }
        collapseView.setOnClickListener {
            collapse()
        }
        downArrow.setOnClickListener {
            expand()
        }
    }

    private fun expand() {
        expandView.expand()
        downArrow.visibility = View.INVISIBLE
    }

    private fun collapse() {
        expandView.collapse()
        downArrow.visibility = View.VISIBLE
    }

    override fun onTagSelected(messageTags: MessageTags?) {
        messageHistoryViewAction?.selectedTag = messageTags
        var title: String? = ""
        if (ALL_TAG_ID == messageTags?.tagId) {
            title = context?.getString(R.string.all)
            tagId = ""
        } else {
            title = messageTags?.getShowName(context!!)
            tagId = messageTags!!.tagId
        }
        expandTitle.text = title
        downArrow.visibility = View.VISIBLE
        startFetchMessageData()
    }

    private fun getRemoteTags() {
        MessageAsyncNetService.queryMessageTags(context, messageHistoryViewAction?.app?.mOrgId, messageHistoryViewAction?.app?.mAppId, object: MessageAsyncNetService.OnMessageTagsListener{
            override fun getMessageTagsSuccess(tags: List<MessageTags>) {
                ChatDaoService.getInstance().saveTags(tags)
                initTagData(tags)
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {

            }
        })
    }



    private fun initTagData(data: List<MessageTags>) {
        if (ListUtil.isEmpty(data)) {
            expandTitle.visibility = View.GONE
            expandView.visibility = View.GONE
            downArrow.visibility = View.GONE
            return
        }
        expandTitle.visibility = View.VISIBLE
        expandView.setMessageTags(data)
        if (messageHistoryViewAction?.selectedTag != null) {
            expandView.setSelectedTag(messageHistoryViewAction?.selectedTag )
        }
    }


    inner class MessageTagLoaderManager() : LoaderManager.LoaderCallbacks<List<MessageTags>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<MessageTags>> {
            return MessageTagsLoader(context!!, messageHistoryViewAction!!.app)
        }

        override fun onLoadFinished(p0: Loader<List<MessageTags>>, data: List<MessageTags>) {
            allTagList = data as MutableList<MessageTags>;
            initTagData(data)
        }

        override fun onLoaderReset(p0: Loader<List<MessageTags>>) {
        }

    }


}