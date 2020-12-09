package com.foreverht.workplus.module.chat.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.module.chat.SearchMessageType
import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity
import com.foreveross.atwork.R
import com.foreveross.atwork.component.ExpandView.ALL_TAG_ID
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.modules.chat.adapter.MessageHistoryAdapter
import com.foreveross.atwork.modules.chat.component.history.MessageHistoryLoadMoreView
import com.foreveross.atwork.utils.AtworkUtil
import java.util.*

class MessageByTagFragment: BaseHistoryMessageFragment() {

    var searchView:     View?       = null
    var backBtn:        ImageView?  = null
    var tvCancel:       TextView?   = null
    var editText:       EditText?   = null
    var clearBtn:       ImageView?  = null
    var searchTagView:  TextView?   = null

    private var tvArticle: TextView? = null
    private var tvVideo:   TextView? = null
    private var tvFile:    TextView? = null
    private var tvImage:   TextView? = null
    private var tvText:    TextView? = null
    private var tvVoice:   TextView? = null

    var rvMessageList:  RecyclerView? = null
    var tagsLayout:     View?       = null
    var nothingView:    View?       = null
    var tagTitle:       TextView?   = null

    var globalSearchKey:    String          = ""
    var globalSearchValue:  String?         = ""
    var globalTag:          SearchMessageType?     = null
    var searchRunnable:     SearchRunnable? = null


    var handler = object: Handler() {

        override fun handleMessage(msg: Message?) {
            val status = msg?.what
            when (status) {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_by_tag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        registerListener()
    }


    override fun onDestroy() {
        AtworkUtil.hideInput(activity)
        handler.removeCallbacks(searchRunnable)
        super.onDestroy()
    }

    override fun findViews(view: View?) {
        searchView = view?.findViewById(R.id.text_search_titlebar)
        tvCancel = searchView?.findViewById(R.id.title_bar_common_left_title)
        tvCancel?.setText(R.string.cancel)
        backBtn = searchView?.findViewById(R.id.title_bar_chat_search_back)
        editText = searchView?.findViewById(R.id.title_bar_chat_search_key)
        searchTagView = searchView?.findViewById(R.id.favorite_search_tag_tv)
        clearBtn = searchView?.findViewById(R.id.title_bar_chat_search_cancel)
        clearBtn?.visibility = View.GONE
        rvMessageList = view?.findViewById(R.id.rv_message_list)
        tagsLayout = view?.findViewById(R.id.tags_layout)
        tagTitle = tagsLayout?.findViewById(R.id.tag_id_title)
        nothingView = view?.findViewById(R.id.layout_no_history_message)

        tvArticle = tagsLayout?.findViewById(R.id.tv_article)
        tvVideo = tagsLayout?.findViewById(R.id.tv_video)
        tvFile = tagsLayout?.findViewById(R.id.tv_file)
        tvImage = tagsLayout?.findViewById(R.id.tv_image)
        tvText = tagsLayout?.findViewById(R.id.tv_text)
        tvVoice = tagsLayout?.findViewById(R.id.tv_audio)
    }

    override fun getNoMessagesView(): View? {
        return nothingView
    }

    override fun getMessageListView(): RecyclerView? {
        return rvMessageList
    }

    private fun initData() {
        val bundle = arguments
        bundle?.apply {
            messageHistoryViewAction = getParcelable(BaseMessageHistoryActivity.DATA_MESSAGE_HISTORY_VIEW_ACTION)
            tagId = messageHistoryViewAction?.selectedTag?.tagId
            var tagName: String?
            if (ALL_TAG_ID == messageHistoryViewAction?.selectedTag?.tagId || messageHistoryViewAction?.selectedTag == null) {
                tagName = getString(R.string.all)
            } else {
                tagName = messageHistoryViewAction?.selectedTag?.getShowName(context!!)
            }
            tagTitle?.text = String.format(getString(R.string.search_by_tag), tagName)
        }

        messageHistoryAdapter = MessageHistoryAdapter(messageList)
        messageHistoryAdapter.setOnLoadMoreListener(this, rvMessageList)
        messageHistoryAdapter.setLoadMoreView(MessageHistoryLoadMoreView())
        rvMessageList?.adapter = messageHistoryAdapter

    }

    private fun registerListener() {

        editText?.requestFocus()

        rvMessageList?.setOnTouchListener { v, event ->
            AtworkUtil.hideInput(activity)
            false
        }

        backBtn?.setOnClickListener { onBackPressed() }


        clearBtn?.setOnClickListener {
            editText?.setText("")
        }

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

        clearBtn?.setOnClickListener {
            editText?.setText("")
        }


        editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && !StringUtils.isEmpty(editText?.getText().toString())) {
                clearBtn?.visibility = View.VISIBLE
            } else {
                clearBtn?.visibility = View.GONE
            }
        }

        editText?.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                search(s.toString())
                if (!StringUtils.isEmpty(s.toString())) {
                    clearBtn?.visibility = View.VISIBLE

                } else {
                    clearBtn?.visibility = View.GONE
                }

            }
        })

        editText?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                listenImeDel()
            }
            false
        }

        tvArticle?.setOnClickListener {
            onTypeClick(tvArticle, SearchMessageType.article)
        }

        tvVideo?.setOnClickListener {
            onTypeClick(tvVideo, SearchMessageType.video)
        }

        tvFile?.setOnClickListener {
            onTypeClick(tvFile, SearchMessageType.file)
        }

        tvImage?.setOnClickListener {
            onTypeClick(tvImage, SearchMessageType.image)
        }

        tvText?.setOnClickListener {
            onTypeClick(tvText, SearchMessageType.text)
        }

        tvVoice?.setOnClickListener {
            onTypeClick(tvVoice, SearchMessageType.voice)
        }
    }

    private fun listenImeDel() {
        if (TextUtils.isEmpty(editText?.text.toString()) && globalTag != null) {
            searchTagView?.text = ""
            globalTag = null
            resetData()
        }

        return;
    }

    fun resetData() {
        messageList.clear()
        messageHistoryAdapter.notifyDataSetChanged()
        rvMessageList?.visibility = View.GONE
        tagsLayout?.visibility = View.VISIBLE
        nothingView?.visibility = View.GONE
        keyword = ""
        tagId = ""
    }


    private fun search(searchValue: String) {
        globalSearchKey = UUID.randomUUID().toString()
        searchRunnable = SearchRunnable(globalSearchKey, searchValue)
        handler.postDelayed(searchRunnable, 200)
    }

    private fun onTypeClick(selectedView: TextView?, type: SearchMessageType) {
        globalTag = type
        searchTagView?.text = selectedView?.text
        messageType = globalTag.toString()
        tagsLayout?.visibility = View.GONE
        rvMessageList?.visibility = View.VISIBLE
        startFetchMessageData()

    }


    inner class SearchRunnable: Runnable {

        var searchKey:      String? = ""
        var searchValue:    String? = ""

        constructor(searchKey: String, searchValue: String)  {
            this.searchKey = searchKey
            this.searchValue = searchValue
        }
        override fun run() {
            if(globalSearchKey == this.searchKey) {
                if (TextUtils.isEmpty(searchValue) && globalTag == null) {
                    resetData()
                } else {
                    tagsLayout?.visibility = View.GONE
                    rvMessageList?.visibility = View.VISIBLE
                    keyword = searchValue!!
                    startFetchMessageData()
                }

            }
        }
    }


}