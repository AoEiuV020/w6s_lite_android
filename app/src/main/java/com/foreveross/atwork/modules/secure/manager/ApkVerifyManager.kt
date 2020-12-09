package com.foreveross.atwork.modules.secure.manager

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.AsyncTask
import android.view.KeyEvent
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.secure.ApkVerifyInfoSyncNetService
import com.foreveross.atwork.api.sdk.secure.model.response.ApkVerifyInfoResponse
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.VerifyApkUtil
import com.foreveross.atwork.manager.listener.BaseQueryListener
import com.foreveross.atwork.support.BaseActivity
import java.util.concurrent.Executors

object ApkVerifyManager {

    private var lastVerifySuccessTime = -1L

    private var singleThreadPool = Executors.newFixedThreadPool(1)


    fun checkLegal(baseActivity: BaseActivity) {

        doCheckLegal(BaseQueryListener { itResult ->
            if (false == itResult) {
                var dialog: AtworkAlertDialog? = baseActivity.verifyLegalAlertDialog
                if (null == dialog) {
                    dialog = AtworkAlertDialog(baseActivity, AtworkAlertDialog.Type.SIMPLE)

                } else {
                    if (dialog.isShowing) {
                        return@BaseQueryListener
                    }

                }

                popDialogNeverDie(dialog, baseActivity)


            }
        })
    }

    private fun popDialogNeverDie(dialog: AtworkAlertDialog, baseActivity: BaseActivity) {
        dialog.apply {
            setContent(R.string.chaos_tip)
            setCanceledOnTouchOutside(false)
            hideDeadBtn()
                    .setClickBrightColorListener {
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }

            setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    return@OnKeyListener true
                }
                false
            })

            show()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun doCheckLegal(listener: BaseQueryListener<Boolean?>) {
        if(BaseApplicationLike.sIsDebug) {
            return
        }

        if(!VerifyApkUtil.isApkPathLegal(AtworkApplicationLike.baseContext)) {
            return
        }

        if(!AtworkConfig.KPPA_VERIFY_CONFIG.needVerify()) {
            return
        }

        if (AtworkConfig.KPPA_VERIFY_CONFIG.getRequestInterval() >= System.currentTimeMillis() - lastVerifySuccessTime) {
            return
        }

        object : AsyncTask<Void, Void, Boolean?>() {

            override fun doInBackground(vararg params: Void?): Boolean? {
                val httpResult = ApkVerifyInfoSyncNetService.getApkVerifyInfo(AtworkApplicationLike.baseContext)
                if (!httpResult.isNetSuccess) {
                    return null
                }


                if (httpResult.isRequestSuccess) {
                    val apkVerifyInfoResponse = httpResult.resultResponse as ApkVerifyInfoResponse

                    //verify successfully
                    if (apkVerifyInfoResponse.infoList.isNotEmpty()) {
                        lastVerifySuccessTime = System.currentTimeMillis()
                        return true
                    }

                }
                return false
            }


            override fun onPostExecute(result: Boolean?) {
                listener.onSuccess(result)
            }


        }.executeOnExecutor(singleThreadPool)

    }
}


