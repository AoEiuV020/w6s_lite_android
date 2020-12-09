package com.foreveross.atwork.modules.group.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.contact.ContactViewMode
import com.foreveross.atwork.infrastructure.model.discussion.Discussion
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.CommonUtil
import com.foreveross.atwork.infrastructure.utils.ContactHelper
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.manager.OrganizationManager
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.service.ChatService
import com.foreveross.atwork.modules.common.component.CommonItemView
import com.foreveross.atwork.modules.contact.component.ContactListItemView
import com.foreveross.atwork.modules.contact.component.ContactTitleView
import com.foreveross.atwork.modules.file.service.FileTransferService
import com.foreveross.atwork.modules.discussion.activity.SelectDiscussionListActivity
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.group.adaptar.RecentContactAdapter
import com.foreveross.atwork.modules.group.component.SelectContactHead
import com.foreveross.atwork.modules.group.listener.SelectedChangedListener
import com.foreveross.atwork.modules.discussion.model.DiscussionSelectControlAction
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction
import com.foreveross.atwork.modules.group.module.TransferMessageMode
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService
import com.foreveross.atwork.modules.search.activity.NewSearchActivity
import com.foreveross.atwork.modules.search.adapter.SearchListAdapter
import com.foreveross.atwork.modules.search.model.NewSearchControlAction
import com.foreveross.atwork.modules.search.model.SearchAction
import com.foreveross.atwork.modules.search.model.SearchContent
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.atwork.utils.ErrorHandleUtil

class TransferMessageFragment : BackHandledFragment(), SelectedChangedListener {


    private lateinit var tvTitle: TextView
    private lateinit var tvLeftTest: TextView
    private lateinit var tvRightest: TextView
    private lateinit var ivBack: ImageView
    private lateinit var rvRecentContacts: RecyclerView
    private lateinit var vSearchHeader: SelectContactHead
    private lateinit var lvSearchResult: ListView
    private lateinit var llSearchNoResult: LinearLayout
    private lateinit var vSelectDiscussion: CommonItemView
    private lateinit var vSelectContact: CommonItemView
    private lateinit var vContactTitleView: ContactTitleView
    private lateinit var vDeviceTitleView: ContactTitleView
    private lateinit var vFileTransferHeadView: ContactListItemView

    private var recentContactAdapter: RecentContactAdapter? = null
    private var searchResultAdapter: SearchListAdapter? = null
    private var singleMode = true
    private val sessionContactList = ArrayList<ShowListItem>()
    private val selectedChangedListeners = ArrayList<SelectedChangedListener>()

    private var transferMessageControlAction: TransferMessageControlAction? = null
    private var transferMessageMode = TransferMessageMode.FORWARD
    private var transferMessages: List<ChatPostMessage>? = null

    private var hasKeyBoard = false
    private var isFileTransferSelect = false

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (SelectToHandleActionService.ACTION_HANDLE_SELECT == action) {
                val contact = intent.getParcelableArrayListExtra<ShowListItem>(SelectToHandleActionService.DATA_SELECT_CONTACTS)
                val select = intent.getBooleanExtra(SelectToHandleActionService.DATA_SELECT_STATUS, false)
                action(contact, select)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerBroadcast()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_message, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initData()

        initSelectedChangedListener()
        registerListener()
    }

    override fun onStart() {
        super.onStart()

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadSessions()

    }

    override fun onDestroy() {
        super.onDestroy()

        SelectToHandleActionService.clear()

        unregisterBroadcast()
    }


    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        tvLeftTest = view.findViewById(R.id.title_bar_common_left_title)
        tvRightest = view.findViewById(R.id.title_bar_common_right_text)
        ivBack = view.findViewById(R.id.title_bar_common_back)
        rvRecentContacts = view.findViewById(R.id.rl_recent_contacts)
        vSearchHeader = view.findViewById(R.id.layout_select_user_contact_head)
        lvSearchResult = view.findViewById(R.id.lv_search_result)
        llSearchNoResult = view.findViewById(R.id.ll_no_result)


    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

    override fun selectContact(contactSelected: ShowListItem?) {
        if (null != contactSelected) {
            selectContactList(ListUtil.makeSingleList(contactSelected))
        }
    }

