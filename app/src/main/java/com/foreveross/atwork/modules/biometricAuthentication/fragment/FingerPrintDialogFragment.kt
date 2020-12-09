package com.foreveross.atwork.modules.biometricAuthentication.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.foreverht.workplus.ui.component.dialogFragment.BasicUIDialogFragment
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.Logger
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import kotlinx.android.synthetic.main.fragment_fingerprint_auth.*


interface IFingerPrintAuthListener {
    fun onDialogDismiss()

    fun onSucceed()
}

class FingerPrintDialogFragment : BasicUIDialogFragment() {

    var fingerPrintAuthListener: IFingerPrintAuthListener? = null

    private lateinit var fingerprintIdentify: FingerprintIdentify

    private var firstResume = true

    private val resumeIdentifyRunnable = { fingerprintIdentify.resumeIdentify() }
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val popupView = inflater.inflate(R.layout.fragment_fingerprint_auth, null)

        return popupView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //该方法需要放在onViewCreated比较合适, 若在 onStart 在部分机型(如:小米3)会出现闪烁的情况
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent_70)
        isCancelable = false

        registerListener()
        initData()
    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setTransparentFullScreen(dialog?.window)
    }

    private fun initData() {
        fingerprintIdentify = FingerprintIdentify(AtworkApplicationLike.baseContext)
        fingerprintIdentify.setSupportAndroidL(true)
        fingerprintIdentify.setExceptionListener {
            LogUtil.e(it.localizedMessage)

            Logger.e(TAG, it.localizedMessage)

        }

        fingerprintIdentify.init()

        LogUtil.e("fingerprint  isHardwareEnable(): ${fingerprintIdentify.isHardwareEnable}  isRegisteredFingerprint(): ${fingerprintIdentify.isRegisteredFingerprint}   isFingerprintEnable()  ${fingerprintIdentify.isFingerprintEnable}")


        fingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, object : BaseFingerprint.IdentifyListener {
            override fun onSucceed() {
                LogUtil.e("fingerprint -> onSucceed()")
                fingerPrintAuthListener?.onSucceed()
                dismiss()

            }

            override fun onNotMatch(availableTimes: Int) {
                LogUtil.e("fingerprint -> onNotMatch() availableTimes: $availableTimes")

                tvFingerPrintGuide.text = AtworkApplicationLike.getResourceString(R.string.please_verify_fingerprint_again)
            }

            override fun onFailed(isDeviceLocked: Boolean) {
                LogUtil.e("fingerprint -> onFailed() isDeviceLocked: $isDeviceLocked")

                tvFingerPrintGuide.text = AtworkApplicationLike.getResourceString(R.string.verify_fingerprint_too_frequently)


            }

            override fun onStartFailedByDeviceLocked() {
                LogUtil.e("fingerprint -> onStartFailedByDeviceLocked()")
                tvFingerPrintGuide.text = AtworkApplicationLike.getResourceString(R.string.verify_fingerprint_too_frequently)
            }

        })
    }


    override fun onResume() {
        super.onResume()

        LogUtil.e("bio onResume")

        if(!firstResume) {
            //部分手机开屏后指纹模块任处于"冻结"状态, 立马启用的话会
            handler.postDelayed(resumeIdentifyRunnable, 100)

        }

        firstResume = false

    }

    override fun onPause() {
        super.onPause()

        LogUtil.e("bio onPause")

        handler.removeCallbacks(resumeIdentifyRunnable)
        fingerprintIdentify.cancelIdentify()
    }

    override fun onDestroy() {
        super.onDestroy()

        fingerprintIdentify.cancelIdentify()
    }

    private fun registerListener() {
        tvCancel.setOnClickListener { dismiss() }

    }

    override fun dismiss() {
        super.dismiss()
        fingerprintIdentify.cancelIdentify()
        fingerPrintAuthListener?.onDialogDismiss()
    }


    companion object {

        private const val MAX_AVAILABLE_TIMES = 3

        private const val TAG = "fingerprint"


        @JvmStatic
        fun startToAuth(fragment: Fragment, fingerPrintAuthListener: IFingerPrintAuthListener? = null): FingerPrintDialogFragment {
            val fingerPrintDialogFragment = FingerPrintDialogFragment()
            fingerPrintDialogFragment.fingerPrintAuthListener = fingerPrintAuthListener
            fingerPrintDialogFragment.show(fragment.childFragmentManager, "fingerPrintDialog")
            return fingerPrintDialogFragment
        }
    }


}