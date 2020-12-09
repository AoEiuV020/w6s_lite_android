package com.foreveross.atwork.modules.meeting.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.component.CommonPopSelectData
import com.foreveross.atwork.component.CommonPopSelectListDialogFragment
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo
import com.foreveross.atwork.infrastructure.model.zoom.ZoomJoinMeetingHistoryDataItem
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_zoom_join_voip_meeting.*

class ZoomJoinVoipMeetingFragment: BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_zoom_join_voip_meeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        registerListener()
    }

    override fun onStart() {
        super.onStart()

        refreshIvArrowUI()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
    }

    override fun onDestroy() {
        super.onDestroy()

        ZoomVoipManager.zoomJoinMeetingHistoryDataItemTempSaved = null
    }

    private fun initViews() {
        tvTitle.text = "加入会议"


        
//        ZoomManager.getLastJoinData(AtworkApplicationLike.baseContext)?.let {
//            etMeetingNumber.etInput.setText(it.meetingId)
//            etDisplayName.etInput.setText(it.name)
//        }

        if(StringUtils.isEmpty(etDisplayName.etInput.text.toString())) {
            etDisplayName.etInput.setText(getInitDisplayName())
        }

        refreshIvArrowUI()
        
        onChangeLoginBtnStatus()

        etMeetingNumber.setInputType(InputType.TYPE_CLASS_NUMBER)


    }

    private fun refreshIvArrowUI() {
        if(!ListUtil.isEmpty(ZoomManager.getJoinDataList(AtworkApplicationLike.baseContext))) {
            ivArrow.isVisible = true

        } else {
            ivArrow.isInvisible = true

        }
    }

    private fun getInitDisplayName(): String {
        val loginUserName = LoginUserInfo.getInstance().getLoginUserName(AtworkApplicationLike.baseContext)
        if(!StringUtils.isEmpty(loginUserName)) {
            return loginUserName
        }

        return "游客"
    }


    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        etMeetingNumber.setInputTextWatcher(object : TextWatcherAdapter() {
            override fun afterTextChanged(text: Editable?) {
                onChangeLoginBtnStatus()
            }
        })

        etDisplayName.setInputTextWatcher(object : TextWatcherAdapter() {
            override fun afterTextChanged(text: Editable?) {
                onChangeLoginBtnStatus()
            }
        })

        swAutoConnectAudio.setOnClickNotPerformToggle {
            swAutoConnectAudio.toggle()
        }


        swOpenCamera.setOnClickNotPerformToggle {
            swOpenCamera.toggle()
        }

        btJoinMeeting.setOnClickListener {
            doJoinMeeting()

//            val progressDialogHelper = ProgressDialogHelper(activity)
//            progressDialogHelper.show()
//
//            ZoomVoipManager.queryZoomTypeMeetingStatus(meetingId = etMeetingNumber.text, listener = object : BaseNetWorkListener<Boolean> {
//
//                override fun onSuccess(result: Boolean) {
//                    progressDialogHelper.dismiss()
//                    if(result) {
//                    } else {
//                        toastOver("会议不存在或已过期")
//                    }
//
//                }
//
//                override fun networkFail(errorCode: Int, errorMsg: String?) {
//                    progressDialogHelper.dismiss()
//                    ErrorHandleUtil.handleError(errorCode, errorMsg)
//                }
//            })

        }

        ivArrow.setOnClickListener {
            popSelectListDialogFragment()

        }
    }

    private fun doJoinMeeting() {
        ZoomManager.setMuteMyMicrophoneWhenJoinMeeting(!swAutoConnectAudio.isChecked)
        ZoomManager.setTurnOffMyVideoWhenJoinMeeting(!swOpenCamera.isChecked)
        ZoomManager.joinMeeting(AtworkApplicationLike.baseContext, HandleMeetingInfo(displayName = etDisplayName.text, meetingId = etMeetingNumber.text))

        ZoomVoipManager.zoomJoinMeetingHistoryDataItemTempSaved = ZoomJoinMeetingHistoryDataItem(
                name = etDisplayName.text,
                meetingId = etMeetingNumber.text,
                joinTime = TimeUtil.getCurrentTimeInMillis()
        )
    }

    private fun popSelectListDialogFragment() {
        ZoomManager.getJoinDataList(AtworkApplicationLike.baseContext)?.let { dataList ->

            val commonPopSelectListDialogFragment = CommonPopSelectListDialogFragment()
            val nameList: List<String> = dataList.map { "${it.name}    ${it.meetingId}" }

            val nameSelected = "${etDisplayName.text}    ${etMeetingNumber.text}"

            commonPopSelectListDialogFragment.setData(CommonPopSelectData(nameList, nameSelected))
            commonPopSelectListDialogFragment.onClickItemListener = object : CommonPopSelectListDialogFragment.OnClickItemListener {
                override fun onClick(position: Int, value: String) {
                    etDisplayName.etInput.setText(dataList[position].name)
                    etMeetingNumber.etInput.setText(dataList[position].meetingId)

                }

            }
            commonPopSelectListDialogFragment.show(fragmentManager!!, "commonPopSelectListDialog")

        }
    }

    private fun onChangeLoginBtnStatus() {

        if (!inputInfoLegal()) {
            btJoinMeeting.setBackgroundResource(R.drawable.shape_login_rect_input_nothing)
            btJoinMeeting.isEnabled = false
            return
        }



        btJoinMeeting.setBackgroundResource(R.drawable.shape_login_rect_input_something)
        btJoinMeeting.isEnabled = true
    }

    private fun inputInfoLegal(): Boolean {
        if(StringUtils.isEmpty(etMeetingNumber.text)) {
            return false
        }

        if(10 > etMeetingNumber.text.length) {
            return false
        }

        if(StringUtils.isEmpty(etDisplayName.text)) {
            return false
        }

        return true
    }




    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

}