package com.foreveross.atwork.modules.newsSummary.util

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.foreverht.db.service.repository.MessageAppRepository
import com.foreverht.db.service.repository.UnreadSubcriptionMRepository
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage

class CheckUnReadUtil {
    companion object{
        @SuppressLint("StaticFieldLeak")
        fun CompareTime(appId: String,deliveryTime: Long){
            object : AsyncTask<Void?, Void?, Boolean>() {

                override fun doInBackground(vararg p0: Void?): Boolean {
                    val unReadDataList = UnreadSubcriptionMRepository.getInstance().queryByAppId(appId)
                    return if(unReadDataList.size > 0) {
                        unReadDataList[0].deliveryTime.toLong() <= deliveryTime
                    }else{
                        false
                    }
                }

                override fun onPostExecute(isFirstNews: Boolean) {
                    if(isFirstNews){
                        UnreadSubcriptionMRepository.getInstance().removeByAppId(appId)
                    }
                }

            }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
        }

        @SuppressLint("StaticFieldLeak")
        fun CompareTimeById(appId: String,deliveryId: String){
            object : AsyncTask<Void?, Void?, Boolean>() {

                override fun doInBackground(vararg p0: Void?): Boolean {
                    val lists: List<NewsSummaryPostMessage> = MessageAppRepository.getInstance().queryMessagesByMsgId(BaseApplicationLike.baseContext, deliveryId)
                    if (lists.isEmpty()) {
                        return false
                    }
                    val unReadDataList = UnreadSubcriptionMRepository.getInstance().queryByAppId(appId)
                    return if(unReadDataList.size > 0) {
                        unReadDataList[0].deliveryTime.toLong() <= lists[0].getChatPostMessage().deliveryTime
                    }else{
                        false
                    }
                }

                override fun onPostExecute(isFirstNews: Boolean) {
                    if(isFirstNews){
                        UnreadSubcriptionMRepository.getInstance().removeByAppId(appId)
                    }
                }

            }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
        }
    }
}