package com.foreveross.atwork.infrastructure.model.file


class VideoItem : MediaItem() {
    var duration: Long = -1

    fun isLegalInChat(): Boolean {
        return type.contains("mp4")
    }
}