    override fun unSelectedContact(contactUnselected: ShowListItem?) {
        if (null != contactUnselected) {

            unSelectedContactList(ListUtil.makeSingleList(contactUnselected))
        }
    }

    override fun selectContactList(contactSelectedList: MutableList<out ShowListItem>?) {
        for (contactItem in sessionContactList) {
            if (contactSelectedList != null) {

                for (contactSelect in contactSelectedList) {
                    if (contactItem.id == contactSelect.id) {
                        contactItem.select(true)
                    }

                }
            }
        }

        recentContactAdapter?.notifyDataSetChanged()
    }

    override fun unSelectedContactList(contactUnselectedList: MutableList<out ShowListItem>?) {
        for (contactItem in sessionContactList) {
            if (contactUnselectedList != null) {

                for (contactUnselected in contactUnselectedList) {
                    if (contactItem.id == contactUnselected.id) {
                        contactItem.select(false)
                    }

                }
            }
        }

        recentContactAdapter?.notifyDataSetChanged()


    }


    /**
     * 初始化选择变更监听
     */
    private fun initSelectedChangedListener() {
        selectedChangedListeners.add(vSearchHeader)
        selectedChangedListeners.add(this)
    }


    private fun registerBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(SelectToHandleActionService.ACTION_HANDLE_SELECT)

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(broadcastReceiver)

    }


    private fun action(contactList: List<ShowListItem>, select: Boolean) {
        for (contactItem in contactList) {
            contactItem.select(select)
        }

        for (selectedChangedListener in selectedChangedListeners) {
            if (select) {
                selectedChangedListener.selectContactList(contactList)


            } else {
                selectedChangedListener.unSelectedContactList(contactList)

            }
        }



        if (1 == contactList.size) {
            if (FileTransferService.isClickFileTransfer(contactList[0])) {
                refreshFileTransferHeaderViewSelectStatus(select)
            }
        }

        updateButtonNum()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun registerListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        tvLeftTest.setOnClickListener {
            singleMode = true
            vSearchHeader.clearInput()
            AtworkUtil.hideInput(activity, vSearchHeader.mSearchText)

            clearAllSelectStatus()

        }


        tvRightest.setOnClickListener {
            if (singleMode) {
                singleMode = false

                recentContactAdapter?.singleMode = singleMode
                recentContactAdapter?.notifyDataSetChanged()

                refreshUI()

            } else {

                if(CommonUtil.isFastClick(1000)) {
                    return@setOnClickListener
                }

                if (!ListUtil.isEmpty(SelectToHandleActionService.contactList)) {
                    SelectToHandleActionService.action(activity, transferMessageControlAction, SelectToHandleActionService.contactList)
                }
            }
        }


        vSelectDiscussion.setOnClickListener {

            val discussionSelectControlAction = DiscussionSelectControlAction().apply {
                if (singleMode) {
                    max = 1
                }

                viewTitle = getStrings(R.string.select_discussions)

                needSearchBtn = false

                selectToHandleAction = transferMessageControlAction

                discussionIdListPreSelected = ContactHelper.toIdList(SelectToHandleActionService.contactList)

            }

            val intent = SelectDiscussionListActivity.getIntent(activity, discussionSelectControlAction)
            startActivity(intent)

        }

        vSelectContact.setOnClickListener {

            SelectedContactList.clear()
            val userSelectControlAction = UserSelectControlAction().apply {
                selectMode = UserSelectActivity.SelectMode.SELECT

                if (singleMode) {
                    selectAction = UserSelectActivity.SelectAction.DISCUSSION

                } else {
                    max = TransferMessageActivity.SELECT_MAX
                }

                selectToHandleAction = transferMessageControlAction?.copy(isNeedCreateDiscussion = singleMode)

                setSelectedContacts(SelectToHandleActionService.contactList)

                isNeedSetNotAllowList = false

            }

            val intent = UserSelectActivity.getIntent(context, userSelectControlAction)
            startActivity(intent)
        }

        vSearchHeader.mSearchText.setOnClickListener {
            if (singleMode) {

                val newSearControlAction = NewSearchControlAction().apply {
                    searchAction = SearchAction.SELECT

                    val tempList = ArrayList<SearchContent>()
                    tempList.add(SearchContent.SEARCH_USER)
                    if (DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
                        tempList.add(SearchContent.SEARCH_DEVICE)
                    }

                    if (AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry) {
                        tempList.add(SearchContent.SEARCH_DISCUSSION)
                    }

                    searchContentList = tempList.toArray(emptyArray())

                    selectToHandleAction = transferMessageControlAction

                    filterMe = true
                }

                val intent = NewSearchActivity.getIntent(activity, newSearControlAction)
                startActivity(intent)
            }
        }


        recentContactAdapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->

            val contactItem = sessionContactList[position]

            if (null != transferMessageControlAction) {
                if (!contactItem.isSelect && SelectToHandleActionService.checkIsToThreshold(transferMessageControlAction!!, contactItem)) {
                    return@OnItemClickListener
                }
            }

            handleContactSelect(contactItem)
        }

        rvRecentContacts.setOnTouchListener { v, event ->
            if (hasKeyBoard) {
                AtworkUtil.hideInput(activity, vSearchHeader.mSearchText)
                hasKeyBoard = false
            }
            false
        }


        lvSearchResult.setOnTouchListener { v, event ->
            if (hasKeyBoard) {
                AtworkUtil.hideInput(activity, vSearchHeader.mSearchText)
                hasKeyBoard = false
            }
            false
        }

        lvSearchResult.setOnItemClickListener { parent, view, position, id ->
            if (null != searchResultAdapter) {
                val result = searchResultAdapter!!.getItem(position)

                if (null != transferMessageControlAction) {
                    if (!result.isSelect && SelectToHandleActionService.checkIsToThreshold(transferMessageControlAction!!, result)) {
                        return@setOnItemClickListener
                    }
                }


                result.select(!result.isSelect)

                SelectToHandleActionService.handleSelect(result)

                vSearchHeader.clearInput()
            }
        }


        vFileTransferHeadView.setOnClickListener {

            if (null != transferMessageControlAction) {
                if (!isFileTransferSelect && SelectToHandleActionService.checkIsToThreshold(transferMessageControlAction!!)) {
                    return@setOnClickListener
                }
            }

            FileTransferService.getFileTransfer(object : UserAsyncNetService.OnQueryUserListener {
                override fun onSuccess(user: User) {
                    user.mSelect = isFileTransferSelect

                    handleContactSelect(user)
                }

                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

            })

        }


    }

    private fun refreshFileTransferHeaderViewSelectStatus(select: Boolean) {
        isFileTransferSelect = select

        if (isFileTransferSelect) {
            vFileTransferHeadView.select()
        } else {
            vFileTransferHeadView.unselect()

        }
    }

    private fun handleContactSelect(contactItem: ShowListItem) {
        if (singleMode) {
            SelectToHandleActionService.action(activity, transferMessageControlAction, contactItem)
        } else {
            contactItem.select(!contactItem.isSelect)
            //                action(contactItem)


            SelectToHandleActionService.handleSelect(contactItem)

        }


    }


    private fun clearAllSelectStatus() {
        SelectToHandleActionService.clear()

        refreshUI()
        refreshFileTransferHeaderViewSelectStatus(false)

        vSearchHeader.clear()
        resetRecentContactSelect()
        recentContactAdapter?.singleMode = singleMode
        recentContactAdapter?.notifyDataSetChanged()
    }

    private fun filterHeaderSearch(contactList: MutableList<ShowListItem>) {

        var contactRemove: ShowListItem? = null
        loop@ for (contact in contactList) {
            if (!User.isYou(BaseApplicationLike.baseContext, contact.id)) {
                continue
            }

            when (contact) {
                is User -> {
                    if (!contact.mFileTransfer) {
                        contactRemove = contact
                        break@loop
                    }
                }

                is Employee -> {
                    contactRemove = contact
                    break@loop
                }
            }
        }

        if (null != contactRemove) {
            contactList.remove(contactRemove)
        }
    }

    private fun refreshSelectStatus(contactList: List<ShowListItem>) {
        val selectIdList = ContactHelper.toIdList(SelectToHandleActionService.contactList)
        for (contact in contactList) {
            if (selectIdList.contains(contact.id)) {
                contact.select(true)
            }
        }
    }

    private fun initViews() {
        vSelectDiscussion = CommonItemView(activity)
        vSelectContact = CommonItemView(activity)
        vContactTitleView = ContactTitleView(activity, R.string.recent_contacts)
        vDeviceTitleView = ContactTitleView(activity, R.string.device)
        vFileTransferHeadView = ContactListItemView(activity)

        vFileTransferHeadView.avatarView.setImageResource(R.mipmap.icon_file_transfer)
        vFileTransferHeadView.titleView.setText(R.string.file_transfer)
        vFileTransferHeadView.setLineVisible(false)

        vContactTitleView.llRoot.setPadding(0, DensityUtil.dip2px(10F), 0, 0)
        vDeviceTitleView.llRoot.setPadding(0, DensityUtil.dip2px(10F), 0, 0)

        vSelectDiscussion.setCommonName(getStrings(R.string.select_discussions))

        tvLeftTest.setText(R.string.cancel)

        tvRightest.visibility = View.VISIBLE


        refreshUI()
    }

    private fun refreshUI() {
        if (singleMode) {
            tvTitle.setText(R.string.transfer_msg_select_one_chat)
            vSelectContact.setCommonName(getStrings(R.string.transfer_msg_and_create_discussion))

            if(!AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry) {
                vSelectContact.visibility = View.GONE
            }

            tvRightest.setTextColor(resources.getColor(R.color.common_item_black))
            tvRightest.setText(R.string.multi_select)

            tvLeftTest.visibility = View.GONE
            ivBack.visibility = View.VISIBLE

            vFileTransferHeadView.hideSelect()

            vSelectDiscussion.visibility = View.VISIBLE


        } else {
            tvTitle.setText(R.string.transfer_msg_select_multi_chat)
            vSelectContact.setCommonName(getStrings(R.string.select_contacts))
            vSelectContact.visibility = View.VISIBLE

            updateButtonNum()

            tvLeftTest.visibility = View.VISIBLE
            ivBack.visibility = View.GONE

            vFileTransferHeadView.showSelect()


            if(ContactViewMode.SIMPLE_VERSION == ContactHelper.getContactViewMode(BaseApplicationLike.baseContext)) {
//                ViewUtil.setHeight(vSelectDiscussion, 0)
                vSelectDiscussion.visibility = View.GONE
            }


        }
    }


    private fun updateButtonNum() {
        val size = SelectToHandleActionService.contactList.size

        if (0 >= size) {
            tvRightest.setTextColor(resources.getColor(R.color.title_bar_rightest_text_gray))
            tvRightest.text = resources.getString(R.string.ok)


        } else {
            tvRightest.setTextColor(resources.getColor(R.color.common_item_black))
            tvRightest.text = resources.getString(R.string.ok_with_num, size)

        }
    }

    fun initData() {
        transferMessageControlAction = arguments?.getParcelable(TransferMessageActivity.DATA_TRANSFER_MESSAGE_CONTROL_ACTION)
        if (null != transferMessageControlAction) {
            transferMessageMode = transferMessageControlAction!!.sendMode
            transferMessages = transferMessageControlAction!!.sendMessageList
        }


        recentContactAdapter = RecentContactAdapter(sessionContactList)
        recentContactAdapter!!.apply {
            if (AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry) {
                addHeaderView(vSelectDiscussion)
            }
            addHeaderView(vSelectContact)
            if (DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
                addHeaderView(vDeviceTitleView)
                addHeaderView(vFileTransferHeadView)
            }
            addHeaderView(vContactTitleView)
        }


        rvRecentContacts.adapter = recentContactAdapter


        searchResultAdapter = SearchListAdapter(activity, SearchAction.SELECT)
        searchResultAdapter!!.setNeedSelectStatus(true)
        lvSearchResult.adapter = searchResultAdapter

        initSelectContactHeader()
    }

    private fun initSelectContactHeader() {
        DbThreadPoolExecutor.getInstance().execute {

            val orgCodeList = ArrayList(OrganizationManager.getInstance().queryLoginOrgCodeListTryCacheSync())
            val modeList = ArrayList<String>()
            if (DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
                modeList.add(SelectContactHead.SearchMode.DEVICE)
            }
            modeList.add(SelectContactHead.SearchMode.FRIEND)
            modeList.add(SelectContactHead.SearchMode.EMPLOYEE)
            modeList.add(SelectContactHead.SearchMode.DISCUSSION)

            vSearchHeader.setSearchModeAndOrgCodes(orgCodeList, modeList)
            vSearchHeader.selectUserSearchListener = object : SelectContactHead.SelectContactSearchListener {
                override fun searchStart(key: String?) {
                    hasKeyBoard = true

                    searchResultAdapter?.clearData()
                    searchResultAdapter?.setKey(key)
                }


                override fun searchSuccess(contactList: List<ShowListItem>, userIdNeedCheckOnlineStatusList: List<String>, isAllSearchNoResult: Boolean) {
                    if (!ListUtil.isEmpty(contactList)) {
                        filterHeaderSearch(contactList as MutableList<ShowListItem>)
                        refreshSelectStatus(contactList)


                        if (!ListUtil.isEmpty(contactList)) {
                            val listItem = contactList[0]
                            when (listItem) {
                                is Discussion -> searchResultAdapter?.setDiscussionSearchItem(contactList as ArrayList<Discussion>)
                                is User -> {
                                    if (listItem.mFileTransfer) {
                                        searchResultAdapter?.setDeviceSearchItem(contactList)

                                    } else {
                                        searchResultAdapter?.setUserSearchItem(contactList, SearchAction.SELECT)

                                    }
                                }

                                else -> {
                                    searchResultAdapter?.setUserSearchItem(contactList, SearchAction.SELECT)
                                }

                            }
                        }

                    }


                    handleUiAfterSearch(isAllSearchNoResult)
                }

                override fun searchClear() {
                    emptySearchResult()
                }

                override fun refresh(contact: ShowListItem) {
                    SelectToHandleActionService.handleSelect(contact)

                }

            }


        }
    }


    private fun handleUiAfterSearch(isAllSearchNoResult: Boolean) {
        if (isAllSearchNoResult) {
            rvRecentContacts.visibility = View.GONE
            lvSearchResult.visibility = View.GONE
            llSearchNoResult.visibility = View.VISIBLE
        } else {
            rvRecentContacts.visibility = View.GONE
            lvSearchResult.visibility = View.VISIBLE
            llSearchNoResult.visibility = View.GONE
        }
    }

    private fun emptySearchResult() {
        rvRecentContacts.visibility = View.VISIBLE
        lvSearchResult.visibility = View.GONE
        llSearchNoResult.visibility = View.GONE
    }


    private fun resetRecentContactSelect() {
        for (recentContact in sessionContactList) {
            recentContact.select(false)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun loadSessions() {
        object : AsyncTask<Void, Void, List<Session>>() {
            override fun doInBackground(vararg params: Void): List<Session> {
                var sessions = ArrayList<Session>()
                sessions.addAll(ChatSessionDataWrap.getInstance().sessions)
                if (sessions.size == 0) {
                    sessions = ChatService.queryAllSessionsDb() as ArrayList<Session>
                }
                sessions = getSessionsCanForward(sessions) as ArrayList<Session>
//                sessions.sort()

                ChatSessionDataWrap.getInstance().sortAvoidShaking(sessions)

                return sessions
            }

            override fun onPostExecute(sessions: List<Session>) {
                sessionContactList.clear()
                sessionContactList.addAll(sessions)

                resetRecentContactSelect()

                recentContactAdapter?.notifyDataSetChanged()
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())
    }

    private fun getSessionsCanForward(sessions: List<Session>): List<Session> {
        val forwardSessionList = ArrayList<Session>()
        for (session in sessions) {
            if (Session.WORKPLUS_SUMMARY_HELPER != session.identifier && Session.WORKPLUS_SYSTEM != session.identifier && (Session.EntryType.To_Chat_Detail === session.entryType || null == session.entryType)) {
                forwardSessionList.add(session)
            }
        }
        return forwardSessionList
    }


}
