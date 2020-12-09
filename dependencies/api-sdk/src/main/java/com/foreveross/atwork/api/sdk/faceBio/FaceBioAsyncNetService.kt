package com.foreveross.atwork.api.sdk.faceBio

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Base64
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.api.sdk.faceBio.requestModel.FaceBioFilmRequest
import com.foreveross.atwork.api.sdk.faceBio.requestModel.FaceBioToggle
import com.foreveross.atwork.api.sdk.faceBio.responseModel.IsFaceBioAuthResult
import com.foreveross.atwork.api.sdk.faceBio.responseModel.SetFaceBioFilmResult
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.infrastructure.plugin.face.OnFaceBioResultListener
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.BitmapUtil
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper
import com.google.gson.Gson
import java.io.File

object FaceBioAsyncNetService {

    //查询用户是否开启人脸功能
    fun isFaceBioAuthOpen(username: String, listener: OnFaceBioResultListener<IsFaceBioAuthResult>?) {
        object: AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                return FaceBioSyncNetService.isFaceBioAuthOpen(username)
            }

            override fun onPostExecute(result: HttpResult) {
                if (result == null) {
                    listener?.onFailure(-1, "")
                    return
                }
                if (result?.isRequestSuccess) {
                    listener?.onSuccess(result.resultResponse as IsFaceBioAuthResult)
                    return
                }
                listener?.onFailure(getFailCode(result), result.error)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }


    private fun getFailCode(httpResult: HttpResult): Int {
        if (httpResult == null) {
            return -1
        }
        if (httpResult.resultResponse == null) {
            return -2
        }
        return httpResult.resultResponse.status
    }

}