package com.foreveross.atwork.modules.common.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.notification.DownloadNoticeManager


class TransparentHandleCommonFileActivity: Activity() {


    private var filePath: String = StringUtils.EMPTY
    private var url: String = StringUtils.EMPTY
    private var downloadId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePath = intent.getStringExtra(DATA_FILE_PATH)
        url = intent.getStringExtra(DATA_DOWNLOAD_URL)
        downloadId = intent.getIntExtra(DATA_DOWNLOAD_ID, -1)

        DownloadNoticeManager.previewDownloadFileOnFileStatusView(filePath)
        DownloadNoticeManager.clear(downloadId)


        finish()

    }




    companion object {

        const val DATA_FILE_PATH = "DATA_FILE_PATH"
        const val DATA_DOWNLOAD_URL = "DATA_DOWNLOAD_URL"
        const val DATA_DOWNLOAD_ID = "DATA_DOWNLOAD_ID"

        fun getIntent(context: Context, downloadId: Int, filePath: String, url: String): Intent {
            val intent = Intent(context, TransparentHandleCommonFileActivity::class.java)
            intent.putExtra(DATA_FILE_PATH, filePath)
            intent.putExtra(DATA_DOWNLOAD_URL, url)
            intent.putExtra(DATA_DOWNLOAD_ID, downloadId)
            return intent
        }
    }
}