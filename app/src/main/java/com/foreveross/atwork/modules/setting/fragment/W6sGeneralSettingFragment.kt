package com.foreveross.atwork.modules.setting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.cache.MessageCache
import com.foreverht.db.service.repository.MessageRepository
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.settingPage.W6sGeneralSetting
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.CustomerHelper
import com.foreveross.atwork.modules.setting.activity.*
import com.foreveross.atwork.modules.setting.adapter.W6sGeneralSettingAdapter
import com.foreveross.atwork.modules.setting.component.W6sSettingItemDecoration
import com.foreveross.atwork.support.BackHandledFragment
import kotlinx.android.synthetic.main.fragment_setting_new.*

class W6sGeneralSettingFragment : BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    private lateinit var w6sGeneralSettingAdapter: W6sGeneralSettingAdapter

    private val generalSettingList = arrayListOf(

            W6sGeneralSetting.LANGUAGE,

            W6sGeneralSetting.FONT_SIZE,

            W6sGeneralSetting.DISCUSSION_HELPER,

            W6sGeneralSetting.WEBVIEW_FLOAT_ACTION_HELPER,

            W6sGeneralSetting.STORAGE_SPACE
    )

    private val generalSettingDistributeList = arrayListOf(

            arrayListOf(W6sGeneralSetting.LANGUAGE),

            arrayListOf(W6sGeneralSetting.FONT_SIZE),

            arrayListOf(W6sGeneralSetting.DISCUSSION_HELPER, W6sGeneralSetting.WEBVIEW_FLOAT_ACTION_HELPER),

//            arrayListOf(W6sGeneralSetting.CLEAR_MESSAGE_HISTORY)

//            arrayListOf(W6sGeneralSetting.EMP_INCOMING_ASSISTANT)

                    arrayListOf(W6sGeneralSetting.STORAGE_SPACE)
    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(CustomerHelper.isKwg(AtworkApplicationLike.baseContext)) {
//            generalSettingList.remove(W6sGeneralSetting.CLEAR_MESSAGE_HISTORY)
//            generalSettingDistributeList.forEach {
//                it.remove(W6sGeneralSetting.CLEAR_MESSAGE_HISTORY)
            generalSettingList.remove(W6sGeneralSetting.STORAGE_SPACE)
            generalSettingDistributeList.forEach {
                it.remove(W6sGeneralSetting.STORAGE_SPACE)
            }
        }

        if(AtworkConfig.SETTING_PAGE_CONFIG.isInvisible(W6sGeneralSetting.EMP_INCOMING_ASSISTANT)) {
            generalSettingList.remove(W6sGeneralSetting.EMP_INCOMING_ASSISTANT)
            generalSettingDistributeList.forEach {
                it.remove(W6sGeneralSetting.EMP_INCOMING_ASSISTANT)
            }
        }

        initUI()
        initRecyclerView()

        registerListener()

    }

    override fun onStart() {
        super.onStart()

        w6sGeneralSettingAdapter.notifyDataSetChanged()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)


    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

    private fun initUI() {
        tvTitle.text = getStrings(R.string.common_general)
    }

    private fun initRecyclerView() {
        w6sGeneralSettingAdapter = W6sGeneralSettingAdapter(generalSettingList, generalSettingDistributeList)

        rvSettings.adapter = w6sGeneralSettingAdapter
        rvSettings.addItemDecoration(W6sSettingItemDecoration(generalSettingList, generalSettingDistributeList))
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }


        w6sGeneralSettingAdapter.setOnItemClickListener { adapter, view, position ->
            when (generalSettingList[position]) {

                W6sGeneralSetting.LANGUAGE -> {
                    val intent = LanguageSettingActivity.getIntent(mActivity)
                    startActivity(intent)
                }
                W6sGeneralSetting.FONT_SIZE -> {
                    val intent = TextSizeSettingActivity.getIntent(mActivity)
                    startActivity(intent)
                }
                W6sGeneralSetting.DISCUSSION_HELPER -> {
                    val intent = DiscussionShieldHelperSettingActivity.getIntent(mActivity)
                    startActivity(intent)
                }
                W6sGeneralSetting.WEBVIEW_FLOAT_ACTION_HELPER -> {
                    val intent = WebviewFloatActionSettingActivity.getIntent(mActivity)
                    startActivity(intent)
                }
                W6sGeneralSetting.STORAGE_SPACE -> {
                    val intent = StorageSpaceSettingActivity.getIntent(mActivity)
                    startActivity(intent)
                }

                W6sGeneralSetting.EMP_INCOMING_ASSISTANT -> {
                    EmpIncomingCallSettingActivity.startActivity(mActivity)
                }
               // W6sGeneralSetting.CLEAR_MESSAGE_HISTORY -> cleanMessage2Threshold()
            }
        }



    }

    private fun cleanMessage2Threshold() {
        val atworkAlertDialog = AtworkAlertDialog(activity, AtworkAlertDialog.Type.SIMPLE)
                .setContent(getStrings(R.string.clean_messages_data_tip, AtworkConfig.CHAT_CONFIG.cleanMessagesThreshold))
                .setClickBrightColorListener { dialog ->


                    doCleanMessage2Threshold()
                }

        atworkAlertDialog.show()
    }

    private fun doCleanMessage2Threshold() {
        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show(false, 30000)

        DbThreadPoolExecutor.getInstance().execute {
            val result = MessageRepository.getInstance().cleanMessages2Threshold()

            if (result) {
                MessageCache.getInstance().clear()
            }

            AtworkApplicationLike.runOnMainThread {
                progressDialogHelper.dismiss()

                if (result) {
                    toastOver(R.string.clean_messages_data_successfully)
                } else {
                    toastOver(R.string.clean_messages_data_unsuccessfully)

                }
            }
        }
    }



}