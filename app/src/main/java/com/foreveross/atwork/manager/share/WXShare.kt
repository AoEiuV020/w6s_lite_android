package com.foreveross.atwork.manager.share

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.share.ExternalShareType
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.BitmapUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util
import com.foreveross.atwork.manager.share.model.WxShareType
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ImageCacheHelper
import com.tencent.mm.sdk.modelmsg.*
import com.tencent.mm.sdk.openapi.IWXAPI
import com.tencent.mm.sdk.openapi.WXAPIFactory


abstract class WXShare(var activity: Activity, appId: String?) : ExternalShareType {


    protected lateinit var api: IWXAPI

    init {
        regToWX(appId)
    }


    private fun regToWX(appId: String?) {
        var appId = appId
        if(StringUtils.isEmpty(appId)) {
            appId = AtworkConfig.WX_APP_ID
        }
        api = WXAPIFactory.createWXAPI(activity, appId)
        if (!api.isWXAppInstalled) {
            //提醒用户没有按照微信
            AtworkToast.showToast(activity.getString(R.string.wx_not_avaliable))
            return
        }
        api.registerApp(appId)

    }

    override fun shareMessage(articleItem: ArticleItem) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {

            val webpageObject = WXWebpageObject()
            webpageObject.webpageUrl = articleItem.url

            val msg = WXMediaMessage(webpageObject)
            msg.title = articleItem.title
            msg.description = articleItem.summary

            if (articleItem.isCoverUrlFromBase64) {
                var base64ImgData = articleItem.coverByteBase64
                doShareToWxFromBase64ImgMedia(base64ImgData, msg)


            } else {
                val imageUrl = ArticleItemHelper.getCoverUrl(articleItem)
                doShareToWxFromHttpImgMedia(imageUrl, msg)
            }

        }

    }

    override fun shareTxt(shareContent: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val wxTextObject = WXTextObject()
            wxTextObject.text = shareContent
            val msg = WXMediaMessage()
            msg.mediaObject = wxTextObject
//                设置描述
            msg.description = shareContent

            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction(WxShareType.TXT.toString().toLowerCase())
            req.message = msg
            req.scene = getScene()
            api.sendReq(req)

        }

    }

    override fun shareImage(imageData: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {

            val wxImageObject = WXImageObject()
            val msg = WXMediaMessage(wxImageObject)


            if(imageData.startsWith("base64://")) {
                val imageDataByte = Base64Util.decode(imageData.substring("base64://".length))
                doShareToWxFromBase64ImgMedia(imageDataByte, msg)

            } else if(imageData.startsWith("http://") || imageData.startsWith("https://")){
                doShareToWxFromHttpImgMedia(imageData, msg)
            }

        }




    }

    abstract fun getScene(): Int


    private fun doShareToWxFromBase64ImgMedia(base64ImgData: ByteArray, msg: WXMediaMessage) {

        val type = when(msg.mediaObject) {
            is WXWebpageObject -> WxShareType.WEBPAGE
            else -> WxShareType.IMAGE
        }

        if (base64ImgData.isEmpty()) {
            val bitmap = BitmapFactory.decodeResource(activity.resources, R.mipmap.default_link)
            msg.thumbData = BitmapUtil.compressImageForQuality(bitmap, AtworkConfig.CHAT_THUMB_SIZE)

        } else {

            if(msg.mediaObject is WXImageObject) {
                (msg.mediaObject as WXImageObject).imageData = base64ImgData
            }

            msg.thumbData = BitmapUtil.compressImageForQuality(BitmapUtil.Bytes2Bitmap(base64ImgData), AtworkConfig.CHAT_THUMB_SIZE)
        }


        doShareToWX(msg, type)
    }

    private fun doShareToWxFromHttpImgMedia(imageUrl: String?, msg: WXMediaMessage) {

        val type = when(msg.mediaObject) {
            is WXWebpageObject -> WxShareType.WEBPAGE
            else -> WxShareType.IMAGE
        }

        ImageCacheHelper.loadImageByUrl(imageUrl, object : ImageCacheHelper.ImageLoadedListener {
            override fun onImageLoadedComplete(bitmapResult: Bitmap?) {
                var bitmap = bitmapResult
                if (null != bitmap) {
                    if(msg.mediaObject is WXImageObject) {
                        (msg.mediaObject as WXImageObject).imageData = BitmapUtil.Bitmap2Bytes(bitmap, false)
                    }

                    msg.thumbData = BitmapUtil.compressImageForQuality(bitmap, AtworkConfig.CHAT_THUMB_SIZE)
                } else {
                    bitmap = BitmapFactory.decodeResource(activity.resources, R.mipmap.default_link)
                    msg.thumbData = BitmapUtil.compressImageForQuality(bitmap, AtworkConfig.CHAT_THUMB_SIZE)
                }


                doShareToWX(msg, type)

            }

            override fun onImageLoadedFail() {
                LogUtil.e("wx share~~~ but bitmap parse failed,  url : $imageUrl")
                val bitmap = BitmapFactory.decodeResource(activity.resources, R.mipmap.default_link)
                msg.thumbData = BitmapUtil.compressImageForQuality(bitmap, AtworkConfig.CHAT_THUMB_SIZE)

                doShareToWX(msg, type)

            }
        })
    }

    private fun doShareToWX(msg: WXMediaMessage, type: WxShareType) {
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction(type.toString().toLowerCase())
        req.message = msg
        req.scene = getScene()
        api.sendReq(req)
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }
}