package com.foreveross.atwork.modules.dropbox.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItem
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.modules.dropbox.isFileOverdue
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil
import com.foreveross.atwork.utils.TimeViewUtil

/**
 *  create by reyzhang22 at 2019-11-12
 */
class DropboxShareItem(context: Context?): LinearLayout(context) {

    lateinit var ivFileIcon:        ImageView
    lateinit var tvFileName:        TextView
    lateinit var tvShareDate:       TextView
    lateinit var tvFileSize:        TextView
    lateinit var tvValidStatus:     TextView
    lateinit var tvDownloadTimes:   TextView
    lateinit var tvDownloadLimit:   TextView

    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_my_dropbox_share, this)
        findView(view)
    }


    private fun findView(view: View) {
        ivFileIcon = view.findViewById(R.id.iv_file_type_icon)
        tvFileName = view.findViewById(R.id.tv_file_name)
        tvShareDate = view.findViewById(R.id.tv_share_date)
        tvFileSize = view.findViewById(R.id.tv_file_size)
        tvValidStatus = view.findViewById(R.id.tv_valid_status)
        tvDownloadTimes = view.findViewById(R.id.tv_download_times)
        tvDownloadLimit = view.findViewById(R.id.tv_download_limit)
    }

    @SuppressLint("SetTextI18n")
    fun setShareItem(shareItem: ShareItem?) {
        ivFileIcon.setImageResource(FileMediaTypeUtil.getFileTypeByExtension(shareItem?.mExtension))
        tvFileName.text = shareItem?.mName
        tvShareDate.text = TimeViewUtil.getSimpleUserCanViewTime(BaseApplicationLike.baseContext , shareItem!!.mCreateTime)
        tvFileSize.text = FileUtil.formatFromSize(shareItem.mSize)
        tvDownloadTimes.text = context.getString(R.string.file_transfer_status_downloaded) + ":${shareItem.mDownloads}"
        tvDownloadLimit.text = context.getString(R.string.download_limit) + ":${shareItem.mDownloadsLimit}"
        val expiredTime = shareItem.mExpireTime
        alpha = 1f
        when(isFileOverdue(expiredTime)) {
            1 -> tvValidStatus.text = context.getString(R.string.invalid_time) + ": " + context.getString(R.string.valid_forever)
            0 -> tvValidStatus.text = context.getString(R.string.invalid_time) + ": ${TimeUtil.getStringForMillis(expiredTime, TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext))}"
            -1 -> {
                alpha= 0.5f
                tvValidStatus.text = context.getString(R.string.invalid_time) + ": " + context.getString(R.string.overdued)
            }
        }


    }

}