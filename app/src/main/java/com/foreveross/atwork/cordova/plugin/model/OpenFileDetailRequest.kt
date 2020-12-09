package com.foreveross.atwork.cordova.plugin.model

import com.foreveross.atwork.infrastructure.model.file.FileData
import com.foreveross.atwork.infrastructure.model.file.FileStatusInfo
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class OpenFileDetailRequest(

        @SerializedName("fileName")
        var fileName: String? = null,

        @SerializedName("mediaId")
        var fileMediaId: String? = null,

        @SerializedName("fileSize")
        var fileSize: Long = -1,


        @SerializedName("isImage")
        var isImage: Boolean = false,

        @SerializedName("filePath")
        var filePath: String? = null,

        @SerializedName("needActionMore")
        var needActionMore: Boolean = true,

        private var progress: Int = 0,

        private var fileStatus: FileStatus = FileStatus.NOT_DOWNLOAD

) : FileStatusInfo {


    fun checkLegal(): Boolean {
        if (StringUtils.isEmpty(fileName))
            return false

        if(!StringUtils.isEmpty(filePath)) {
            if(StringUtils.isEmpty(mediaId) && !FileUtil.isExist(filePath)) {
                return false
            }

            return true
        }

        if (StringUtils.isEmpty(mediaId))
            return false

        if (0 >= fileSize)
            return false

        return true
    }

    fun isImgType(): Boolean {
        if (isImage) {
            return true
        }

        val fileType = fileType
        return FileData.FileType.File_Image === fileType || FileData.FileType.File_Gif === fileType
    }

    fun isGifType(): Boolean {
        val fileType = fileType
        return FileData.FileType.File_Gif === fileType
    }

    override fun getKeyId(): String? {
        return fileMediaId
    }

    override fun getName(): String? {
        return fileName
    }

    override fun getPath(): String? {
        return filePath
    }

    override fun setPath(path: String?) {
        this.filePath = path
    }

    override fun getSize(): Long {
        return fileSize
    }

    override fun getFileStatus(): FileStatus? {
        return fileStatus
    }

    override fun setFileStatus(fileStatus: FileStatus) {
        this.fileStatus = fileStatus
    }

    override fun getMediaId(): String? {
        return fileMediaId
    }

    override fun getProgress(): Int {
        return progress
    }

    override fun setProgress(progress: Int) {
        this.progress = progress
    }

    override fun getFileType(): FileData.FileType {
        return FileData.getFileType(fileName)
    }

    override fun needActionMore(): Boolean {
        return needActionMore
    }

    /**
     * 是否遵循 workplus 聊天文件 域设置规则
     */
    override fun needW6sChatFileBehavior(): Boolean {
        return false
    }
}