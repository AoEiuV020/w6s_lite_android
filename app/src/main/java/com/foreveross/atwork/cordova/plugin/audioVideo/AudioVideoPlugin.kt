package com.foreveross.atwork.cordova.plugin.audioVideo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.cordova.plugin.WorkPlusCordovaPlugin
import com.foreveross.atwork.cordova.plugin.audioVideo.model.TranslateAudioCordovaRequest
import com.foreveross.atwork.cordova.plugin.audioVideo.model.TranslateAudioCordovaResponse
import com.foreveross.atwork.cordova.plugin.audioVideo.model.VideoCordovaResponse
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util
import com.foreveross.atwork.modules.common.fragment.VoiceRecordDialogFragment
import com.foreveross.atwork.modules.video.activity.VideoRecordActivity
import com.foreveross.atwork.modules.video.model.VideoRecordRequestAction
import com.foreveross.atwork.modules.video.model.VideoRecordResponse
import com.foreveross.atwork.utils.AtworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.cordova.CallbackContext
import org.apache.cordova.PluginResult
import org.json.JSONObject
import org.telegram.messenger.MediaController
import java.io.File
import java.util.*

class AudioVideoPlugin : WorkPlusCordovaPlugin() {

    companion object {
        /**开始录音*/
        const val START_AUDIO = "voiceRecord"
        /**开始播放录音*/
        const val START_PLAYER_AUDIO = "startAudioPlayer"
        /**开始录制视频*/
        const val START_VIDEO = "startVideoRecoder"

        const val TRANSLATE_AUDIO = "translateAudio"

        const val REQUEST_CODE_SYSTEM_VIDEO_RECORD_VIDEO = 1
    }

    var callbackCtx: CallbackContext? = null
    /**压缩之前的视频路径*/
    private var videoFilePath:String? = null
    /**压缩之后的视频路径*/
    private var videoCompressFilePath:String? = null

    override fun execute(action: String, rawArgs: String, callbackContext: CallbackContext): Boolean {

        if(!requestCordovaAuth()){
            callbackContext.error("Cordova_Not_Auth")
            return true;
        }

        callbackCtx = callbackContext

        when (action) {
            START_AUDIO -> {
                voice(callbackContext!!)
                return true
            }

            START_PLAYER_AUDIO -> { }
            START_VIDEO -> {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.activity, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA), object : PermissionsResultAction() {
                    override fun onGranted() {
                        val videoRecordRequestAction: VideoRecordRequestAction? = NetGsonHelper.fromCordovaJson(rawArgs, VideoRecordRequestAction::class.java)

                        w6sVideo(videoRecordRequestAction)
//                        systemVideo()
                    }

                    override fun onDenied(permission: String) {
                        AtworkUtil.popAuthSettingAlert(cordova.activity, permission)
                    }
                })
                return true
            }

