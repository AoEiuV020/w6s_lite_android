package com.foreveross.atwork.modules.workbench.component

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchImageType
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.utils.ImageCacheHelper
import com.foreveross.atwork.utils.ImageViewUtil
import com.nostra13.universalimageloader.core.DisplayImageOptions

interface IWorkbenchCardRefreshView<T : WorkbenchCard> {

    var workbenchCard: T

    fun refresh(workbenchCard: T)

    fun refreshView(workbenchCard: T)

    fun refreshContentRemote(immediate: Boolean = false) {

    }

    fun refreshWorkbenchData(workbenchData: WorkbenchData) {
    }


    fun getViewType(): Int


}

fun refreshIconView(imageView: ImageView, iconTypeStr: String?, iconValue: String?, holdingRes: Int) {


    if (StringUtils.isEmpty(iconValue)) {
//        imageView.visibility = View.INVISIBLE

        imageView.setImageResource(holdingRes)
        ImageCacheHelper.resetImageViewMediaIdLoaded(imageView)


        return
    }


    if (ImageCacheHelper.isImageViewMediaIdLoaded(imageView, iconValue)) {
        return
    }


    imageView.visibility = View.VISIBLE

    val iconType = WorkbenchImageType.parse(iconTypeStr)

    when (iconType) {
        WorkbenchImageType.URL -> refreshIconViewUrl(imageView, iconValue, holdingRes)
        WorkbenchImageType.MEDIAID -> refreshIconViewMediaId(imageView, iconValue, holdingRes)
        WorkbenchImageType.INNER -> refreshIconViewInner(imageView, iconValue, holdingRes)
    }


}

private fun refreshIconViewUrl(imageView: ImageView, iconValue: String?, holdingRes: Int) {
    ImageCacheHelper.displayImage(iconValue, imageView, getLoadingOptions(holdingRes, holdingRes), object : ImageCacheHelper.ImageLoadedListener {
        override fun onImageLoadedComplete(bitmap: Bitmap?) {
            ImageCacheHelper.setImageViewMediaIdLoaded(imageView, iconValue!!)
        }

        override fun onImageLoadedFail() {
            ImageCacheHelper.resetImageViewMediaIdLoaded(imageView)
        }

    })
}

private fun refreshIconViewMediaId(imageView: ImageView, iconValue: String?, holdingRes: Int) {
    ImageCacheHelper.displayImageByMediaId(iconValue, imageView, getLoadingOptions(holdingRes, holdingRes), object : ImageCacheHelper.ImageLoadedListener {
        override fun onImageLoadedComplete(bitmap: Bitmap?) {
            ImageCacheHelper.setImageViewMediaIdLoaded(imageView, iconValue!!)

        }

        override fun onImageLoadedFail() {
            ImageCacheHelper.resetImageViewMediaIdLoaded(imageView)
        }

    })
}

private fun refreshIconViewInner(imageView: ImageView, iconValue: String?, holdingRes: Int) {
    val resId = ImageViewUtil.getResourceInt(iconValue)
    if (-1 == resId) {
        imageView.setImageResource(holdingRes)
    } else {
        imageView.setImageResource(resId)

    }
}

private fun getLoadingOptions(resId: Int, loadingId: Int): DisplayImageOptions {
    val builder = DisplayImageOptions.Builder()
    builder.cacheOnDisk(true)
    builder.cacheInMemory(true)
    if (-1 == loadingId) {
        builder.showImageOnLoading(null)
    } else {
        builder.showImageOnLoading(loadingId)
    }

    if (-1 != resId) {
        builder.showImageForEmptyUri(resId)
        builder.showImageOnFail(resId)
    }
    return builder.build()
}
