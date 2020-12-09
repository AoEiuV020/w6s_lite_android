@file: JvmName("CdnHelper")

package com.foreveross.atwork.infrastructure.utils.aliyun


import android.net.Uri
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.support.cdn.CdnProducer
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils
import java.util.*

const val TAG = "CDN"

fun wrapCdnUrl(mediaUrl: String): String {

    if(!AtworkConfig.CDN_CONFIG.enable) {
        return mediaUrl
    }

    if(!mediaUrl.contains(AtworkConfig.CDN_CONFIG.mediaUrl)) {
        return mediaUrl
    }

    when(AtworkConfig.CDN_CONFIG.producer) {
        CdnProducer.ALIYUN -> {
            when(AtworkConfig.CDN_CONFIG.type) {

                "AUTH_A" -> return wrapCdnUrlOnAliyunAuthA(mediaUrl)
            }
        }
    }

    return mediaUrl

}

private fun wrapCdnUrlOnAliyunAuthA(mediaUrl: String): String {
    val aliyunKey = AtworkConfig.CDN_CONFIG.key

    val timestamp = TimeUtil.getCurrentTimeInMillis() / 1000L + AtworkConfig.CDN_CONFIG.expireDuration
    val rand = UUID.randomUUID().toString().replace("-", StringUtils.EMPTY)
    val uid = 0

    val filename = Uri.parse(mediaUrl).path

    val hashvalue = MD5Utils.encoderByMd5("$filename-$timestamp-$rand-$uid-$aliyunKey")

    val authKey = "$timestamp-$rand-$uid-$hashvalue"

    val cdnUrl = UrlHandleHelper.addParam(mediaUrl, "auth_key", authKey)

    LogUtil.e(TAG, "filename ->  $filename")
    LogUtil.e(TAG, "mediaUrl ->  $mediaUrl")
    LogUtil.e(TAG, "hashvalue ->  $hashvalue")
    LogUtil.e(TAG, "cdnUrl ->  $cdnUrl")

    return cdnUrl
}


fun wrapCdnUrlTest(mediaUrl: String): String {
    val aliyunKey = "aliyuncdnexp1234"
    val domain = "http://cdn.example.com"

//    val timestamp = TimeUtil.getCurrentTimeInMillis() / 1000L + 30 * 60
    val timestamp = 1444435200
    val rand = 0
    val uid = 0


    val filename = mediaUrl.substringAfter(domain)

    val hashvalue = MD5Utils.encoderByMd5("$filename-$timestamp-$rand-$uid-$aliyunKey")

    val authKey = "$timestamp-$rand-$uid-$hashvalue"

    val cdnUrl = UrlHandleHelper.addParam(mediaUrl, "auth_key", authKey)

    LogUtil.e(TAG,"filename ->  $filename")
    LogUtil.e(TAG,"mediaUrl ->  $mediaUrl")
    LogUtil.e(TAG, "hashvalue ->  $hashvalue")
    LogUtil.e(TAG, "cdnUrl ->  $cdnUrl")

    return cdnUrl
}