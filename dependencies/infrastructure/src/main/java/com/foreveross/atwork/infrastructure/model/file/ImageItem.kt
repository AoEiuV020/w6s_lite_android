package com.foreveross.atwork.infrastructure.model.file

class ImageItem: MediaItem() {

    var height = -1

    var width = -1

    fun isGif(): Boolean {
        if(null != type && type.contains("gif")) {
            return true
        }
        return false
    }
}