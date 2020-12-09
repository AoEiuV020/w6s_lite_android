package com.foreveross.atwork.modules.advertisement.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.advertisement.AdvertisementNetSyncService
import com.foreveross.atwork.api.sdk.advertisement.BootAdvertisementService
import com.foreveross.atwork.api.sdk.advertisement.model.response.GetAdvertisesListResponse
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementEvent
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementKind
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementOpsType
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.aliyun.wrapCdnUrl
import com.foreveross.atwork.modules.advertisement.manager.IAdvertisementManager.Companion.ACTION_REFRESH_ADVERTISEMENTS
import com.foreveross.atwork.modules.advertisement.manager.IAdvertisementManager.Companion.DATA_KIND
import com.foreveross.atwork.modules.advertisement.manager.IAdvertisementManager.Companion.DATA_ORG_CODE
import com.foreveross.atwork.utils.ErrorHandleUtil

interface IAdvertisementManager {

    fun postAdvertisementEvent(advertisementEvent: AdvertisementEvent, listener: BootAdvertisementService.OnPostAdvertisementEventListener? = null)

    fun requestCurrentOrgBannerAdvertisementsSilently(advertisementKind: AdvertisementKind)

    fun checkCurrentOrgBannerMediaDownloadSilently(advertisementKind: AdvertisementKind)

    fun notifyRefreshAdvertisements(orgCode: String, advertisementKind: AdvertisementKind)

    fun updateAppTopBannerStatistics(mediaId: String, advertisementEvent: AdvertisementEvent)

    fun isCurrentOrgAdvertisementsLegal(advertisementKind: AdvertisementKind): Boolean

    fun getCurrentOrgAdvertisementsLegal(advertisementKind: AdvertisementKind): List<AdvertisementConfig>


    companion object {
        const val ACTION_REFRESH_ADVERTISEMENTS = "ACTION_REFRESH_ADVERTISEMENTS"
        const val DATA_ORG_CODE = "DATA_ORG_CODE"
        const val DATA_KIND = "DATA_KIND"
    }
}

object AdvertisementManager : IAdvertisementManager {

    override fun getCurrentOrgAdvertisementsLegal(advertisementKind: AdvertisementKind): List<AdvertisementConfig> {
        val context = AtworkApplicationLike.baseContext
        val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)
        val advertisementConfigs = OrgPersonalShareInfo.getInstance().getAppTopAdvertisements(context, currentOrgCode)
        return advertisementConfigs.filter {
            FileUtil.isExist(it.getLocalBannerPath(context, currentOrgCode))
                    && it.isValidDuration
        }

    }

    override fun isCurrentOrgAdvertisementsLegal(advertisementKind: AdvertisementKind): Boolean {
        return !getCurrentOrgAdvertisementsLegal(advertisementKind).isEmpty()
    }

    override fun updateAppTopBannerStatistics(mediaId: String, advertisementEvent: AdvertisementEvent) {

        val context = BaseApplicationLike.baseContext
        if (AdvertisementOpsType.Display.valueOfString() == advertisementEvent.opsType
                && PersonalShareInfo.getInstance().containsAppTopBannerDisplayRecord(context, mediaId)) {
            return
        }

        postAdvertisementEvent(advertisementEvent, object : BootAdvertisementService.OnPostAdvertisementEventListener {
            override fun networkFail(errorCode: Int, errorMsg: String?) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg)
            }

            override fun onPostSuccess() {
                PersonalShareInfo.getInstance().setDataAppTopBannerDisplayRecord(context, mediaId)
            }

        })
    }

    @SuppressLint("StaticFieldLeak")
    override fun requestCurrentOrgBannerAdvertisementsSilently(advertisementKind: AdvertisementKind) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val context = BaseApplicationLike.baseContext
                val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)

                val kindValue = when(advertisementKind) {
                    AdvertisementKind.APP_BANNER -> "banner"
                    AdvertisementKind.WORKBENCH_BANNER -> "widget"
                }


                val httpResult = AdvertisementNetSyncService.getAdvertisements(context, currentOrgCode, kindValue)
                if (httpResult.isRequestSuccess) {
                    val getAdvertisesListResponse = httpResult.resultResponse as GetAdvertisesListResponse

                    getAdvertisesListResponse.result?.apply {

                        val advertisesSorted = filter { kindValue == it.mKind }.sortedBy { it.mSort }

                        val advertisementsPrevious = OrgPersonalShareInfo.getInstance().getAdvertisements(context, currentOrgCode, advertisementKind)

                        OrgPersonalShareInfo.getInstance().setAdvertisements(context, currentOrgCode, advertisesSorted, advertisementKind)

                        //检查更新, 若有更新, 检查下载到本地
                        if (advertisementsPrevious != advertisesSorted) {
//                            notifyRefreshAdvertisements(currentOrgCode)
                            checkCurrentOrgBannerMediaDownloadSilently(advertisementKind)

                        }


                    }

                }

                return httpResult
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }


    override fun checkCurrentOrgBannerMediaDownloadSilently(advertisementKind: AdvertisementKind) {
        AsyncTaskThreadPool.getInstance().execute {
            val context = BaseApplicationLike.baseContext
            val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)

            val advertisements = OrgPersonalShareInfo.getInstance().getAppTopAdvertisements(context, currentOrgCode)
            var newDownload = false
            advertisements.forEach { advertisement ->


                if (!FileUtil.isExist(advertisement.getLocalBannerPath(context, currentOrgCode))) {

                    if (MediaCenterHttpURLConnectionUtil.getInstance().isDownloading(advertisement.mMediaId)) {
                        return@forEach
                    }

                    var downloadUrl = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), advertisement.mMediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
                    downloadUrl = wrapCdnUrl(downloadUrl)

                    val httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                            DownloadFileParamsMaker.newRequest().setDownloadId(advertisement.mMediaId).setDownloadUrl(downloadUrl)
                                    .setDownloadPath(advertisement.getLocalBannerPath(context, currentOrgCode)).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION)
                    )

                    if (httpResult.isNetSuccess) {
                        newDownload = true
                    }
                }
            }

            if (newDownload) {
                notifyRefreshAdvertisements(currentOrgCode, advertisementKind)
            }

        }
    }


    /**
     * 提交广告事件
     * @param advertisementEvent
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    override fun postAdvertisementEvent(advertisementEvent: AdvertisementEvent, listener: BootAdvertisementService.OnPostAdvertisementEventListener?) {

        if(!AtworkConfig.BEHAVIOR_LOG_CONFIG.isEnable) {
            return
        }

        object : AsyncTask<Void, Void, HttpResult>() {

            override fun doInBackground(vararg params: Void): HttpResult {

                val employee = AtworkApplicationLike.getLoginUserEmpSync(PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext))
                if (null != employee) {
                    advertisementEvent.positions = employee.positions
                }
                return AdvertisementNetSyncService.postAdvertisementEvent(BaseApplicationLike.baseContext, advertisementEvent)
            }

            override fun onPostExecute(httpResult: HttpResult) {
                listener?.apply {
                    if (httpResult.isRequestSuccess) {
                        onPostSuccess()
                        return
                    }

                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener)
                }


            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }


    override fun notifyRefreshAdvertisements(orgCode: String, advertisementKind: AdvertisementKind) {
        val intent = Intent(ACTION_REFRESH_ADVERTISEMENTS)
        intent.putExtra(DATA_ORG_CODE, orgCode)
        intent.putExtra(DATA_KIND, advertisementKind)
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent)
    }


}