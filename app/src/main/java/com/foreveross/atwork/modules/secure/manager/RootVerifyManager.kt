package com.foreveross.atwork.modules.secure.manager

import android.content.DialogInterface
import android.view.KeyEvent
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.root.RootSniffer
import com.foreveross.atwork.support.BaseActivity

object RootVerifyManager {

    private const val VERIFY_INTERVAL = 30 * 60 * 1000

    private var lastVerifySuccessTime = -1L


    fun checkRoot(baseActivity: BaseActivity) {

        if(!AtworkConfig.KPPA_VERIFY_CONFIG.crackCheck) {
            return
        }


        if (VERIFY_INTERVAL >= System.currentTimeMillis() - lastVerifySuccessTime) {
            return
        }

        if(RootSniffer.isRoot(AtworkApplicationLike.baseContext)) {
            var dialog: AtworkAlertDialog? = baseActivity.verifyLegalAlertDialog
            if (null == dialog) {
                dialog = AtworkAlertDialog(baseActivity, AtworkAlertDialog.Type.SIMPLE)

            } else {
                if (dialog.isShowing) {
                    return
                }

            }

            popDialogNeverDie(dialog, baseActivity)


        } else {

            lastVerifySuccessTime = System.currentTimeMillis()

        }
    }

    private fun popDialogNeverDie(dialog: AtworkAlertDialog, baseActivity: BaseActivity) {
        dialog.apply {
            setContent(R.string.chaos2_tip)
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

}