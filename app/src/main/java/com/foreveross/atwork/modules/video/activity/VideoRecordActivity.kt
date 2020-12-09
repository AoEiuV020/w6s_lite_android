package com.foreveross.atwork.modules.video.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper
import com.foreveross.atwork.modules.video.fragment.VideoRecordFragment
import com.foreveross.atwork.modules.video.model.VideoRecordRequestAction
import com.foreveross.atwork.support.SingleFragmentActivity

class VideoRecordActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment = VideoRecordFragment()

    override fun changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, true)
    }

    companion object {

        const val DATA_VIDEO_RECORD_REQUEST_ACTION = "DATA_VIDEO_RECORD_REQUEST_ACTION"
        const val DATA_VIDEO_RECORD_RESPONSE = "DATA_VIDEO_RECORD_RESPONSE"
        const val REQUEST_CODE = 0x102

        fun startVideoRecordActivity(context: Activity, action: VideoRecordRequestAction?) {
            val intent = getStartVideoRecordIntent(context, action)
            context.startActivityForResult(intent, REQUEST_CODE)
        }

        fun getStartVideoRecordIntent(context: Activity, action: VideoRecordRequestAction?): Intent {
            val intent = Intent(context, VideoRecordActivity::class.java)
            action?.let {
                intent.putExtra(DATA_VIDEO_RECORD_REQUEST_ACTION, action)
            }
            return intent
        }
    }

}