package com.foreveross.atwork.modules.app.manager.extension

import android.content.Context
import com.foreverht.cache.AppCache
import com.foreverht.db.service.repository.AppRepository
import com.foreveross.atwork.api.sdk.net.HttpResultException
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.extension.coroutineScope
import com.foreveross.atwork.modules.app.manager.AppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*


fun AppManager.queryAppBundle(context: Context, appId: String, bundleId: String, orgCode: String)
        = queryApp(context, appId, orgCode).map { app -> app.mBundles.find { it.id == bundleId } }

fun AppManager.queryApp(context: Context, appId: String, orgCode: String) = callbackFlow<App> {
    queryApp(context, appId, orgCode, object : AppManager.GetAppFromMultiListener {
        override fun onSuccess(app: App) {
            offer(app)
            close()
        }

        override fun networkFail(errorCode: Int, errorMsg: String?) {
            close(HttpResultException(errorCode, errorMsg))
        }

    })

    awaitClose()

}

fun AppManager.batchQueryAppBundlesLocal(context: Context, appIds: List<String>, bundleIds: List<String>, getBundles: (List<AppBundles>) -> Unit) {
    batchQueryAppBundlesLocal(context, appIds, bundleIds)
            .flowOn(Dispatchers.IO)
            .catch {  }
            .onEach { getBundles(it) }
            .launchIn(context.coroutineScope)
}

fun AppManager.batchQueryAppBundlesLocal(context: Context, appIds: List<String>, bundleIds: List<String>) = flow<List<AppBundles>> {
    val appListInCache = appIds.mapNotNull { AppCache.getInstance().getAppCache(it) }
    if(!ListUtil.isEmpty(appListInCache)) {
        emit(appListInCache.flatMap { it.mBundles }.filter { bundleIds.contains(it.mBundleId) })
    }

    if(appListInCache.size == appIds.size) {
        return@flow
    }

    val appIdsInCache = appListInCache.map { it.id }
    val localAppList = AppRepository.getInstance().queryAccessApps(appIds.filter { !appIdsInCache.contains(it) })
    if(!ListUtil.isEmpty(localAppList)) {
        emit(localAppList.flatMap { it.mBundles }.filter { bundleIds.contains(it.mBundleId) })
    }
}