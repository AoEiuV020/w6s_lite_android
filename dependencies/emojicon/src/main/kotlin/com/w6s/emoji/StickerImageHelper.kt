package com.w6s.emoji

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import java.util.*

fun displayImageByMediaId(context: Context, mediaId: String, imageView: ImageView, options: DisplayImageOptions) {
    if (StringUtils.isEmpty(mediaId)) {
        imageView.setImageDrawable(options.getImageOnFail(imageView.resources))
        return
    }

    val url = getDisplayImageByMediaIdUrl(context, mediaId)
    displayImage(url, imageView, options)
}


fun getDisplayImageByMediaIdUrl(context: Context, mediaId: String): String {
    val loginInfo = LoginUserInfo.getInstance() ?: return mediaId

    var url = ""
    if (!TextUtils.isEmpty(mediaId)) {
        url = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), mediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    }
    return url
}

fun displayImage(uri: String, imageView: ImageView, options: DisplayImageOptions) {
    if (StringUtils.isEmpty(uri) || uri.startsWith("http") && !RequestRemoteInterceptor.checkLegal(uri)) {
        imageView.setImageDrawable(options.getImageOnFail(imageView.resources))
        return
    }

    val uriHandled = handleUri(uri)
    if (uriHandled.startsWith("file://")) {
        val tag = UUID.randomUUID().toString()

        imageView.tag = tag

        object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void): String {
                return EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(uri, false)
            }

            override fun onPostExecute(path: String) {
                if (null != imageView.tag && imageView.tag == tag) {
                    imageLoaderDisplayImage(imageView, "file://$path", options)

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)


    } else {
        imageLoaderDisplayImage(imageView, uriHandled, options)

    }
}

fun handleUri(uri: String): String {
    var uri = uri
    if (TextUtils.isEmpty(uri)) {
        return uri
    }
    if (!uri.startsWith("http") && !uri.startsWith("file:") && !uri.startsWith("assets:") && !uri.startsWith("drawable:")) {
        uri = "file://$uri"
    }
    return uri
}

fun imageLoaderDisplayImage(imageView: ImageView, uriHandled: String, options: DisplayImageOptions) {
    ImageLoader.getInstance().displayImage(uriHandled, imageView, options, object : ImageLoadingListener {
        override fun onLoadingStarted(imageUri: String, view: View) {}

        override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {
        }

        override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
        }

        override fun onLoadingCancelled(imageUri: String, view: View) {}
    })
}