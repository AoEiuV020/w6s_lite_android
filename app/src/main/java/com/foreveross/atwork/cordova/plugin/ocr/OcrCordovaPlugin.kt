package com.foreveross.atwork.cordova.plugin.ocr

import android.Manifest
import android.app.Activity
import android.content.Intent
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.ocr.OcrSyncNetService
import com.foreveross.atwork.api.sdk.ocr.model.OcrApiRequest
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.cordova.plugin.WorkPlusCordovaPlugin
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util
import com.foreveross.atwork.modules.ocr.activity.OcrCameraActivity
import com.foreveross.atwork.modules.ocr.model.OcrRequestAction
import com.foreveross.atwork.modules.ocr.model.OcrResultResponse
import com.foreveross.atwork.utils.AtworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.cordova.CallbackContext
import org.json.JSONObject

const val ACTION_OCR_RECOGNIZE = "recognize"

class OcrCordovaPlugin: WorkPlusCordovaPlugin() {

    var callbackContext: CallbackContext? = null
    var currentRequest: OcrRequestAction? = null

    override fun execute(action: String, rawArgs: String, callbackContext: CallbackContext): Boolean {

        if(!requestCordovaAuth()){
            callbackContext.error("Cordova_Not_Auth")
            return true
        }

        when(action) {

            ACTION_OCR_RECOGNIZE -> {

                ocrRecognize(rawArgs, callbackContext)



                return true
            }
        }

        return false
    }

    private fun ocrRecognize(rawArgs: String, callbackContext: CallbackContext) {
        val ocrRequestAction: OcrRequestAction? = NetGsonHelper.fromCordovaJson(rawArgs, OcrRequestAction::class.java)

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.activity, arrayOf(Manifest.permission.CAMERA), object : PermissionsResultAction() {
            override fun onGranted() {
                this@OcrCordovaPlugin.callbackContext = callbackContext
                this@OcrCordovaPlugin.currentRequest = ocrRequestAction

                val intent = OcrCameraActivity.getStartOcrCameraIntent(cordova.activity, ocrRequestAction)
                cordova.startActivityForResult(this@OcrCordovaPlugin, intent, OcrCameraActivity.REQUEST_CODE)

            }

            override fun onDenied(permission: String) {
                AtworkUtil.popAuthSettingAlert(cordova.activity, permission)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if(Activity.RESULT_OK == resultCode) {
            if(OcrCameraActivity.REQUEST_CODE == requestCode) {
                val response = intent?.getParcelableExtra<OcrResultResponse>(OcrCameraActivity.DATA_OCR_RESPONSE_ACTION)

                if(null == response) {
                    callbackContext?.error()
                } else {

                    GlobalScope.launch(Dispatchers.IO) {

                        if(StringUtils.isEmpty(response.cropImg)) {
                            response.cropImg = Base64Util.encode(FileUtil.readFile(response.cropPath))
                        }

                        var progressLoading: ProgressDialogHelper? = null

                        var httpResult: HttpResult? = null

                        currentRequest?.let {
                            if (OcrApiRequest.TYPE_UKNOWN != it.recognizeType || !StringUtils.isEmpty(it.recognizeSuffixPath)) {

                                withContext(Dispatchers.Main) {
                                    progressLoading = ProgressDialogHelper(cordova.activity)
                                    progressLoading!!.show()
                                }

                                val cropImgBase64 = response.cropImg ?: StringUtils.EMPTY
                                httpResult = OcrSyncNetService().postOcrSync(BaseApplicationLike.baseContext, OcrApiRequest(it.recognizeType, it.recognizeSuffixPath, file = cropImgBase64))

                            }
                        }


                        withContext(Dispatchers.Main) {
                            progressLoading?.dismiss()

                            var jsonObject = JSONObject()

                            if(null != httpResult && httpResult!!.isNetSuccess) {

                                jsonObject.put("recognize_result", JSONObject(httpResult!!.result))
                                jsonObject.put("crop_path", response.cropPath)
                                jsonObject.put("crop_img", response.cropImg)

                            } else {
                                jsonObject.put("crop_path", response.cropPath)
                                jsonObject.put("crop_img", response.cropImg)

                            }


//                            LogUtil.e("ocr cordova 接口返回 -> $jsonObject")
                            callbackContext?.success(jsonObject)

                        }

                    }

                }

            }
        }
    }
}