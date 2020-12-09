package com.foreveross.atwork.manager.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import com.foreveross.atwork.R
import com.foreveross.atwork.cordova.plugin.WorkPlusImagesPlugin
import com.foreveross.atwork.cordova.plugin.model.OpenFileDetailRequest
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.file.FileData
import com.foreveross.atwork.infrastructure.model.file.ImageItem
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk
import com.foreveross.atwork.manager.BasicNotificationManager
import com.foreveross.atwork.manager.NotificationBuilder
import com.foreveross.atwork.modules.common.activity.TransparentHandleCommonFileActivity
import com.foreveross.atwork.modules.file.fragement.CommonFileStatusFragment
import com.foreveross.atwork.modules.image.activity.MediaPreviewActivity
import java.io.File
import java.io.Serializable

object DownloadNoticeManager : BasicNotificationManager() {

    fun notifyNotificationProgress(context: Context, downloadId: Int, max: Int = 100, progress: Int, filePath: String) {
        val builder = NotificationBuilder(context)
        builder.setAutoCancel(false)
        builder.setOngoing(true)
        builder.setContentTitle(FileUtil.getName(filePath))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.icon_notice_small)

        } else {
            builder.setSmallIcon(R.mipmap.icon_logo)

        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.icon_logo))
        builder.setContentText(context.getString(R.string.downloading_state))

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            builder.setChannelId(BasicNotificationManager.WEAK_CHANNEL_ID)
        }

        builder.setProgress(max, progress, false)

        val notification = builder.build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(downloadId, notification)

    }

    fun notifyNotificationSuccess(context: Context, downloadId: Int, filePath: String, url: String) {

        clear(downloadId)

        val intent = TransparentHandleCommonFileActivity.getIntent(context, downloadId, filePath, url)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationBuilder(context)
        builder.setAutoCancel(true)

        builder.setContentTitle(FileUtil.getName(filePath))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.icon_notice_small)

        } else {
            builder.setSmallIcon(R.mipmap.icon_logo)

        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.icon_logo))
        builder.setContentText(context.getString(R.string.file_transfer_status_download_success))

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            builder.setChannelId(BasicNotificationManager.DEFAULT_CHANNEL_ID)
        }

        builder.setContentIntent(pendingIntent)

        val notification = builder.build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(downloadId, notification)
    }


    fun previewDownloadFileOnFileStatusView(path: String) {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false) { filePathFromCheck ->
            val fileType = FileData.getFileType(path)
            when (fileType) {
                FileData.FileType.File_Gif, FileData.FileType.File_Image -> {
                    val imageList = ArrayList<ImageItem>()
                    val imageItem = ImageItem()
                    imageItem.filePath = filePathFromCheck
                    imageList.add(imageItem)

                    val intent = MediaPreviewActivity.getImagePreviewIntent(BaseApplicationLike.baseContext, MediaPreviewActivity.FromAction.IMAGE_SELECT)
                    //todo ugly implementation
                    intent.putExtra(WorkPlusImagesPlugin.IMAGES_FROM_CORDOVA_CODE, true)
                    intent.putExtra(WorkPlusImagesPlugin.ACTION_SHOW_IMAGE, imageList as Serializable)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    BaseApplicationLike.baseContext.startActivity(intent)
                }

                else -> {
                    val file = File(filePathFromCheck)

                    val fileDetailRequest = OpenFileDetailRequest()
                    fileDetailRequest.fileName = file.name
                    fileDetailRequest.path = file.path
                    fileDetailRequest.fileSize = file.length()
                    fileDetailRequest.isImage = false
                    fileDetailRequest.needActionMore = false
                    fileDetailRequest.setFileStatus(FileStatus.DOWNLOADED)


                    CommonFileStatusFragment.showOverall(fileDetailRequest)
                }
            }
        }
    }




}