            TRANSLATE_AUDIO -> {
                val translateAudioCordovaRequest: TranslateAudioCordovaRequest? = NetGsonHelper.fromCordovaJson(rawArgs, TranslateAudioCordovaRequest::class.java)
                if(null == translateAudioCordovaRequest || !translateAudioCordovaRequest.legal()) {
                    callbackContext.error()

                } else {
                    if(!StringUtils.isEmpty(translateAudioCordovaRequest.mediaId)) {
                        //translate to mp3

                    } else if(!StringUtils.isEmpty(translateAudioCordovaRequest.filePath)) {
                        cordova.threadPool.execute {
                            //upload server first
                            val mediaId = MediaCenterHttpURLConnectionUtil.getInstance().syncUploadFileAndGetMediaId(cordova.activity, translateAudioCordovaRequest.filePath, false)

                            val mediaUrl = String.format(UrlConstantManager.getInstance().mp3Voice, mediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(AtworkApplicationLike.baseContext))

                            val savePath = AtWorkDirUtils.getInstance().getFiles(cordova.activity) + System.currentTimeMillis() + ".mp3"


                            //then transfer and download
                            val httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                                    DownloadFileParamsMaker.newRequest().setDownloadId(UUID.randomUUID().toString()).setDownloadUrl(mediaUrl).setDownloadPath(savePath)
                            )


                            AtworkApplicationLike.runOnMainThread {

                                if(httpResult.isNetSuccess) {
                                    val translateAudioCordovaResponse = TranslateAudioCordovaResponse(voicePath = savePath)
                                    val jsonObject = JSONObject()
                                    jsonObject.put("code",0)
                                    jsonObject.put("message","语音转换成功")
                                    jsonObject.put("info", JSONObject(JsonUtil.toJson(translateAudioCordovaResponse)))
                                    callbackContext.success(jsonObject)

                                } else {
                                    val jsonObject = JSONObject()
                                    jsonObject.put("code",-1)
                                    jsonObject.put("message","语音转换失败")
                                    callbackContext.error(jsonObject)
                                }
                            }
//                            if(!StringUtils.isEmpty(mediaId)) {
//                                val mp3MediaIdResult = DropboxSyncNetService.getInstance().translateFile(cordova.activity, mediaId, "amr", "mp3")
//
//                                if(mp3MediaIdResult.isRequestSuccess) {
//                                    val fileTranslateResponseJson = mp3MediaIdResult.resultResponse as FileTranslateResponseJson
//
//                                    LogUtil.e("mp3MediaId  ${fileTranslateResponseJson.mDestIds }")
//
//                                }
//                            }
                        }



                    }

                }

