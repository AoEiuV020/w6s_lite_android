package com.foreveross.atwork.infrastructure.manager.contentObserver

import android.database.ContentObserver
import com.foreveross.atwork.infrastructure.manager.FileAlbumService
import com.foreveross.atwork.infrastructure.utils.LogUtil

class GalleryObserverExternal : ContentObserver(null) {

    override fun onChange(selfChange: Boolean) {

        LogUtil.e(FileAlbumService.TAG, "GalleryObserverExternal   onChange")

        FileAlbumService.getInstance().refreshAllBucketList()
    }

}