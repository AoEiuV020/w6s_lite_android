package com.foreveross.atwork.modules.workbench.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreverht.db.service.repository.WorkbenchRepository
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker
import com.foreveross.atwork.api.sdk.workbench.WorkbenchSyncNetService
import com.foreveross.atwork.api.sdk.workbench.model.response.WorkbenchQueryListContentResponse
import com.foreveross.atwork.api.sdk.workbench.model.response.WorkbenchQueryShortcutCardContentResponse
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListContent
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardContent
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.aliyun.wrapCdnUrl
import com.foreveross.atwork.modules.web.util.UrlReplaceHelper

object WorkbenchCardContentManager : IWorkbenchCardContentManager {



    val cardContentRefreshSuccessfullyRecord = hashMapOf<String, Long>()
    val cardContentRequestRecord = hashMapOf<String, Long>()


    override fun clearRequestRecord() {
        cardContentRefreshSuccessfullyRecord.clear()
        cardContentRequestRecord.clear()
    }


    override fun removeRequestRecord(card: WorkbenchCard) {
        cardContentRefreshSuccessfullyRecord.remove(card.requestId)
        cardContentRequestRecord.remove(card.requestId)

    }

    override fun checkBannerMediaDownloadSilently(records: ArrayList<AdvertisementConfig>) {

        AsyncTaskThreadPool.getInstance().execute {

            val context = AtworkApplicationLike.baseContext
            val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)

            var newDownload = false
            records.forEach { advertisement ->


                if (!FileUtil.isExist(advertisement.getLocalBannerPath(context, currentOrgCode))) {

                    if (MediaCenterHttpURLConnectionUtil.getInstance().isDownloading(advertisement.mMediaId)) {
                        return@forEach
                    }

                    var downloadUrl = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), advertisement.mMediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
                    downloadUrl = wrapCdnUrl(downloadUrl)

                    val httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                            DownloadFileParamsMaker.newRequest().setDownloadId(advertisement.mMediaId)
                                    .setDownloadUrl(downloadUrl).setDownloadPath(advertisement.getLocalBannerPath(context, currentOrgCode)).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION)
                    )

                    if (httpResult.isNetSuccess) {
                        newDownload = true
                    }
                }
            }

            if (newDownload) {
                notifyRefreshAdvertisements(records)
            }

        }


    }


    @SuppressLint("StaticFieldLeak")
    override fun requestListContent(card: WorkbenchCard, immediate: Boolean, onGetResult: (Long, WorkbenchListContent?) -> Unit): Boolean {
        if (!checkRequestLegal(card, immediate)) {
            return false
        }

        cardContentRequestRecord[card.requestId] = System.currentTimeMillis()

        object : AsyncTask<Void, Void, WorkbenchListContent?>() {
            override fun doInBackground(vararg params: Void?): WorkbenchListContent? {

                if(!card.isCallbackUrlLegal()) {
                    return null;
                }

                var callbackUrl = card.callBackUrl


                val replaceResult = UrlReplaceHelper.replaceKeyParamsSync(callbackUrl)
                callbackUrl = replaceResult.url

                if(null == callbackUrl) {
                    return null

                }


                val httpResult = WorkbenchSyncNetService.queryWorkbenchListContent(callbackUrl)

                if (httpResult.isRequestSuccess) {
                    val items: List<WorkbenchListItem>? = (httpResult.resultResponse as WorkbenchQueryListContentResponse).result?.items

                    val cardContent = WorkbenchListContent(card.id, items)


                    //save db
                    if (!card.adminDisplay) {
                        WorkbenchRepository.insertOrUpdateWorkbenchCardContent(cardContent)
                    }

                    if (replaceResult.fullReplaced) {
                        cardContentRefreshSuccessfullyRecord[card.requestId] = System.currentTimeMillis()
                    }


                    return cardContent
                }



                return null
            }


            override fun onPostExecute(result: WorkbenchListContent?) {
                onGetResult(card.id, result)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

        return true


    }

    @SuppressLint("StaticFieldLeak")
    override fun requestShortcutCardContent(card: WorkbenchCard, immediate: Boolean, onGetResult: (Long, WorkbenchShortcutCardContent?) -> Unit): Boolean {

        if (!checkRequestLegal(card, immediate)) {
            return false
        }


        cardContentRequestRecord[card.requestId] = System.currentTimeMillis()

        object : AsyncTask<Void, Void, WorkbenchShortcutCardContent?>() {
            override fun doInBackground(vararg params: Void?): WorkbenchShortcutCardContent? {

                if(!card.isCallbackUrlLegal()) {
                    return null;
                }

                var callbackUrl = card.callBackUrl


                val replaceResult = UrlReplaceHelper.replaceKeyParamsSync(callbackUrl)
                callbackUrl = replaceResult.url

                if(null == callbackUrl) {
                    return null

                }

                val httpResult = WorkbenchSyncNetService.queryWorkbenchShortcutCardContent(callbackUrl)

                if (httpResult.isRequestSuccess) {


                    val items: List<WorkbenchShortcutCardItem>? = (httpResult.resultResponse as WorkbenchQueryShortcutCardContentResponse).result?.items
                    items?.forEachIndexed { index, workbenchShortcutCardItem ->
                        workbenchShortcutCardItem.requestId = card.requestId
                        workbenchShortcutCardItem.index = index
                    }


                    val cardContent = WorkbenchShortcutCardContent(card.id, items)


                    //save db
                    if (!card.adminDisplay) {
                        WorkbenchRepository.insertOrUpdateWorkbenchCardContent(cardContent)
                    }

                    if (replaceResult.fullReplaced) {
                        cardContentRefreshSuccessfullyRecord[card.requestId] = System.currentTimeMillis()
                    }

                    return cardContent

                }


                return null
            }


            override fun onPostExecute(result: WorkbenchShortcutCardContent?) {
                onGetResult(card.id, result)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

        return true
    }

    fun checkRequestLegal(card: WorkbenchCard, immediate: Boolean): Boolean {

        if(immediate) {
            return true
        }

        val lastContentRequestTime = cardContentRequestRecord[card.requestId]

        if (null == lastContentRequestTime) {
            return true
        }


        if(2 * 1000 > System.currentTimeMillis() - lastContentRequestTime) {
            return false
        }

        val lastRequestSuccessfullyTime = cardContentRefreshSuccessfullyRecord[card.requestId]
        if (null == lastRequestSuccessfullyTime) {
            return true
        }


        if(REQUEST_INTERVAL <= System.currentTimeMillis() - lastRequestSuccessfullyTime) {
            return true
        }

        return false
    }



    private fun notifyRefreshAdvertisements(advertisementConfigs: ArrayList<AdvertisementConfig>) {
        val intent = Intent(IWorkbenchCardContentManager.ACTION_REFRESH_WORKBENCH_BANNER_ADVERTISEMENTS)
        intent.putParcelableArrayListExtra(IWorkbenchCardContentManager.DATA_ADVERTISEMENTS, advertisementConfigs)

        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent)
    }


}