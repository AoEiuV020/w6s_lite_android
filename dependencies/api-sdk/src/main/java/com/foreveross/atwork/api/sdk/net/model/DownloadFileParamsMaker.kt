package com.foreveross.atwork.api.sdk.net.model

import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager

/**
 *  create by reyzhang22 at 2019-12-25
 */
class DownloadFileParamsMaker {

    private var downloadId:    String = ""
    private var downloadUrl:   String = ""
    private var downloadPath:  String = ""
    private var downloadType:  MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE = MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE
    private var mediaId:       String = ""
    private var fileSize:      Long = -1
    //下载起始点，如果设置该值，就代表装个下载是支持断点续传，请调用维护好自身断点下载值
    private var downloadPos:   Long = -1
    private var encrypt:       Boolean = false
    private var cookie:        String = ""

    private var progressListener: MediaCenterHttpURLConnectionUtil.MediaProgressListener? = null


    companion object {

        fun newRequest(): DownloadFileParamsMaker {
            return DownloadFileParamsMaker()
        }
    }

    fun setDownloadId(downloadId: String): DownloadFileParamsMaker {
        this.downloadId = downloadId
        return this
    }

    fun setDownloadUrl(downloadUrl: String): DownloadFileParamsMaker {
        this.downloadUrl = downloadUrl
        return this
    }

    fun setDownloadPath(downloadPath: String): DownloadFileParamsMaker {
        this.downloadPath = downloadPath
        return this
    }

    fun setDownloadType(downloadType: MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE): DownloadFileParamsMaker {
        this.downloadType = downloadType
        return this
    }

    fun setFileSize(fileSize: Long): DownloadFileParamsMaker {
        this.fileSize = fileSize
        return this
    }

    fun setDownloadPos(downloadPos: Long): DownloadFileParamsMaker {
        this.downloadPos = downloadPos
        return this
    }

    fun setEncrypt(encrypt: Boolean): DownloadFileParamsMaker {
        this.encrypt = encrypt
        return this
    }

    fun setCookie(cookie: String): DownloadFileParamsMaker {
        this.cookie = cookie
        return this
    }

    fun setProgressListener(progressListener: MediaCenterHttpURLConnectionUtil.MediaProgressListener): DownloadFileParamsMaker {
        this.progressListener = progressListener
        return this
    }

    fun setMediaId(mediaId: String): DownloadFileParamsMaker {
        this.mediaId = mediaId
        return this
    }

    fun getDownloadId(): String {
        return this.downloadId
    }

    fun getDownloadUrl(): String {
        return this.downloadUrl
    }

    fun getDownloadPath(): String {
        return this.downloadPath
    }

    fun getDownloadPos(): Long {
        return this.downloadPos
    }

    fun getDownloadType(): MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE {
        return this.downloadType
    }

    fun getFileSize(): Long {
        return this.fileSize
    }

    fun getEncrypt(): Boolean {
        return this.encrypt
    }

    fun getCookie(): String {
        return this.cookie
    }

    fun getProgressListener(): MediaCenterHttpURLConnectionUtil.MediaProgressListener? {
        return this.progressListener
    }

    fun getMediaId(): String{
        return this.mediaId
    }
}