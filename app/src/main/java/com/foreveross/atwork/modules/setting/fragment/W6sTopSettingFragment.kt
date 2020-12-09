package com.foreveross.atwork.modules.setting.fragment

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.settingPage.W6sTopSetting
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.VoipManager
import com.foreveross.atwork.modules.aboutatwork.activity.AboutAtWorkActivity
import com.foreveross.atwork.modules.aboutatwork.activity.FeedbackActivity
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity
import com.foreveross.atwork.modules.setting.activity.MessagePushSettingActivity
import com.foreveross.atwork.modules.setting.activity.W6sAccountAndSecureSettingActivity
import com.foreveross.atwork.modules.setting.activity.W6sGeneralSettingActivity
import com.foreveross.atwork.modules.setting.adapter.W6sTopSettingAdapter
import com.foreveross.atwork.modules.setting.component.W6sSettingItemDecoration
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.services.ImSocketService
import com.foreveross.atwork.support.BackHandledFragment
import kotlinx.android.synthetic.main.fragment_setting_new.*

class W6sTopSettingFragment : BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var vFooterLogoutView: View

    private lateinit var w6sTopSettingAdapter: W6sTopSettingAdapter

    private val topSettingList = arrayListOf(

            W6sTopSetting.ACCOUNT_AND_SECURE,

            W6sTopSetting.MESSAGE_PUSH,

            W6sTopSetting.GENERAL,

            W6sTopSetting.FEEDBACK,

            W6sTopSetting.INFORM,

            W6sTopSetting.ABOUT


    )

    private val topSettingDistributeList = arrayListOf(

            arrayListOf(W6sTopSetting.ACCOUNT_AND_SECURE),

            arrayListOf(W6sTopSetting.MESSAGE_PUSH, W6sTopSetting.GENERAL),

            arrayListOf(W6sTopSetting.FEEDBACK, W6sTopSetting.INFORM, W6sTopSetting.ABOUT)

    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkSettingConfigs()

        initUI()
        initRecyclerView()

        registerListener()

    }

    private fun checkSettingConfigs() {
        checkSettingConfig(W6sTopSetting.ABOUT)
        checkSettingConfig(W6sTopSetting.FEEDBACK)
        checkSettingConfig(W6sTopSetting.INFORM)

        if(StringUtils.isEmpty(DomainSettingsManager.getInstance().informUrl)) {
            topSettingList.remove(W6sTopSetting.INFORM)
            topSettingDistributeList.forEach { it.remove(W6sTopSetting.INFORM) }
        }
    }

    private fun checkSettingConfig(topSetting : W6sTopSetting) {
        if (AtworkConfig.SETTING_PAGE_CONFIG.isInvisible(topSetting)) {
            topSettingList.remove(topSetting)
            topSettingDistributeList.forEach { it.remove(topSetting) }
        }
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)

        vFooterLogoutView = LayoutInflater.from(activity).inflate(R.layout.component_logout, null)

    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

    private fun initUI() {
        tvTitle.text = getStrings(R.string.setting)
    }

    private fun initRecyclerView() {
        w6sTopSettingAdapter = W6sTopSettingAdapter(topSettingList, topSettingDistributeList)
        w6sTopSettingAdapter.addFooterView(vFooterLogoutView)

        rvSettings.adapter = w6sTopSettingAdapter
        rvSettings.addItemDecoration(W6sSettingItemDecoration(topSettingList, topSettingDistributeList))
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }


        w6sTopSettingAdapter.setOnItemClickListener { adapter, view, position ->
            when (topSettingList[position]) {
                W6sTopSetting.ACCOUNT_AND_SECURE -> {
                    val intent = W6sAccountAndSecureSettingActivity.getIntent(mActivity)
                    startActivity(intent)
                }
                W6sTopSetting.MESSAGE_PUSH -> {
                    val intent = MessagePushSettingActivity.getIntent(mActivity)
                    startActivity(intent)
                }
                W6sTopSetting.GENERAL -> {
                    val generalSettingIntent = W6sGeneralSettingActivity.getIntent(mActivity)
                    startActivity(generalSettingIntent)

                }
                W6sTopSetting.FEEDBACK -> {
                    val feedbackIntent = FeedbackActivity.getIntent(mActivity)
                    startActivity(feedbackIntent)
                }


                W6sTopSetting.INFORM -> {
                    val webviewAction = WebViewControlAction
                            .newAction()
                            .setUrl(DomainSettingsManager.getInstance().informUrl)
                            .setNeedShare(false)

                    startActivity(WebViewActivity.getIntent(mActivity, webviewAction))
                }

                W6sTopSetting.ABOUT -> {
                    val intent = AboutAtWorkActivity.getIntent(mActivity)
                    intent.putExtra("aboutName", AtworkApplicationLike.getResourceString(R.string.about))
                    startActivity(intent)
                }
            }
        }


        vFooterLogoutView.setOnClickListener {
            handleVoipLeave()
            doLogout()
        }

    }


    private fun doLogout() {
        //延迟100毫秒, 使登出前的网络请求丢出去
        Handler().postDelayed({
            AtworkApplicationLike.clearData()
            val beeWorks = BeeWorks.getInstance()
            if (beeWorks.isBeeWorksFaceBioSettingEnable && beeWorks.config.beeWorksSetting.faceBioSetting.faceBioAuth) {
                startActivity(AccountLoginActivity.getLoginIntent(mActivity))
            } else {
                startActivity(LoginWithAccountActivity.getClearTaskIntent(mActivity))
            }

            mActivity.setResult(Activity.RESULT_OK)
            finish()

            ImSocketService.closeConnection()
        }, 100)
    }

    /**
     * 若正在会议中, 退出登录时需要离开会议
     */
    private fun handleVoipLeave() {
        if (VoipHelper.isHandlingVoipCall()) {
            VoipManager.leaveMeeting(mActivity, null, null, VoipManager.getInstance().voipMeetingController.workplusVoipMeetingId, AtworkApplicationLike.getLoginUserHandleInfo(mActivity), null, object : VoipManager.OnHandleVoipMeetingListener {
                override fun onSuccess() {
                    LogUtil.e("VOIP", "leave success")
                }

                override fun networkFail(errorCode: Int, errorMsg: String) {

                }
            })
        }
    }

}