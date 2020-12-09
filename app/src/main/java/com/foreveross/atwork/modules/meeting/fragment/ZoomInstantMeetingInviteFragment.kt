package com.foreveross.atwork.modules.meeting.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember
import com.foreveross.atwork.infrastructure.utils.ContactHelper
import com.foreveross.atwork.manager.VoipManager
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.meeting.adapter.ZoomInstantMeetingSelectModeAdapter
import com.foreveross.atwork.modules.meeting.model.ZoomInstantMeetingSelectMode
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_select_members_in_zoom_instant_meeting.*

class ZoomInstantMeetingInviteFragment : BackHandledFragment() {

    //创建预约会畅会议 code
    private val INVITE_ZOOM_MEETING_INSTANT_ORG_SELECT = 3
    private val INVITE_ZOOM_MEETING_INSTANT_DISCUSSION_SELECT = 4

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private val modeLabelList = arrayListOf(ZoomInstantMeetingSelectMode.DISCUSSION, ZoomInstantMeetingSelectMode.ORGANIZATION)
    lateinit var adapter: ZoomInstantMeetingSelectModeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_members_in_zoom_instant_meeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ZoomInstantMeetingSelectModeAdapter(modeLabelList)
        rvSelect.adapter = adapter

        tvTitle.setText(R.string.select_contact_title)

        registerListener()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (INVITE_ZOOM_MEETING_INSTANT_ORG_SELECT == requestCode && Activity.RESULT_OK == resultCode) {
            handleInviteZoomMeetingInstant()

        }

        if(INVITE_ZOOM_MEETING_INSTANT_DISCUSSION_SELECT == requestCode && Activity.RESULT_OK == resultCode) {
            handleInviteZoomMeetingInstant()
        }
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }


        adapter.setOnItemClickListener { adapter, view, position ->
            when (modeLabelList[position]) {
                ZoomInstantMeetingSelectMode.DISCUSSION -> {
                    ZoomVoipManager.currentVoipMeeting?.mMeetingInfo?.mId?.let {

                        val discussionMemberSelectControlAction = DiscussionMemberSelectControlAction()
//                        discussionMemberSelectControlAction.selectedContacts = ZoomVoipManager.getMemberList()
                        discussionMemberSelectControlAction.discussionId = it
                        discussionMemberSelectControlAction.selectMode = DiscussionMemberSelectActivity.Mode.VOIP

                        val intent = DiscussionMemberSelectActivity.getIntent(activity, discussionMemberSelectControlAction)
                        startActivityForResult(intent, INVITE_ZOOM_MEETING_INSTANT_DISCUSSION_SELECT)

                    }


                }
                ZoomInstantMeetingSelectMode.ORGANIZATION -> {
                    SelectedContactList.clear()
                    val userSelectControlAction = UserSelectControlAction()
                    userSelectControlAction.selectMode = UserSelectActivity.SelectMode.SELECT
//                    userSelectControlAction.selectedContacts = ZoomVoipManager.getMemberList()

                    val intent = UserSelectActivity.getIntent(activity, userSelectControlAction)

                    startActivityForResult(intent, INVITE_ZOOM_MEETING_INSTANT_ORG_SELECT)
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }




    private fun handleInviteZoomMeetingInstant() {
        val currentVoipMeeting = ZoomVoipManager.currentVoipMeeting ?: return
        val activity = activity?: return


        val contactList = SelectedContactList.getContactList()
        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()


        ZoomVoipManager.inviteMeeting(activity, currentVoipMeeting.mWorkplusVoipMeetingId, currentVoipMeeting.mMeetingInfo, currentVoipMeeting.mVoipType, ContactHelper.transferContactList(contactList), object : VoipManager.OnInviteVoipMeetingListener {
            override fun onSuccess(meetingMemberList: List<VoipMeetingMember>) {
                progressDialogHelper.dismiss()

                AtworkToast.showToast("邀请成功")
                finish()
            }

            override fun networkFail(errorCode: Int, errorMsg: String) {
                progressDialogHelper.dismiss()

                ErrorHandleUtil.handleError(errorCode, errorMsg)
                finish()
            }
        })
    }
}