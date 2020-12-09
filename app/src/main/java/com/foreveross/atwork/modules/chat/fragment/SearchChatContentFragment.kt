package com.foreveross.atwork.modules.chat.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.*
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.chat.activity.INTENT_SESSION_ID
import com.foreveross.atwork.modules.chat.activity.SearchChatByCalendarActivity
import com.foreveross.atwork.modules.chat.activity.SearchChatMediaActivity
import com.foreveross.atwork.modules.chat.adapter.SearchMessageContentAdapter
import com.foreveross.atwork.modules.chat.dao.ChatDaoService
import com.foreveross.atwork.modules.chat.data.SearchMessageItemData
import com.foreveross.atwork.modules.chat.data.SearchMessageTimeLineList
import com.foreveross.atwork.modules.chat.util.ShareMsgHelper
import com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_CHAT_LIST
import com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_FILE_DETAIL
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.atwork.utils.OfficeHelper
import kotlinx.android.synthetic.main.fragment_search_chat_content.*
import kotlinx.android.synthetic.main.title_bar_new_search.*
import java.io.File

/**
 *  create by reyzhang22 at 2020-02-10
 */
class SearchChatContentFragment : BackHandledFragment() {

    private lateinit var etSearch:          EditText
    private lateinit var tvSearchHint:      TextView
    private lateinit var groupSearchType:   View
    private lateinit var rvSearchList:      RecyclerView
    private lateinit var viewNoData:        View

    private lateinit var sessionId: String
    private lateinit var adapter: SearchMessageContentAdapter
    private val resultList = SearchMessageTimeLineList<SearchMessageItemData>()

    private var searchType: String = TEXT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_chat_content, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViews(view)
        initData()
        registerListener()

