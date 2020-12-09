package com.foreveross.atwork.modules.device.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.device.model.request.ModifyLoginDeviceInfoRequest
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.modules.device.activity.LoginDeviceModifyInfoActivity
import com.foreveross.atwork.modules.device.manager.LoginDeviceManager
import com.foreveross.atwork.modules.device.manager.refreshLoginDeviceInfo
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_login_device_modify_info.*

class LoginDeviceModifyInfoFragment: BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvRightEdit: TextView

    private var loginDeviceInfo: LoginDeviceInfo? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_device_modify_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initUI()
        registerListener()
    }

    override fun findViews(view: View) {

        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvRightEdit = view.findViewById(R.id.title_bar_common_right_text)
    }

    private fun initUI() {
        tvTitle.text = getStrings(R.string.set_login_device_name)
        ivBack.setImageResource(R.mipmap.icon_remove_back)
        tvRightEdit.setText(R.string.save)
        tvRightEdit.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))

        refreshUI()

        refreshEditBtnStatus()

        tvRightEdit.isVisible = true
    }

    private fun refreshUI() {
        loginDeviceInfo?.let {
            etModify.setText(it.deviceName)
            etModify.setSelection(etModify.text.length)
        }
    }

    private fun refreshEditBtnStatus() {
        if(StringUtils.isEmpty(etModify.text.toString())) {
            tvRightEdit.alpha = 0.5f
            tvRightEdit.isEnabled = false
            return
        }

        tvRightEdit.alpha = 1f
        tvRightEdit.isEnabled = true
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        etModify.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                ivModifyCancelBtn.isVisible = !StringUtils.isEmpty(text.toString())
                refreshEditBtnStatus()

            }
        })

        ivModifyCancelBtn.setOnClickListener { etModify.setText(StringUtils.EMPTY) }


        tvRightEdit.setOnClickListener {
            val loginDeviceInfoHandle = loginDeviceInfo ?: return@setOnClickListener

            val progressDialogHelper = ProgressDialogHelper(activity)
            progressDialogHelper.show()

            val modifyLoginDeviceInfoRequest = ModifyLoginDeviceInfoRequest(id = loginDeviceInfoHandle.id, deviceName = etModify.text.toString())
            LoginDeviceManager.modifyLoginDeviceInfo(AtworkApplicationLike.baseContext, modifyLoginDeviceInfoRequest, object : BaseCallBackNetWorkListener {

                override fun onSuccess() {

                    loginDeviceInfoHandle.deviceName = etModify.text.toString()
                    refreshLoginDeviceInfo(AtworkApplicationLike.baseContext, loginDeviceInfoHandle)

                    progressDialogHelper.dismiss()
                    toastOver(R.string.setting_success)

                    finish()

                }

                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    progressDialogHelper.dismiss()
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

            })

        }
    }

    private fun initData() {
        loginDeviceInfo = arguments?.getParcelable(LoginDeviceModifyInfoActivity.DATA_LOGIN_DEVICE_DETAIL)

    }



    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}