                return true

            }
        }
        return false
    }



    private fun voice(callbackContext: CallbackContext) {
        val fragment = cordova.fragment
        if (null == fragment) {
            callbackContext.error()
            return
        }

        val fragmentManager = fragment.fragmentManager
        if (null == fragmentManager) {
            callbackContext.error()
            return
        }

        val voiceRecordDialogFragment = VoiceRecordDialogFragment()
        voiceRecordDialogFragment.setOnlyRecord(true)
        voiceRecordDialogFragment.setPublishAction { result ->
            if (null == result) {
                val jsonObject = JSONObject()
                jsonObject.put("code",-1)
                jsonObject.put("message","语音录制失败")
                jsonObject.put("info","")
                val pluginResult = PluginResult(PluginResult.Status.ERROR, jsonObject)
                callbackCtx!!.sendPluginResult(pluginResult)
                callbackCtx!!.error()
            } else {
                val jsonObject = JSONObject()
                jsonObject.put("code",0)
                jsonObject.put("message","语音录制成功")
                val jsonValue = JSONObject()
                jsonValue.put("voice_path",result)
                jsonObject.put("info",jsonValue)
                val pluginResult = PluginResult(PluginResult.Status.OK, jsonObject)
                callbackCtx!!.sendPluginResult(pluginResult)
                callbackCtx!!.success()

                LogUtil.e("voice result $jsonObject")

            }

            voiceRecordDialogFragment.dismiss()

            Unit
        }

        voiceRecordDialogFragment.show(fragmentManager, "voiceRecordDialogPop")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && null != intent) {
            when(requestCode) {
                REQUEST_CODE_SYSTEM_VIDEO_RECORD_VIDEO -> handleSystemVideoRecord()
                VideoRecordActivity.REQUEST_CODE ->  handleW6sVideoRecord(intent)
            }

        } else {
            val videoCordovaResponse = VideoCordovaResponse(code = -1, message = "视频录制失败", info = null)
            callbackCtx?.success(videoCordovaResponse)
        }
    }

    private fun w6sVideo(videoRecordRequestAction: VideoRecordRequestAction?) {
        val intent = VideoRecordActivity.getStartVideoRecordIntent(cordova.activity, videoRecordRequestAction)
        cordova.startActivityForResult(this@AudioVideoPlugin, intent, VideoRecordActivity.REQUEST_CODE)
    }

    private fun systemVideo() {
        videoFilePath = AtWorkDirUtils.getInstance().getAUDIO(cordova.activity) + "test" + ".mp4"
        val uri = Uri.fromFile(File(videoFilePath!!))
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
//        cordova.activity.startActivityForResult(intent, REQUEST_CODE_SYSTEM_VIDEO_RECORD_VIDEO)
        cordova.startActivityForResult(this,intent, REQUEST_CODE_SYSTEM_VIDEO_RECORD_VIDEO)
    }


    private fun handleW6sVideoRecord(intent: Intent?) {
        val recordInfo = intent?.getParcelableExtra<VideoRecordResponse>(VideoRecordActivity.DATA_VIDEO_RECORD_RESPONSE)
        if(null != recordInfo && !StringUtils.isEmpty(recordInfo.videoPath)) {


            GlobalScope.launch(Dispatchers.IO) {

                val coverBitmap = ThumbnailUtils.createVideoThumbnail(recordInfo.videoPath, MediaStore.Images.Thumbnails.MINI_KIND)
                recordInfo.videoCover = Base64Util.encode(BitmapUtil.Bitmap2Bytes(coverBitmap, true))

                if(recordInfo.syncSystemAlbum) {
                    val savedPath = MicroVideoHelper.saveVideoToGalleryAndGetPath(cordova.activity, null, recordInfo.videoPath)

                }

                withContext(Dispatchers.Main) {
                    val videoCordovaResponse = VideoCordovaResponse(code = 0, message = "视频录制成功", info = recordInfo)
                    callbackCtx?.success(videoCordovaResponse)
                }


            }



        } else {
            GlobalScope.launch(Dispatchers.Main) {
                val videoCordovaResponse = VideoCordovaResponse(code = -1, message = "视频录制失败", info = null)
                callbackCtx?.success(videoCordovaResponse)
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private fun handleSystemVideoRecord() {
        if (videoFilePath != null) {
            videoCompressFilePath = AtWorkDirUtils.getInstance().getFiles(cordova.activity) + System.currentTimeMillis() + ".mp4"
            LogUtil.e("压缩", videoCompressFilePath)
            val dialogHelper = ProgressDialogHelper(cordova.activity)
            dialogHelper.show("正在压缩")
            object : AsyncTask<Void, Void, Boolean>() {
                override fun doInBackground(vararg params: Void): Boolean? {

                    LogUtil.e("原始视频大小: ${FileUtil.getSize(videoFilePath)}")

                    val beginTime = System.currentTimeMillis()

                    val result = MediaController().convertVideo(videoFilePath, -1, -1, videoCompressFilePath, 720, 1280, 0, 512000)
                    LogUtil.e("压缩", if (result) "视频压缩成功" else "视频压缩失败")


                    LogUtil.e("压缩时间 : ${System.currentTimeMillis() - beginTime}")
                    return result
                }

                override fun onPostExecute(suc: Boolean) {
                    dialogHelper.dismiss()
                    if (suc && videoCompressFilePath != null && callbackCtx != null) {
                        val file = File(videoFilePath)
                        file.delete()

                        val jsonObject = JSONObject()
                        jsonObject.put("code", 0)
                        jsonObject.put("message", "视频录制成功")
                        val jsonValue = JSONObject()
                        jsonValue.put("video_path", videoCompressFilePath)
                        jsonObject.put("info", jsonValue)
                        val pluginResult = PluginResult(PluginResult.Status.OK, jsonObject)
                        callbackCtx!!.sendPluginResult(pluginResult)
                        callbackCtx!!.success()
                    } else {
                        val jsonObject = JSONObject()
                        jsonObject.put("code", -1)
                        jsonObject.put("message", "视频录制失败")
                        jsonObject.put("info", "")
                        val pluginResult = PluginResult(PluginResult.Status.ERROR, jsonObject)
                        callbackCtx!!.sendPluginResult(pluginResult)
                        callbackCtx!!.error()
                    }
                }
            }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

        }
    }

}