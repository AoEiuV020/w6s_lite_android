package com.foreveross.atwork.api.sdk.net

object RequestRemoteInterceptor {

    private var interceptRequestIdData = hashMapOf<String, Long>()

    @JvmOverloads
    fun checkLegal(requestId: String, legalThreshold: Long = -1L): Boolean {

        if(-1L == legalThreshold) {
            return !interceptRequestIdData.containsKey(requestId)
        }

        interceptRequestIdData[requestId]?.let {
            return legalThreshold < System.currentTimeMillis() - it

        }

        return true
    }

    fun addInterceptRequestId(requestId: String) {
        interceptRequestIdData[requestId] = System.currentTimeMillis()

    }

    fun removeInterceptRequestId(requestId: String) {
        interceptRequestIdData.remove(requestId)
    }


    fun clear() {
        interceptRequestIdData.clear()
    }
}