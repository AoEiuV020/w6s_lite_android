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
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo
import com.foreveross.atwork.modules.device.activity.LoginDeviceDetailActivity
import com.foreveross.atwork.modules.device.adapter.LoginDeviceListManagerAdapter
import com.foreveross.atwork.modules.device.manager.ACTION_REFRESH_DEVICE_INFO
import com.foreveross.atwork.modules.device.manager.ACTION_REMOVE_DEVICE_INFO
import com.foreveross.atwork.modules.device.manager.DATA_DEVICE_INFO
import com.foreveross.atwork.modules.device.manager.LoginDeviceManager
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_login_device_manager.*

class LoginDeviceManagerFragment: BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var vFooterIntro: View


    private lateinit var adapter: LoginDeviceListManagerAdapter

    private val loginDeviceList = arrayListOf<LoginDeviceInfo>()


    val broadcast = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {

                ACTION_REFRESH_DEVICE_INFO -> {
                    val handleDevice = intent.getParcelableExtra<LoginDeviceInfo>(DATA_DEVICE_INFO)
                    val index = loginDeviceList.indexOf(handleDevice)
                    if(-1 < index) {
                        loginDeviceList.removeAt(index)
                        loginDeviceList.add(index, handleDevice)
                        refreshUI()
                    }

                }

                ACTION_REMOVE_DEVICE_INFO -> {
                    val handleDevice = intent.getParcelableExtra<LoginDeviceInfo>(DATA_DEVICE_INFO)
                    loginDeviceList.remove(handleDevice).let {
                        if(it) {
                            refreshUI()
                        }
                    }
                }
            }
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_device_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initRecyclerView()
        registerListener()
        registerBroadcast()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBroadcast()
    }



    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)

        vFooterIntro = LayoutInflater.from(activity).inflate(R.layout.component_login_device_manager_bottom_tip, null)
    }

    private fun initUI() {
        tvTitle.text = getStrings(R.string.login_device_manager)

    }

    private fun initRecyclerView() {
        adapter = LoginDeviceListManagerAdapter(loginDeviceList)
        adapter.addFooterView(vFooterIntro)

        rvDeviceList.adapter = adapter

    }

    private fun refreshUI() {

        sortDevices()

        val withoutAuthCount = loginDeviceList.count {
            it.authenticated
        }

        val deviceMaxUnAuthCount = DomainSettingsManager.getInstance().loginDeviceMaxUnAuthCount
        //根据域设置做筛选, 只显示最大显示"免认证"的数量
        if(deviceMaxUnAuthCount < withoutAuthCount) {
            for(i in deviceMaxUnAuthCount until withoutAuthCount) {
                loginDeviceList[i].authenticated = false
            }

        }

        adapter.notifyDataSetChanged()

        vFooterIntro.isVisible = true
    }

    private fun sortDevices() {
        loginDeviceList.sortWith(Comparator { o1, o2 ->
            if (o1.authenticated && !o2.authenticated) {
                return@Comparator -1
            }

            if (!o1.authenticated && o2.authenticated) {
                return@Comparator 1
            }

            if (!o1.authenticated && !o2.authenticated) {
                return@Comparator o2.lastLoginTime.compareTo(o1.lastLoginTime)
            }


            return@Comparator o2.authenticatedTime.compareTo(o1.authenticatedTime)
//            return@Comparator o2.lastLoginTime.compareTo(o1.lastLoginTime)

        })
    }

    private fun initData() {
        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()
        LoginDeviceManager.queryLoginDevices(AtworkApplicationLike.baseContext, object : BaseNetWorkListener<List<LoginDeviceInfo>> {

            override fun onSuccess(result: List<LoginDeviceInfo>) {
                progressDialogHelper.dismiss()

                loginDeviceList.clear()
                loginDeviceList.addAll(result)

                refreshUI()
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                progressDialogHelper.dismiss()
                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        adapter.setOnItemClickListener { adapter, view, position ->

            activity?.let {
                val loginDeviceInfo = loginDeviceList[position]
                loginDeviceInfo.currentAuthenticatedDeviceIds = loginDeviceList
                        .filter { it.authenticated }
                        .mapNotNull { it.id }






                val intent = LoginDeviceDetailActivity.getIntent(it, loginDeviceInfo)
                startActivity(intent)

            }

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