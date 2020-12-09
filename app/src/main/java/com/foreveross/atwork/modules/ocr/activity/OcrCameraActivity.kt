package com.foreveross.atwork.modules.ocr.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper
import com.foreveross.atwork.modules.ocr.fragment.OcrCameraFragment
import com.foreveross.atwork.modules.ocr.model.OcrRequestAction
import com.foreveross.atwork.support.SingleFragmentActivity


class OcrCameraActivity: SingleFragmentActivity() {



    override fun createFragment(): Fragment {
        return OcrCameraFragment()
    }

    override fun changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, false)
    }


    companion object {

        const val DATA_OCR_REQUEST_ACTION = "DATA_OCR_REQUEST_ACTION"
        const val DATA_OCR_RESPONSE_ACTION = "DATA_OCR_REQUEST_ACTION"
        const val REQUEST_CODE = 0x102

        fun startOcrCameraActivity(context: Activity, action: OcrRequestAction?) {
            val intent = getStartOcrCameraIntent(context, action)
            context.startActivityForResult(intent, REQUEST_CODE)
        }

        fun getStartOcrCameraIntent(context: Activity, action: OcrRequestAction?): Intent {
            val intent = Intent(context, OcrCameraActivity::class.java)
            action?.let {
                intent.putExtra(DATA_OCR_REQUEST_ACTION, action)
            }
            return intent
        }
    }
}