package com.foreveross.atwork.modules.setting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.settingPage.W6sAccountAndSecureSetting
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.device.activity.LoginDeviceManagerActivity
import com.foreveross.atwork.modules.setting.activity.ChangePasswordActivity
import com.foreveross.atwork.modules.setting.adapter.W6sAccountAndSecureSettingAdapter
import com.foreveross.atwork.modules.setting.component.W6sSettingItemDecoration
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_setting_new.*

class W6sAccountAndSecureSettingFragment : BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    private lateinit var w6sAccountAndSecureSettingAdapter: W6sAccountAndSecureSettingAdapter

    private val accountAndSecureSettingList = arrayListOf(

            W6sAccountAndSecureSetting.ACCOUNT,

            W6sAccountAndSecureSetting.MOBILE,

            W6sAccountAndSecureSetting.MODIFY_PWD,

            W6sAccountAndSecureSetting.DEVICES


    )

    private val accountAndSecureSettingDistributeList = arrayListOf(
            arrayListOf(W6sAccountAndSecureSetting.ACCOUNT, W6sAccountAndSecureSetting.MOBILE),

            arrayListOf(W6sAccountAndSecureSetting.DEVICES)

    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(AtworkConfig.SETTING_PAGE_CONFIG.isInvisible(W6sAccountAndSecureSetting.MODIFY_PWD)) {
            accountAndSecureSettingList.remove(W6sAccountAndSecureSetting.MODIFY_PWD)
            accountAndSecureSettingDistributeList.forEach {
                it.remove(W6sAccountAndSecureSetting.MODIFY_PWD)
            }
        }


        if(invisibleLoginDevices()) {
            accountAndSecureSettingList.remove(W6sAccountAndSecureSetting.DEVICES)
            accountAndSecureSettingDistributeList.forEach {
                it.remove(W6sAccountAndSecureSetting.DEVICES)
            }
        }

        initUI()
        initRecyclerView()

        registerListener()

    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
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
        tvTitle.text = getStrings(R.string.account_and_secure)
    }

    private fun initRecyclerView() {
        w6sAccountAndSecureSettingAdapter = W6sAccountAndSecureSettingAdapter(accountAndSecureSettingList, accountAndSecureSettingDistributeList, null)

        rvSettings.adapter = w6sAccountAndSecureSettingAdapter
        rvSettings.addItemDecoration(W6sSettingItemDecoration(accountAndSecureSettingList, accountAndSecureSettingDistributeList))
    }


    private fun initData() {


        AtworkApplicationLike.getLoginUser(object : UserAsyncNetService.OnQueryUserListener {
            override fun onSuccess(loginUser: User) {
                w6sAccountAndSecureSettingAdapter.loginUser = loginUser
                w6sAccountAndSecureSettingAdapter.notifyDataSetChanged()
            }

            override fun networkFail(errorCode: Int, errorMsg: String) {
                ErrorHandleUtil.handleBaseError(errorCode, errorMsg)
            }
        })

    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }


        w6sAccountAndSecureSettingAdapter.setOnItemClickListener { adapter, view, position ->
            when (accountAndSecureSettingList[position]) {
                W6sAccountAndSecureSetting.MODIFY_PWD -> routeModifyPwd()
                W6sAccountAndSecureSetting.DEVICES -> routeLoginDeviceManger()
            }
        }



    }

    private fun invisibleLoginDevices() =
            AtworkConfig.SETTING_PAGE_CONFIG.isInvisible(W6sAccountAndSecureSetting.DEVICES) || !DomainSettingsManager.getInstance().loginDeviceAuthEnable

    private fun routeLoginDeviceManger() {
        val intent = LoginDeviceManagerActivity.getIntent(mActivity)
        startActivity(intent)
    }

    private fun routeModifyPwd() {
        if (AtworkConfig.hasCustomModifyPwdJumpUrl()) {
            //跳转定制的修改密码 web 页面
            val webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(AtworkConfig.AUTH_ROUTE_CONFIG.customModifyPwdJumpUrl)
                    .setNeedShare(false)

            startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction))
            return

        }

        //跳转到修改密码页面
        val intent = ChangePasswordActivity.getIntent(mActivity)
        startActivity(intent)
    }


}