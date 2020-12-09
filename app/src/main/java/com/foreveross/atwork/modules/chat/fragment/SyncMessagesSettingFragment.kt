package com.foreveross.atwork.modules.chat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment
import com.foreveross.atwork.R
//import com.foreveross.atwork.component.CommonPopSelectData
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.chat.SyncMessageMode
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.support.BackHandledFragment

private val daySelectList = arrayListOf(
        30, 15, 7, 3, 1, 0
)

class SyncMessagesSettingFragment : BackHandledFragment() {

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvReset: TextView
    private lateinit var llFetchAllMsgsSinceLogOut: LinearLayout
    private lateinit var ivFetchAllMsgsSinceLogOut: ImageView
    private lateinit var llOnlyFetchRecentMsgs: LinearLayout
    private lateinit var ivOnlyFetchRecentMsgs: ImageView
    private lateinit var tvOnlyFetchRecentMsgs: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_sync_messages_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        registerListener()
    }

    override fun findViews(view: View) {
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        tvReset = view.findViewById(R.id.title_bar_common_right_text)
        llFetchAllMsgsSinceLogOut = view.findViewById(R.id.ll_fetch_all_msgs_since_log_out)
        ivFetchAllMsgsSinceLogOut = view.findViewById(R.id.iv_fetch_all_msgs_since_log_out)
        llOnlyFetchRecentMsgs = view.findViewById(R.id.ll_only_fetch_recent_msgs)
        ivOnlyFetchRecentMsgs = view.findViewById(R.id.iv_only_fetch_recent_msgs)
        tvOnlyFetchRecentMsgs = view.findViewById(R.id.tv_only_fetch_recent_msgs)
    }

    private fun initUI() {
        tvTitle.setText(R.string.msg_sync_setting)
        tvReset.setText(R.string.reset)

        tvReset.visibility = View.VISIBLE

        refreshUI()
    }

    private fun refreshUI() {
        tvOnlyFetchRecentMsgs.text = getStrings(R.string.only_fetch_recent_msgs, CommonShareInfo.getSelectFetchMessagesDays(BaseApplicationLike.baseContext))

        when (CommonShareInfo.getSelectFetchMessageMode(BaseApplicationLike.baseContext)) {
            SyncMessageMode.MODE_FETCH_ALL_MSGS_SINCE_LOGOUT -> {
                ivFetchAllMsgsSinceLogOut.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
                ivOnlyFetchRecentMsgs.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
            }


            SyncMessageMode.MODE_ONLY_FETCH_RECENT_MSGS -> {
                ivFetchAllMsgsSinceLogOut.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
                ivOnlyFetchRecentMsgs.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
            }

        }
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        tvReset.setOnClickListener {
            CommonShareInfo.setSelectFetchMessageMode(BaseApplicationLike.baseContext, AtworkConfig.CHAT_CONFIG.defaultFetchMessagesMode)
            CommonShareInfo.setSelectFetchMessagesDays(BaseApplicationLike.baseContext, AtworkConfig.CHAT_CONFIG.defaultOnlyFetchRecentMessagesDays)

            refreshUI()

            toastOver(R.string.reset_successfully)
        }


        llFetchAllMsgsSinceLogOut.setOnClickListener {
            CommonShareInfo.setSelectFetchMessageMode(context, SyncMessageMode.MODE_FETCH_ALL_MSGS_SINCE_LOGOUT)
            refreshUI()

        }

        ivOnlyFetchRecentMsgs.setOnClickListener{
            if(SyncMessageMode.MODE_ONLY_FETCH_RECENT_MSGS == CommonShareInfo.getSelectFetchMessageMode(BaseApplicationLike.baseContext)) {
                return@setOnClickListener
            }

            AtworkAlertDialog(activity, AtworkAlertDialog.Type.SIMPLE)
                    .setContent(R.string.set_only_fetch_recent_msgs_alert)
                    .setClickBrightColorListener {
                        CommonShareInfo.setSelectFetchMessageMode(context, SyncMessageMode.MODE_ONLY_FETCH_RECENT_MSGS)
                        refreshUI()

                    }
                    .show()
        }

        llOnlyFetchRecentMsgs.setOnClickListener {
            val itemList = daySelectList.map { getStrings(R.string.day_label, it)}
            val itemOriginalSelected = getStrings(R.string.day_label, CommonShareInfo.getSelectFetchMessagesDays(BaseApplicationLike.baseContext))
            val w6sSelectDialogFragment = W6sSelectDialogFragment()
            w6sSelectDialogFragment.setData(CommonPopSelectData(itemList, itemOriginalSelected))
            fragmentManager?.let { it1 ->
                w6sSelectDialogFragment.setOnClickItemListener(object : W6sSelectDialogFragment.OnClickItemListener {
                    override fun onClick(position: Int, value: String) {
                        val itemClick = daySelectList[position]
                        val context = BaseApplicationLike.baseContext

                        if(SyncMessageMode.MODE_ONLY_FETCH_RECENT_MSGS == CommonShareInfo.getSelectFetchMessageMode(BaseApplicationLike.baseContext)
                                && value == itemOriginalSelected) {
                            return
                        }
                        AtworkAlertDialog(activity, AtworkAlertDialog.Type.SIMPLE)
                                .setContent(R.string.set_only_fetch_recent_msgs_alert)
                                .setClickBrightColorListener {
                                    CommonShareInfo.setSelectFetchMessagesDays(context, itemClick)
                                    CommonShareInfo.setSelectFetchMessageMode(context, SyncMessageMode.MODE_ONLY_FETCH_RECENT_MSGS)

                                    refreshUI()

                                }
                                .show()
                    }
                })
                        .show(it1, "w6sSelectDialogFragment")
            }
        }

    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }



}

