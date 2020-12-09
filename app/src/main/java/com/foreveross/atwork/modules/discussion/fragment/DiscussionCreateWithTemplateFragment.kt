package com.foreveross.atwork.modules.discussion.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.foreveross.atwork.R
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionTemplate
import com.foreveross.atwork.infrastructure.model.orgization.Organization
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.infrastructure.utils.extension.ctxApp
import com.foreveross.atwork.infrastructure.utils.extension.getColorCompat
import com.foreveross.atwork.infrastructure.utils.extension.getParcelableDirectly
import com.foreveross.atwork.manager.OrganizationManager
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.discussion.adapter.DiscussionMembersInCreatingAdapter
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.modules.discussion.manager.extension.createDiscussion
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberContactItemInfo
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberItemInfoType
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.organization.helper.popOrgSelect
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_discussion_create_with_template_v2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class DiscussionCreateWithTemplateFragment : BackHandledFragment() {

    private val REQUEST_ADD_MEMBERS = 0X006
    private val GRID_SPAN_COUNT = 4

    private lateinit var memberSelectedAdapter: DiscussionMembersInCreatingAdapter
    private lateinit var template: DiscussionTemplate
    private val discussionMemberItemInfoList = ArrayList<DiscussionMemberContactItemInfo>()
    private val discussionMemberItemOriginalList = ArrayList<ShowListItem>()

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView

    private var selectOrg: Organization? = null

    private var modeExpandMembers = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discussion_create_with_template_v2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)
        initData()
        initViews()
        registerListener()

        refreshDiscussionMemberItemInfoList()
    }

    private fun initData() {
        arguments?.getParcelableDirectly<DiscussionTemplate>()?.let { template = it }

    }


    override fun findViews(view: View) {
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvTitle = view.findViewById(R.id.title_bar_common_title)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ADD_MEMBERS -> {
                if (Activity.RESULT_OK == resultCode) {
                    val contactList = SelectedContactList.getContactList()
                    refreshDiscussionMemberItemInfoList(contactList)
                }
            }
        }
    }

    private fun initViews() {

        tvTitle.setText(R.string.create_discussion)

        vDiscussionTemplate.refreshItem(template)
        etDiscussionName.setHint(getString(R.string.please_input_template_discussion_name, template.getNameI18n(ctxApp)))

        memberSelectedAdapter = DiscussionMembersInCreatingAdapter(discussionMemberItemInfoList)
        rvMemberList.layoutManager = GridLayoutManager(activity, GRID_SPAN_COUNT)
        rvMemberList.adapter = memberSelectedAdapter

        refreshUI()

        if (template.scopePinned) {
            OrganizationManager.getInstance().getLocalCurrentOrg(ctxApp) {
                if(null == selectOrg) {
                    selectOrg = it
                    refreshTvMappingOrganizationTipUI()
                }
            }
        }
    }


    private fun registerListener() {

        ivBack.setOnClickListener {
            onBackPressed()

        }

        memberSelectedAdapter.setOnItemClickListener { adapter, view, position ->
            when (discussionMemberItemInfoList[position].type) {
                DiscussionMemberItemInfoType.ADD -> routeAdd()
                DiscussionMemberItemInfoType.REMOVE -> switchRemoveMode()

            }
        }


        memberSelectedAdapter.onItemRemoveBtnClick = { pos, contact ->
            discussionMemberItemInfoList.removeAt(pos)
            discussionMemberItemOriginalList.remove(contact)

            refreshDiscussionMemberItemInfoList()
        }

        clRoot.setOnClickListener {
            if (memberSelectedAdapter.removeMode) {
                memberSelectedAdapter.removeMode = false
                refreshUI()
            }
        }


        tvCreateDiscussion.setOnClickListener {
            val progressHelper = ProgressDialogHelper(activity)

            var avatar: String? = template.avatar
            if("" == avatar) {
                avatar = null
            }
            var name: String? = etDiscussionName.text.toString()
            if("" == name) {
                name = null
            }

            DiscussionManager.getInstance()
                    .createDiscussion(ctxApp, discussionMemberItemOriginalList, name, avatar, selectOrg?.mOrgCode, template.getRequestId())
                    .flowOn(Dispatchers.IO)
                    .onStart { progressHelper.show() }
                    .onEach { discussion ->
                        val intent = ChatDetailActivity.getIntent(activity, discussion.mDiscussionId)
                        startActivity(intent)

                    }
                    .onCompletion { progressHelper.dismiss() }
                    .catch { ErrorHandleUtil.handleError(it) }
                    .launchIn(lifecycleScope)


        }

        tvMappingOrganizationTip.setOnClickListener {
            activity
                    ?.asType<FragmentActivity>()
                    ?.let {
                        popOrgSelect(it) { org ->

                            //clear data
                            if(org != selectOrg) {
                                discussionMemberItemOriginalList.clear()
                                refreshDiscussionMemberItemInfoList()
                            }

                            selectOrg = org
                            refreshUI()
                        }
            }

        }

        tvMemberMore.setOnClickListener {
            modeExpandMembers = !modeExpandMembers
            refreshDiscussionMemberItemInfoList()
        }


    }

    private fun switchRemoveMode() {
        memberSelectedAdapter.removeMode = memberSelectedAdapter.removeMode.not()
        refreshUI()

    }

    private fun routeAdd() {

        if(template.scopePinned && null == selectOrg) {
            toast("请先选择组织")
            return
        }

        SelectedContactList.clear()

        val userSelectControlAction = UserSelectControlAction().apply {
            selectMode = UserSelectActivity.SelectMode.SELECT
            setSelectedContacts(discussionMemberItemOriginalList)
            isNeedSetNotAllowList = false
            isSuggestiveHideMe = true
            directOrgCode = selectOrg?.mOrgCode
            directOrgShow = null != selectOrg
            selectAction = UserSelectActivity.SelectAction.DISCUSSION
        }

        val intent = UserSelectActivity.getIntent(activity, userSelectControlAction)
        startActivityForResult(intent, REQUEST_ADD_MEMBERS)

//        UserManager.getInstance().getLoginUser()
//                .flowOn(Dispatchers.IO)
//                .onStart { UserSelectActivity.SelectedContactList.clear() }
//                .onEach { loginUser ->
//
//                }
//                .catch { ErrorHandleUtil.handleError(it) }
//                .launchIn(lifecycleScope)


    }

    private fun refreshDiscussionMemberItemInfoList(contactList: List<ShowListItem>? = null) {
        if (null != contactList) {
            discussionMemberItemOriginalList.clear()
            discussionMemberItemOriginalList.addAll(contactList)
        }
        discussionMemberItemInfoList.clear()


        discussionMemberItemInfoList.add(DiscussionMemberContactItemInfo(type = DiscussionMemberItemInfoType.ADD))
        if (discussionMemberItemOriginalList.isNotEmpty()) {
            discussionMemberItemInfoList.add(DiscussionMemberContactItemInfo(type = DiscussionMemberItemInfoType.REMOVE))
        }

        val displayMemberSize = if (modeExpandMembers) {
            discussionMemberItemOriginalList.size
        } else {
            GRID_SPAN_COUNT * 2 - discussionMemberItemInfoList.size
        }

        discussionMemberItemOriginalList.map { DiscussionMemberContactItemInfo(type = DiscussionMemberItemInfoType.CONTACT, contact = it) }
                .take(displayMemberSize)
                .let { discussionMemberItemInfoList.addAll(it) }

        refreshUI()
    }

    private fun refreshUI() {
        val memberCount = discussionMemberItemOriginalList.size

        if (0 >= memberCount) {
            discussionMemberItemInfoList.removeAll { DiscussionMemberItemInfoType.REMOVE == it.type }
        }



        memberSelectedAdapter.notifyDataSetChanged()

        tvDiscussionMemberCount.text = getStrings(R.string.common_sum_people, memberCount)

        groupMappingOrganization.isVisible = template.scopePinned

        if (0 >= memberCount) {
            tvCreateDiscussion.background.asType<GradientDrawable>()?.run {
                mutate()
                setColor(ContextCompat.getColor(ctxApp, R.color.login_button_input_nothing))
            }
            tvCreateDiscussion.isEnabled = false

        } else {

            tvCreateDiscussion.background.asType<GradientDrawable>()?.run {
                mutate()
                setColor(ContextCompat.getColor(ctxApp, R.color.common_blue_bg))
            }

            tvCreateDiscussion.isEnabled = true

        }

        groupMemberMore.isVisible = shouldVisibleGroupMemberMore()


        refreshTvMappingOrganizationTipUI()

    }

    private fun refreshTvMappingOrganizationTipUI() {
        if (null != selectOrg) {
            tvMappingOrganizationTip.text = selectOrg!!.getNameI18n(ctxApp)
            tvMappingOrganizationTip.setTextColor(ctxApp.getColorCompat(R.color.common_text_gray_color))

        } else {
            tvMappingOrganizationTip.text = getString(R.string.please_select_mapping_organization)
            tvMappingOrganizationTip.setTextColor(ctxApp.getColorCompat(R.color.common_text_hint_gray))
        }
    }

    private fun shouldVisibleGroupMemberMore() =
            discussionMemberItemInfoList
                    .count { itemInfo -> null != itemInfo.contact }
                    .let { count -> count != discussionMemberItemOriginalList.size }


    override fun onBackPressed(): Boolean {
        if (memberSelectedAdapter.removeMode) {
            memberSelectedAdapter.removeMode = false
            refreshUI()
            return true
        }


        if(modeExpandMembers) {
            modeExpandMembers = !modeExpandMembers
            refreshDiscussionMemberItemInfoList()
            return true
        }


        finish()
        return false
    }
}