@file: JvmName("MediaSelectHelper")
package com.foreveross.atwork.modules.image.util

import com.foreveross.atwork.infrastructure.model.file.ImageItem
import com.foreveross.atwork.infrastructure.model.file.MediaItem
import com.foreveross.atwork.infrastructure.model.file.VideoItem

fun isStaticImage(mediaItem: MediaItem): Boolean {
    if(mediaItem is VideoItem) {
        return false
    }

    if(mediaItem is ImageItem && mediaItem.isGif()) {
        return false
    }

    return true
}