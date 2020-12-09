package com.foreveross.atwork.infrastructure.model.file

import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*

/**
 * 一个目录的相册对象
 */
class MediaBucket {

    var identifier: String = UUID.randomUUID().toString()

    var count = 0

    var bucketName: String = StringUtils.EMPTY

    var mediaList: List<MediaItem> = arrayListOf()




}
