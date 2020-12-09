package com.foreveross.atwork.modules.discussion.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.discussion.queryReadUnread
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryReadOrUnreadResponse.QueryReadUnreadResult
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService.OnQueryUserListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.utils.ContactHelper
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.OnlineManager
import com.foreveross.atwork.manager.UserManager
import com.foreveross.atwork.modules.discussion.manager.extension.queryDiscussion
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity
import com.foreveross.atwork.modules.contact.adapter.ContactListArrayListAdapter
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.modules.discussion.activity.DiscussionReadUnreadActivity
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ContactQueryHelper
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.Serializable

class DiscussionReadUnreadFragment: BackHandledFragment() {

    private lateinit var tvTitle: TextView

    private lateinit var tvCheck: TextView

    private lateinit var ivBack: View

    private lateinit var lwContact: ListView

    private lateinit var contactListArrayListAdapter: ContactListArrayListAdapter

    private var mode: ReadOrUnread? = null

    private lateinit var messageId: String

    private lateinit var discussionId: String

    private var orgCode: String? = null

    private lateinit var progressHelper: ProgressDialogHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read_unread, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
        registerListener()
    }

    override fun findViews(view: View) {
        tvCheck = view.findViewById(R.id.title_bar_common_right_text)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        lwContact = view.findViewById(R.id.group_read_un_read_list_view)
        ivBack = view.findViewById(R.id.title_bar_common_back)

        tvCheck.visibility = View.VISIBLE

        tvTitle.text = resources.getString(R.string.group_un_read_title, 0)
        tvCheck.setText(R.string.check_read)

        progressHelper = ProgressDialogHelper(mActivity)
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }


    private fun initData() {
        contactListArrayListAdapter = ContactListArrayListAdapter(activity, false)
        lwContact.adapter = contactListArrayListAdapter
        val bundle = arguments
        if (null != bundle) {
            mode = bundle.getSerializable(DiscussionReadUnreadActivity.READ_OR_UNREAD) as ReadOrUnread?
            discussionId = bundle.getString(DiscussionReadUnreadActivity.DISCUSSION_ID) ?: StringUtils.EMPTY
            messageId = bundle.getString(DiscussionReadUnreadActivity.MESSAGE_ID)?: StringUtils.EMPTY
            orgCode = bundle.getString(DiscussionReadUnreadActivity.DISCUSSION_ORG_CODE)
            loadFromRemote()
        }
    }

    private fun registerListener() {
        //返回
        ivBack.setOnClickListener { onBackPressed() }

        lwContact.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
            val contact = parent.getItemAtPosition(position) as ShowListItem
            UserManager.getInstance().queryUserByUserId(mActivity, contact.id, contact.domainId, object : OnQueryUserListener {
                override fun onSuccess(user: User) {
                    if (null != activity) {
                        startActivity(PersonalInfoActivity.getIntent(activity, user))
                    }
                }

                override fun networkFail(errorCode: Int, errorMsg: String) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }
            })
        }

        tvCheck.setOnClickListener { v: View? ->
            if (null == mode || ReadOrUnread.Read == mode) {
                mode = ReadOrUnread.Unread
            } else if (ReadOrUnread.Unread == mode) {
                mode = ReadOrUnread.Read
            }

            loadFromRemote()
        }
    }

    private fun loadFromRemote() {
        if (null == mode) {
            mode = ReadOrUnread.Unread
        }

        queryReadUnread(AtworkApplicationLike.baseContext, messageId, mode.toString())
                .zip(queryDiscussionMemberIdList())
                { readUnreadResult, memberIds -> readUnreadResult.filter { memberIds.contains(it.to) && !User.isYou(AtworkApplicationLike.baseContext, it.to) } }
                .map { readUnreadResult -> getShowContactInfoList(readUnreadResult) }
                .flowOn(Dispatchers.IO)
                .onStart { progressHelper.show() }
                .onEach { contactList ->
                    changeUi(contactList)
                    checkOnline(contactList.map { it.id })
                }
                .catch { ErrorHandleUtil.handleError(it) }
                .onCompletion { progressHelper.dismiss() }
                .launchIn(lifecycleScope)


    }

    private fun queryDiscussionMemberIdList() =
            DiscussionManager.getInstance().queryDiscussion(AtworkApplicationLike.baseContext, discussionId).map { it.toMemberIdList() }

    private fun getShowContactInfoList(readUnreadResult: List<QueryReadUnreadResult>): List<ShowListItem> {

        return ContactQueryHelper.queryContactsWithParticipantBySync(AtworkApplicationLike.baseContext, readUnreadResult.map { it.to }, orgCode)
                .toList()
                .map { contact -> contactWithReadTime(readUnreadResult, contact) }
                .also { ContactHelper.sortContactWithReadTime(it) }
    }

    private fun contactWithReadTime(readUnreadResult: List<QueryReadUnreadResult>, contact: ShowListItem): ShowListItem {
        readUnreadResult
                .find { it.to == contact.id }
                ?.receiptTime
                ?.let {
                    if (contact is User) {
                        contact.readTime = it
                    } else if (contact is Employee) {
                        contact.mReadTime = it

                    }
                }

        return contact
    }

    private fun changeUi(contacts: List<ShowListItem?>) {
        if (isAdded) {
            if (ReadOrUnread.Unread == mode) {
                tvCheck.setText(R.string.check_read)
                tvTitle.text = resources.getString(R.string.group_un_read_title, contacts.size.toString())
                refreshAdapter(contacts, false)
            } else if (ReadOrUnread.Read == mode) {
                tvCheck.setText(R.string.check_unread)
                tvTitle.text = resources.getString(R.string.group_read_title, contacts.size.toString())
                refreshAdapter(contacts, true)
            }
        }
    }




    private fun refreshAdapter(contacts: List<ShowListItem?>, isReadTimeMode: Boolean) {
        contactListArrayListAdapter.clear()
        contactListArrayListAdapter.readTimeMode(isReadTimeMode)
        contactListArrayListAdapter.addAll(contacts)
    }


    private fun checkOnline(checkOnlineList: List<String>) {
        OnlineManager.getInstance().checkOnlineList(mActivity, checkOnlineList) { contactListArrayListAdapter.notifyDataSetChanged() }
    }



    enum class ReadOrUnread : Serializable {
        Read, Unread
    }

}