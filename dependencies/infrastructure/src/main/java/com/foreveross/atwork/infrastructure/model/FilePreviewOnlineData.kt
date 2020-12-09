package com.foreveross.atwork.infrastructure.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilePreviewOnlineData (
        //文件名
        internal var fileName: String = "",
        //文件Id
        internal var fileId: String = "",
        //文件类型
        internal var fileType: String = "",
        //文件下载地址
        internal var fileDownloadUrl: String = ""
): Parcelable{
    fun getFileName(): String{
        return fileName
    }

    fun getFileId(): String{
        return fileId
    }

    fun getFileType(): String{
        return fileType
    }

    fun getFileDownloadUrl(): String{
        return fileDownloadUrl
    }
}