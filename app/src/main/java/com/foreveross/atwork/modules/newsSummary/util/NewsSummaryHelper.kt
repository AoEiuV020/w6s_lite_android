package com.foreveross.atwork.modules.newsSummary.util

import android.app.Activity
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import java.util.*

class NewsSummaryHelper {
    companion object{
        fun filtrateMessageList(chatPostMessageList: MutableList<ChatPostMessage>) {
            //若是服务号消息，则过滤服务号订阅消息
            val chatPostMessagesList: MutableList<ChatPostMessage> = ArrayList()
            for (chatPostMessage in chatPostMessageList) {
                if (chatPostMessage.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY) {
                    chatPostMessagesList.add(chatPostMessage)
                }
            }
            chatPostMessageList.removeAll(chatPostMessagesList)
        }

        fun selectApp(mActivity: Activity, newsSummaryRVData: NewsSummaryRVData, callBack: CallBack){
            val identifier = newsSummaryRVData.getChatId()
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"$identifier\"")
            if(sessionList.size > 0) {
                val session = sessionList[0]
                callBack.onResult(session,null)
            }else{
                var app: App? = null
                val appList = AppManager.getInstance().appList
                for(i in 0 until appList.size){
                    if(appList[i].mAppId == newsSummaryRVData.getChatId()){
                        app = appList[i]
                    }
                }
                if(app == null){
                    AppManager.getInstance().queryApp(mActivity, identifier, "", object : AppManager.GetAppFromMultiListener {
                        override fun onSuccess(app: App) {
                            callBack.onResult(null,app)
                        }

                        override fun networkFail(errorCode: Int, errorMsg: String?) {
                            callBack.onResult(null,null)
                        }

                    })
                }else{
                    callBack.onResult(null,app)

                }

            }
        }

    }

    //自定义接口
    interface CallBack {
        fun onResult(mSession: Session?,mApp: App?)
    }

    interface SessionItemColorCallBack{
        fun onResult(isAudio: Boolean,textStr: String)
    }
}