package com.foreveross.atwork.modules.device.fragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult
import com.foreveross.atwork.api.sdk.auth.model.LoginWithMobileRequest
import com.foreveross.atwork.api.sdk.auth.model.PhoneSecureCodeRequestJson
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.AppUtil
import com.foreveross.atwork.infrastructure.utils.CustomerHelper
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.modules.device.activity.LoginDeviceAuthActivity
import com.foreveross.atwork.modules.login.listener.BasicLoginNetListener
import com.foreveross.atwork.modules.login.service.LoginService
import com.foreveross.atwork.modules.login.util.LoginHelper
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.support.BaseActivity
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.foreveross.atwork.utils.ViewHelper
import kotlinx.android.synthetic.main.fragment_login_device_auth.*

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class LoginDeviceAuthFragment:BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    private var loginDeviceNeedAuthResult: LoginDeviceNeedAuthResult? = null

    private var timeLeft: Int = 0
    private var timeCountDownScheduledFuture: ScheduledFuture<*>? = null
    private val timeCountDownService = Executors.newScheduledThreadPool(1)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_device_auth, container, false)
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
    }


    private fun initUI() {
        tvTitle.text = getStrings(R.string.title_login_device_auth)

        loginDeviceNeedAuthResult?.let {
            tvMobile.text = it.userPhone

        }

        onChangeAuthBtnStatus()

        tvTip1.text = DomainSettingsManager.getInstance().loginDeviceUnAuthPrompt

        tvTip2.text = DomainSettingsManager.getInstance().loginDeviceRefuseAuthPrompt
    }

    private fun initData() {
        loginDeviceNeedAuthResult = arguments?.getParcelable(LoginDeviceAuthActivity.DATA_LOGIN_DEVICE_NEED_AUTH_RESULT)
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        etInputSecureCode.setOnFocusChangeListener { v, hasFocus -> ViewHelper.focusOnLine(vBottomLineSecureCode, hasFocus) }

        etInputSecureCode.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(content: Editable) {
                onChangeAuthBtnStatus()
            }
        })

        tvAuth.setOnClickListener {
            handleLogin()
        }

        tvSendSecureCode.setOnClickListener {

            if (null == loginDeviceNeedAuthResult) {
                return@setOnClickListener
            }

            if(isSecureCodeResendTimeCountDowning()) {
                return@setOnClickListener
            }


            val progressDialogHelper = ProgressDialogHelper(activity)
            progressDialogHelper.show()

            val phoneSecureCodeRequest = PhoneSecureCodeRequestJson().apply {
                mDomainId = AtworkConfig.DOMAIN_ID
                mBit = 4
                mFrozenSeconds = 60
                mSurvivalSeconds = 300
                mType = "phone"
                mAddresser = AtworkConfig.getDeviceId()
                mSubsystem = AppUtil.getAppName(AtworkApplicationLike.baseContext)
                mRecipient = loginDeviceNeedAuthResult!!.userPhone

                if (CustomerHelper.isKwg(BaseApplicationLike.baseContext)) {
                    loginDeviceNeedAuthResult?.name?.let {
                        mTemplate = "尊敬的合景员工$it，现有新的设备尝试登陆到你的KK帐号，验证码为:%s，请使用该码验证登陆新设备。"
                    }
                }

            }
            LoginService(AtworkApplicationLike.baseContext).requestPhoneSecureCode(phoneSecureCodeRequest, object : BaseCallBackNetWorkListener {
                override fun onSuccess() {
                    progressDialogHelper.dismiss()

                    toast(R.string.secure_code_sent_successfully);

                    startTimeCountDown()

                }

                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    progressDialogHelper.dismiss()

                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

            })
        }

    }


    private fun startTimeCountDown() {
        timeLeft = 60
        tvSendSecureCode.text = "${timeLeft}s"
        tvSendSecureCode.alpha = 0.5f
        tvSendSecureCode.isEnabled = false

        timeCountDownScheduledFuture = timeCountDownService.scheduleAtFixedRate({

            tvSendSecureCode.post {
                timeLeft--
                tvSendSecureCode.text = "${timeLeft}s"

                if (0 == timeLeft) {

                    tvSendSecureCode.setText(R.string.re_send_secure_code)
                    tvSendSecureCode.alpha = 1f
                    tvSendSecureCode.isEnabled = true

                    timeCountDownScheduledFuture?.cancel(true)
                    timeCountDownScheduledFuture = null
                }
            }


        }, 1000, 1000, TimeUnit.MILLISECONDS)
    }

    private fun isSecureCodeResendTimeCountDowning(): Boolean {
        return null != timeCountDownScheduledFuture
    }

    private fun handleLogin() {
        if (null == loginDeviceNeedAuthResult) {
            return
        }

        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()

        val loginWithMobileRequest = LoginWithMobileRequest().apply {
            ip = AtworkConfig.getDeviceId()
            clientId = loginDeviceNeedAuthResult!!.userPhone
            clientPrincipal = loginDeviceNeedAuthResult!!.username
            clientSecret = etInputSecureCode.text.toString()
            val romChannel = RomUtil.getRomChannel()
            if (!StringUtils.isEmpty(romChannel)) {
                channelVendor = romChannel
                channelId = AppUtil.getPackageName(context)
                productVersion = AppUtil.getVersionName(context)

            }

            deviceName = systemModel
            deviceSystemInfo = "Android " + Build.VERSION.RELEASE


        }

        LoginService(AtworkApplicationLike.baseContext).loginWithMobile(loginWithMobileRequest, loginDeviceNeedAuthResult!!.preInputPwd, object : BasicLoginNetListener {
            override fun loginSuccess(clientId: String, needInitPwd: Boolean) {
                progressDialogHelper.dismiss()

                LoginHelper.handleFinishLogin(activity as BaseActivity, needInitPwd, loginDeviceNeedAuthResult!!.username, loginDeviceNeedAuthResult!!.preInputPwd)
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                progressDialogHelper.dismiss()

                if(202521 == errorCode || 202523 == errorCode) {
                    toast(R.string.WalletModifyPwd_202521)
                    return
                }


                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })
    }

    private fun onChangeAuthBtnStatus() {

        refreshSendSecureCodeBtnStatus()

        if (StringUtils.isEmpty(etInputSecureCode.text.toString())) {
            tvAuth.setBackgroundResource(R.drawable.shape_login_rect_input_nothing)
            tvAuth.isEnabled = false
            return
        }


        tvAuth.setBackgroundResource(R.drawable.shape_login_rect_input_something)
        tvAuth.isEnabled = true


    }


    private fun refreshSendSecureCodeBtnStatus() {
        if (isSecureCodeResendTimeCountDowning()) {
            tvSendSecureCode.alpha = 0.5f
            tvSendSecureCode.isEnabled = false

        } else {
            tvSendSecureCode.alpha = 1f
            tvSendSecureCode.isEnabled = true
        }

    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}