        etSearch.postDelayed(Runnable { AtworkUtil.showInput(mActivity, etSearch) }, 100)

    }

    override fun findViews(view: View) {
        etSearch = view.findViewById(R.id.et_search_content)
        tvSearchHint = view.findViewById(R.id.tv_search_hint)
        groupSearchType = view.findViewById(R.id.group_search_type)
        rvSearchList = view.findViewById(R.id.rv_searched_list)
        viewNoData = view.findViewById(R.id.ll_no_result)
        iv_back.visibility = GONE
    }

    private fun initData() {
        sessionId = arguments?.get(INTENT_SESSION_ID) as String? ?: return
        adapter = SearchMessageContentAdapter(resultList)
        rvSearchList.adapter = adapter

        adapter.setOnItemClickListener { _, _, position ->
            val itemData = resultList[position] as SearchMessageItemData
            if (itemData.mIsTimeLine) {
                return@setOnItemClickListener
            }
            if (searchType == FILE && itemData.mMessage is FileTransferChatMessage) {
                val fileTransferChatMessage = itemData.mMessage as FileTransferChatMessage
                if (shouldPreviewLocal(fileTransferChatMessage)) {
                    previewLocal(fileTransferChatMessage, VIEW_FROM_CHAT_LIST)
                } else {
                    showFileStatusFragment(fileTransferChatMessage)
                }
                return@setOnItemClickListener
            }
            if (searchType == SHARE && itemData.mMessage is ShareChatMessage) {
                val shareChatMessage = itemData.mMessage as ShareChatMessage
                if (shareChatMessage.shareType == ShareChatMessage.ShareType.Link.toString()) {
                    ShareMsgHelper.jumpLinkShare(context, shareChatMessage)
                    return@setOnItemClickListener
                }
            }
            val intent = ChatDetailActivity.getIntent(context, sessionId, itemData.mMessage.deliveryId)
            startActivity(intent)
        }
    }


    private fun registerListener() {
        tv_cancel.setOnClickListener {
            onBackPressed()
        }

        iv_icon_clear.setOnClickListener {
            resetStatus()
        }

        etSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isEmpty(s)) {
                    tv_search_text_hint.visibility = View.GONE
                    iv_icon_clear.visibility = View.GONE
                    return
                }
                iv_icon_clear.visibility = View.VISIBLE
                tv_search_text_hint.visibility = View.VISIBLE
                tv_search_text_hint.text = getString(R.string.please_search, s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.action == ACTION_UP) {
                    if (TextUtils.isEmpty(etSearch.text)) {
                        AtworkToast.showToast(getString(R.string.please_input_search_keyword))
                    } else {
                        AtworkUtil.hideInput(mActivity)
                        search()
                    }
                }
            }

            false
        }


        tv_type_date.setOnClickListener {
            SearchChatByCalendarActivity.startActivity(mActivity, sessionId)
        }

        tv_type_media.setOnClickListener {
            SearchChatMediaActivity.startActivity(mActivity, sessionId)
        }

        tv_type_file.setOnClickListener {
            tv_search_hint.text = getString(R.string.label_file_chat_pop)
            onSearchTypeClicked(FILE)
            search()
        }

        tv_type_link.setOnClickListener {
            tv_search_hint.text = getString(R.string.message_type_link)
            onSearchTypeClicked(SHARE)
            search()
        }

    }

    private fun onSearchTypeClicked(type: String) {
        searchType = type
        iv_icon_clear.visibility = VISIBLE
        resultList.setNeedTimeLine(true)
    }

    private fun search() {
        resultList.clear()
        val list = mutableListOf<SearchMessageItemData>()
        if (searchType == TEXT) {
            ChatDaoService.getInstance().searchMessages(BaseApplicationLike.baseContext, "", etSearch.text.toString(), sessionId) {_, messages ->
                for (message in messages) {
                    val stringBuilder = StringBuilder()
                    if (message is ShareChatMessage) {
                        if (!(message.shareType == ShareChatMessage.ShareType.Link.toString())) {
                            continue
                        }

                        stringBuilder.append("[").append(getString(R.string.message_type_link)).append("]")
                        if (null != message.mArticleItem) {
                            stringBuilder.append(message.mArticleItem.title)
                            if (!TextUtils.isEmpty(message.mArticleItem.summary)) {
                                stringBuilder.append(" | ").append(message.mArticleItem.summary)
                            }
                        }
                        message.showableString = stringBuilder.toString()
                    }
                    if (message is FileTransferChatMessage) {
                        stringBuilder.append("[").append(getString(R.string.file)).append("]")
                        stringBuilder.append(message.name)
                        message.showableString = stringBuilder.toString()
                    }
                    if (message is AnnoImageChatMessage) {
                        stringBuilder.append("[").append(getString(R.string.image)).append("]")
                        stringBuilder.append(message.comment)
                        message.showableString = stringBuilder.toString()
                    }
                    if (message is AnnoFileTransferChatMessage) {
                        stringBuilder.append("[").append(getString(R.string.file)).append("]")
                        stringBuilder.append(message.comment)
                        message.showableString = stringBuilder.toString()
                    }

                    val data = SearchMessageItemData()
                    data.apply {
                        this.mName = message.mMyName
                        this.mIsTimeLine = false
                        this.mType = TEXT
                        this.mMessage = message
                        this.msgTime = message.deliveryTime
                    }
                    list.add(data)
                }
                compatMessageData(list)
            }
            return
        }
        ChatDaoService.getInstance().searchMessagesByMessageType(BaseApplicationLike.baseContext, "", etSearch.text.toString(), sessionId, searchType) { _, messages ->
            for (message in messages) {
                if (message is ShareChatMessage) {
                    if (!(message.shareType == ShareChatMessage.ShareType.Link.toString())) {
                        continue
                    }
                }
                val data = SearchMessageItemData()
                data.apply {
                    this.mName = message.mMyName
                    this.mIsTimeLine = false
                    this.mType = searchType
                    this.mMessage = message
                    this.msgTime = message.deliveryTime
                }
                list.add(data)
            }
            compatMessageData(list)
        }
    }

    private fun compatMessageData(list: MutableList<SearchMessageItemData>) {
        resultList.addAll(list.apply { sortByDescending { it.msgTime } })

        viewNoData.visibility = if (resultList.isEmpty()) VISIBLE else GONE
        adapter.setKeywords(etSearch.text.toString())
        adapter.notifyDataSetChanged()
        group_search_type.visibility = GONE
    }

    private fun resetStatus() {
        iv_icon_clear.visibility = GONE
        group_search_type.visibility = VISIBLE
        tv_search_text_hint.visibility = GONE
        viewNoData.visibility = GONE
        tv_search_hint.text = ""
        searchType = TEXT
        etSearch.setText("")
        resultList.clear()
        adapter.apply {
            setKeywords("")
            notifyDataSetChanged()
        }
    }

    override fun onBackPressed(): Boolean {
        AtworkUtil.hideInput(mActivity)
        mActivity.finish()
        return true
    }

    private fun showFileStatusFragment(fileTransferChatMessage: FileTransferChatMessage) {
        val fileStatusFragment = FileStatusFragment()
        fileStatusFragment.initBundle(sessionId, fileTransferChatMessage, null, VIEW_FROM_FILE_DETAIL)
        fileStatusFragment.show(childFragmentManager, "FILE_DIALOG")

        AtworkUtil.hideInput(activity, etSearch)
    }

    fun previewLocal(fileTransferChatMessage: FileTransferChatMessage, intentType: Int) {
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(fileTransferChatMessage.filePath, false) { fileName ->
            OfficeHelper.previewByX5(context, fileName, sessionId, fileTransferChatMessage, intentType)
        }
    }

    private fun shouldPreviewLocal(fileTransferChatMessage: FileTransferChatMessage): Boolean {
        if (!OfficeHelper.isSupportType(fileTransferChatMessage.filePath)) {
            return false
        }
        return isFileExist(fileTransferChatMessage)
    }

    private fun isFileExist(fileTransferChatMessage: FileTransferChatMessage): Boolean {
        if (TextUtils.isEmpty(fileTransferChatMessage.filePath)) {
            return false
        }
        val file = File(fileTransferChatMessage.filePath)
        return file.exists()
    }



}