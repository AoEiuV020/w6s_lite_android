package com.foreverht.workplus.module.chat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.module.chat.SearchMessageType
import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity
import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity.Companion.MESSAGE_HISTORY_TYPE
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.modules.chat.adapter.MessageHistoryAdapter
import com.foreveross.atwork.modules.chat.component.history.MessageHistoryLoadMoreView

class MessageByTypeFragment: BaseHistoryMessageFragment() {

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var rvMessageList: RecyclerView
    private lateinit var nothingView: View

    private lateinit var type: SearchMessageType


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_by_type, container, false)
    }

    override fun findViews(view: View) {
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        rvMessageList = view.findViewById(R.id.rv_message_list)
        nothingView = view.findViewById(R.id.layout_no_history_message)
    }

    override fun getNoMessagesView(): View? {
        return nothingView
    }

    override fun getMessageListView(): RecyclerView? {
        return rvMessageList
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViewUI()
        registerListener()
        startFetchMessageData()
    }

    private fun initData() {
        val bundle = arguments
        bundle?.apply {
            messageHistoryViewAction = getParcelable(BaseMessageHistoryActivity.DATA_MESSAGE_HISTORY_VIEW_ACTION)
            type = getSerializable(MESSAGE_HISTORY_TYPE) as SearchMessageType
            messageType = type.toString()
        }

        messageHistoryAdapter = MessageHistoryAdapter(messageList)
        messageHistoryAdapter.setOnLoadMoreListener(this, rvMessageList)
        messageHistoryAdapter.setLoadMoreView(MessageHistoryLoadMoreView())
        rvMessageList.adapter = messageHistoryAdapter
    }

    private fun initViewUI() {
        var title = when (type) {
            SearchMessageType.voice -> getString(R.string.audio2)
            SearchMessageType.text -> getString(R.string.text)
            SearchMessageType.image -> getString(R.string.image)
            SearchMessageType.file -> getString(R.string.file)
            SearchMessageType.video -> getString(R.string.video2)
            SearchMessageType.article -> getString(R.string.article_tag)
        }

        tvTitle.text = title
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }


        messageHistoryAdapter.setOnItemClickListener { _, _, position ->
            val msg = messageList[position]
            when(msg) {
                is ImageChatMessage -> showMediaSwitchFragment(msg)
                is FileTransferChatMessage -> handleShowFile(msg)
                is ArticleChatMessage -> showArticleItem(msg)
                is MicroVideoChatMessage ->  handleShowVideo(msg)
                is VoiceChatMessage -> handlePlayVoice(msg, position)

            }
        }
    }


}