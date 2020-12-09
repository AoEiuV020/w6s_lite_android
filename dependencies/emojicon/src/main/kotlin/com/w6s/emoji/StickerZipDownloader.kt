package com.w6s.emoji

import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.view.View
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.ZipUtil
import java.io.File
import java.io.IOException
import java.util.*

public val MESSAGE_STICKER_DOWNLOADED = 0;

public val MESSAGE_STICKER_DOWNLOAD_FAIL = 1

var curViewHolder: EmojiPagerAdapter.DownloadViewHolder? = null


fun downloadStickerAlbumZip(context: Context, stickerCategory: StickerCategory, downloadViewHolder: EmojiPagerAdapter.DownloadViewHolder?, handler: Handler?, quite: Boolean) {
    object : AsyncTask<Void, Double, Boolean>() {

        override fun onPreExecute() {
            curViewHolder = downloadViewHolder
            showDownloadStatus(true, downloadViewHolder, quite)
        }

        override fun doInBackground(vararg params: Void?): Boolean {

            //表情根目录
            val stickerRootPath = AtWorkDirUtils.getInstance().stickerRootPath
            //表情zip包目录
            val stickerZipPath = stickerRootPath + File.separator + stickerCategory.getCategoryId() + ".zip"
             //表情解压目录
            val stickerReleaseDirPath = AtWorkDirUtils.getInstance().getStickerDirByCategoryId(stickerCategory.getCategoryId())

            //假如zip包存在
            if (FileUtil.isExist(stickerZipPath)) {
                FileUtil.delete(stickerZipPath)
            }

            val downloadZipUrl = String.format(UrlConstantManager.getInstance().stickerAlbumZipUrl, stickerCategory.getCategoryId(), LoginUserInfo.getInstance().getLoginUserAccessToken(context))
            val tempZipPath = stickerRootPath + stickerCategory.getCategoryId() + ".zip.tmp"

            val httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                    DownloadFileParamsMaker.newRequest().setDownloadId(UUID.randomUUID().toString()).setDownloadUrl(downloadZipUrl).setDownloadPath(tempZipPath).setEncrypt(false).setProgressListener(
                            MediaCenterHttpURLConnectionUtil.MediaProgressListener { value, size -> publishProgress(value, size) }
                    )
            )
            if (httpResult.isNetSuccess) {
                try {
                    //删除旧目录数据
                    FileUtil.deleteFile(stickerReleaseDirPath, true)

                    val file = File(stickerReleaseDirPath)
                    file.mkdirs()

                    FileUtil.copyFile(tempZipPath, stickerZipPath)

                    FileUtil.deleteFile(tempZipPath, true)

                    ZipUtil.upZipFile(stickerZipPath, stickerReleaseDirPath, true)
                    StickerManager.instance.loadStickerAlbumConfig(stickerCategory)
                    //假如zip包存在
                    if (FileUtil.isExist(stickerZipPath)) {
                        FileUtil.delete(stickerZipPath)
                    }
                    return true
                } catch (e: IOException) {
                    e.printStackTrace()

                }

            }
            stickerCategory.setStatus(-1)
            return false
        }

        override fun onProgressUpdate(vararg progress: Double?) {
            val progressInt = progress[0]?.toInt()
            stickerCategory.setDownloadProgress(if (progressInt == null) 0  else progressInt)
            if (curViewHolder != null && curViewHolder?.id == downloadViewHolder?.id && !quite) {
                curViewHolder?.downloadingView?.visibility = View.VISIBLE

                curViewHolder?.progressText?.text = "$progressInt%"
                curViewHolder?.progressView?.progress = if (progressInt == null) 0  else progressInt
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (result == null || !result) {
                showDownloadStatus(false, downloadViewHolder, quite)
                return;
            }
            if (curViewHolder != null && curViewHolder?.id == downloadViewHolder?.id && !quite) {
                curViewHolder?.downloadingView?.visibility = View.VISIBLE
                val progressInt = 100
                curViewHolder?.progressText?.text = "$progressInt%"
                curViewHolder?.progressView?.progress = if (progressInt == null) 0  else progressInt

            }
            handler?.obtainMessage(MESSAGE_STICKER_DOWNLOADED)?.sendToTarget()

        }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

}

fun setCurDownloadHolder(viewHodler : EmojiPagerAdapter.DownloadViewHolder) {
    curViewHolder = viewHodler
}


fun showDownloadStatus(show: Boolean, viewHolder: EmojiPagerAdapter.DownloadViewHolder?, quite: Boolean) {
    if (curViewHolder != null && curViewHolder?.id == viewHolder?.id && !quite) {
        curViewHolder?.downloadingView?.visibility = if (show) View.VISIBLE else View.GONE
        curViewHolder?.unDownloadView?.visibility = if (show) View.GONE else View.VISIBLE
    }
}