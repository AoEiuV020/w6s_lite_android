package com.foreveross.atwork.modules.device.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.modules.device.activity.LoginDeviceDetailActivity
import com.foreveross.atwork.modules.device.activity.LoginDeviceModifyInfoActivity
import com.foreveross.atwork.modules.device.manager.*
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_login_device_detail.*

class LoginDeviceDetailFragment : BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    private var loginDeviceInfo: LoginDeviceInfo? = null

    val broadcast = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {

                ACTION_REFRESH_DEVICE_INFO -> {
                    val handleDevice = intent.getParcelableExtra<LoginDeviceInfo>(DATA_DEVICE_INFO)
                    loginDeviceInfo = handleDevice
                    refreshUI()
                }

            }
        }

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_device_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initUI()
        registerListener()
        registerBroadcast()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBroadcast()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
    }

    private fun initUI() {
        tvTitle.text = getStrings(R.string.login_device_detail)

    }

    private fun initData() {
        loginDeviceInfo = arguments?.getParcelable(LoginDeviceDetailActivity.DATA_LOGIN_DEVICE_DETAIL)
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }


        switchBtn.setOnClickNotPerformToggle {
            val loginDeviceInfoHandle = loginDeviceInfo ?: return@setOnClickNotPerformToggle

            val waitToOpen = !switchBtn.isChecked

            if (waitToOpen) {
                if(DomainSettingsManager.getInstance().loginDeviceMaxUnAuthCount  <= loginDeviceInfoHandle.currentAuthenticatedDeviceIds.size) {
                    toast(R.string.login_device_without_auth_meet_max_warn)
                    return@setOnClickNotPerformToggle
                }

                setLoginDevicesWithoutAuthStatus(loginDeviceInfoHandle)

            } else {
                setLoginDevicesNeedAuthStatus(loginDeviceInfoHandle)

            }
        }

        tvDeleteDevice.setOnClickListener {
            AtworkAlertDialog(activity, AtworkAlertDialog.Type.SIMPLE)
                    .setContent(R.string.ask_delete_login_device)
                    .setClickBrightColorListener {
                        removeLoginDevices()
                    }
                    .show()
        }


        rlDeviceName.setOnClickListener {
            val loginDeviceInfoHandle = loginDeviceInfo ?: return@setOnClickListener
            activity?.let {
                val intent = LoginDeviceModifyInfoActivity.getIntent(it, loginDeviceInfoHandle)
                startActivity(intent)
            }

        }

    }

    private fun removeLoginDevices() {
        val loginDeviceInfoHandle = loginDeviceInfo ?: return

        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()

        LoginDeviceManager.removeLoginDevices(AtworkApplicationLike.baseContext, ListUtil.makeSingleList(loginDeviceInfoHandle.id), object : BaseCallBackNetWorkListener {
            override fun onSuccess() {
                progressDialogHelper.dismiss()

                removeLoginDeviceInfo(AtworkApplicationLike.baseContext, loginDeviceInfoHandle)
                finish()

            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                progressDialogHelper.dismiss()
                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })
    }

    private fun setLoginDevicesNeedAuthStatus(loginDeviceInfoHandle: LoginDeviceInfo) {
        LoginDeviceManager.setLoginDevicesNeedAuthStatus(AtworkApplicationLike.baseContext, ListUtil.makeSingleList(loginDeviceInfoHandle.id), object : BaseCallBackNetWorkListener {
            override fun onSuccess() {
                loginDeviceInfoHandle.authenticated = false

                (loginDeviceInfoHandle.currentAuthenticatedDeviceIds as ArrayList).remove(loginDeviceInfoHandle.id)

                refreshLoginDeviceInfo(AtworkApplicationLike.baseContext, loginDeviceInfoHandle)

                switchBtn.toggle()

            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })
    }

    private fun setLoginDevicesWithoutAuthStatus(loginDeviceInfoHandle: LoginDeviceInfo) {
        LoginDeviceManager.setLoginDevicesWithoutAuthStatus(AtworkApplicationLike.baseContext, ListUtil.makeSingleList(loginDeviceInfoHandle.id), object : BaseCallBackNetWorkListener {
            override fun onSuccess() {
                loginDeviceInfoHandle.authenticated = true
                loginDeviceInfoHandle.authenticatedTime = System.currentTimeMillis()

                (loginDeviceInfoHandle.currentAuthenticatedDeviceIds as ArrayList).add(loginDeviceInfoHandle.id)

                refreshLoginDeviceInfo(AtworkApplicationLike.baseContext, loginDeviceInfoHandle)

                switchBtn.toggle()
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })
    }


    private fun refreshUI() {
        loginDeviceInfo?.deviceName?.let {
            tvDeviceName.text = it
        }

        loginDeviceInfo?.authenticated?.let {
            switchBtn.isChecked = it
        }

        loginDeviceInfo?.deviceSystemInfo?.let {
            tvDeviceType.text = it
        }

        loginDeviceInfo?.lastLoginTime?.let {
            tvDeviceLastLoginTime.text = TimeUtil.getStringForMillis(it, TimeUtil.getTimeFormat2(AtworkApplicationLike.baseContext))
        }


    }


    private fun registerBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_REFRESH_DEVICE_INFO)
        intentFilter.addAction(ACTION_REMOVE_DEVICE_INFO)

        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).registerReceiver(broadcast, intentFilter)
    }

    private fun unregisterBroadcast() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(broadcast)
    }



    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}