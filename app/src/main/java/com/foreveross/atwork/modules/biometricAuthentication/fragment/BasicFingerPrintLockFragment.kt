package com.foreveross.atwork.modules.biometricAuthentication.fragment

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.support.BackHandledFragment
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify

abstract class BasicFingerPrintLockFragment: BackHandledFragment() {


    private var fingerPrintDialog: FingerPrintDialogFragment? = null

    override fun onStart() {
        super.onStart()

        startToAuth()

    }

    protected open fun shouldDoAuth() = true

    protected fun startToAuth() {

        if(!shouldDoAuth()) {
            return
        }

        if (isFingerPrintUserSetAndSystemEnable()) {

            if(null != fingerPrintDialog && fingerPrintDialog!!.isVisible) {
                return
            }

            fingerPrintDialog = FingerPrintDialogFragment.startToAuth(this@BasicFingerPrintLockFragment, object : IFingerPrintAuthListener {

                override fun onSucceed() {

                    finish()
                }

                override fun onDialogDismiss() {

                }

            })
        }
    }


    fun isFingerPrintSystemEnable(): Boolean  {
        val fingerprintIdentify = FingerprintIdentify(AtworkApplicationLike.baseContext)
        fingerprintIdentify.init()

        return fingerprintIdentify.isFingerprintEnable
    }


    fun isFingerPrintUserSetAndSystemEnable(): Boolean = PersonalShareInfo.getInstance().getFingerPrintSetting(AtworkApplicationLike.baseContext) && isFingerPrintSystemEnable()


}